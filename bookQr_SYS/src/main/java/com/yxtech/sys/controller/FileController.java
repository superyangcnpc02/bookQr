package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultString;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.Res;
import com.yxtech.sys.service.FileResService;
import com.yxtech.sys.service.QrDownRankService;
import com.yxtech.sys.service.ResService;
import com.yxtech.sys.service.ZhixueTokenService;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.NameUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.qr.HttpTookit;
import jodd.datetime.JDateTime;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/10/9.
 */
@RestController("fileController")
@Scope("prototype")
@RequestMapping(value = "/file")
public class FileController {
    public final static Logger log = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileResService fileResService;
    @Autowired
    private ResService resService;
    @Autowired
    private QrDownRankService qrDownRankService;
    @Autowired
    private ZhixueTokenService zhixueTokenService;
    /**
     * 获取智学云上传TOKEN
     */
    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public JsonResultString zipDownload()throws Exception{

        try {
            String token = this.zhixueTokenService.getToken();
            return new JsonResultString(true,"",token);
        } catch (Exception e) {
            log.error("获取智学云token失败！",e);
            return new JsonResultString(false,"获取智学云token失败!");
        }

    }


    /**
     * 上传文件
     * @param crunchifyFiles
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "/upload")
//    @RequiresPermissions("/file/upload")
    public Map<String, Object> upload(@RequestParam("file") List<MultipartFile> crunchifyFiles,
                               HttpServletRequest request)throws IllegalStateException, IOException {
        //生成上传文件的路径信息，按天生成
        String savePath = Constant.FILE_PATH + File.separator + new JDateTime().toString("YYYYMMDD");
        String saveDirectory = PathUtil.getAppRootPath(request) + savePath;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }


        List<String> uuids = new ArrayList<String>();
        if (null != crunchifyFiles && crunchifyFiles.size() > 0) {
            for (MultipartFile multipartFile : crunchifyFiles) {

                String fileName = multipartFile.getOriginalFilename();

                if (!"".equalsIgnoreCase(fileName)) {
                    //获取文件属性
                    FileRes file = new FileRes();
                    String name=NameUtil.getFileNameNoEx(fileName);
                    if(name.length()>50) {
                        name = name.substring(0, 50);
                    }
                    file.setName(name);
                    file.setSuffix(NameUtil.getExtensionName(fileName));
                    String uuid = UUID.randomUUID().toString();
                    file.setUuid(uuid);
                    file.setPath(savePath + File.separator + uuid + "." + file.getSuffix());
                    file.setSize(Integer.parseInt("" + multipartFile.getSize()));
                    file.setCreateTime(new Date());
                    //实际将文件存储到服务器
                    multipartFile
                            .transferTo(new File(saveDirectory, uuid + "." + file.getSuffix()));

                    //ansi格式的txt转换成utf-8
                    String suffix=file.getSuffix();
                    if("txt".equals(suffix)){
                        String enCoding=FileUtil.getCharset(new File(saveDirectory, uuid + "." + file.getSuffix()));
                        FileUtil.change(new File(saveDirectory, uuid + "." + file.getSuffix()),enCoding);
                    }

                    uuids.add(uuid);
                    //保存文件信息到数据库
                    this.fileResService.insertSelective(file);
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("fileId", uuids.get(0));
        map.put("status", true);
        map.put("message", "ok");
        return map;
    }

    /**
     * 下载文件
     * @param uuid
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/down2/{uuid}")

    public ResponseEntity<byte[]> download2(@PathVariable("uuid") String uuid,HttpServletRequest request) throws IOException {
        Example example=new Example(FileRes.class);
        example.createCriteria().andEqualTo("uuid",uuid);
        List<FileRes> fileList=this.fileResService.selectByExample(example);
        if (null!=fileList && fileList.size()>0) {
            FileRes fileRes = fileList.get(0);
            File file = new File(PathUtil.getAppRootPath(request) +fileRes.getPath());
            HttpHeaders headers = new HttpHeaders();
            String fileName=new String((fileRes.getName()+"."+fileRes.getSuffix()).getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        }
        return null;
    }

    @RequestMapping(value = "/down/{uuid}")

    public void download(@PathVariable("uuid") String uuid,HttpServletRequest request,HttpServletResponse response) throws IOException {
        Example example=new Example(FileRes.class);
        example.createCriteria().andEqualTo("uuid",uuid);
        List<FileRes> fileList=this.fileResService.selectByExample(example);
        if (null!=fileList && fileList.size()>0) {
            FileRes fileRes = fileList.get(0);
//            //资源下载记录 资源下载数+1
//            Example resExample=new Example(Res.class);
//            resExample.createCriteria().andEqualTo("fileUuid",fileRes.getUuid());
//            List<Res> resList = resService.selectByExample(resExample);
//            if(resList != null && resList.size()>0){
//                Res res = resList.get(0);
//                res.setNum(res.getNum()+1);
//                resService.updateByPrimaryKeySelective(res);
//            }
            File file = new File(PathUtil.getAppRootPath(request) +fileRes.getPath());
            if(file.exists()){
                int len = 0;
                FileInputStream  fis = new FileInputStream(file);
                String filename= URLEncoder.encode(fileRes.getName()+"."+fileRes.getSuffix(), "utf-8"); //解决中文文件名下载后乱码的问题
                if ("FF".equals(WebUtil.getBrowser(request))) {
                    // 针对火狐浏览器处理方式不一样了
                    filename = new String((fileRes.getName()+"."+fileRes.getSuffix()).getBytes("UTF-8"), "iso-8859-1");
                }
                byte[] b = new byte[2048];
                response.setCharacterEncoding("utf-8");
//                response.setHeader("Content-Disposition","attachment; filename="+filename+"");
                response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
                //获取响应报文输出流对象
                ServletOutputStream out =response.getOutputStream();
                //输出
                while ((len = fis.read(b)) > 0){
                    out.write(b, 0, len);
                }
                out.flush();
                out.close();

            }

        }

    }

    /**
     * 预览文件
     * @param uuid
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/view/{uuid}")
    public void getIcon(@PathVariable("uuid") String uuid,
                        HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        response.setHeader("ETag", "W/\""+uuid+"\"");

        String etagStr = request.getHeader("If-None-Match");
        if (!StringUtils.isEmpty(etagStr)&&etagStr.equals("W/\""+uuid+"\"")){
            response.setStatus(304);
            return;
        }

        Example example=new Example(FileRes.class);
        example.createCriteria().andEqualTo("uuid",uuid);
        List<FileRes> fileList=this.fileResService.selectByExample(example);
        if (null!=fileList && fileList.size()>0) {
            FileRes fileRes = fileList.get(0);
            File file = new File(PathUtil.getAppRootPath(request) +fileRes.getPath());
            if (!file.exists()){
                file = new File(PathUtil.getAppRootPath(request)+"icon"+File.separator+"default.png");
            }
            FileInputStream is = new FileInputStream(file);
            ServletOutputStream sos = response.getOutputStream();
            byte[] data = new byte[2048];
            int len = 0;
            while ((len = is.read(data)) > 0) {
                sos.write(data, 0, len);
            }
            is.close();
            sos.flush();
            sos.close();
        }
    }


    /**
     * 单独统计资源下载
     * @param fileId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/downNum", method = RequestMethod.GET)
    public JsonResult downNum(int fileId,String openId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Assert.isTrue(fileId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"资源ID"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(openId),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"openId"));

        return qrDownRankService.downNum(fileId,openId);
    }

    /**
     * 预览文件
     * @param uuid
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/get")
    public Map<String, Object> getPath(@RequestParam("fileId") String uuid,
                        HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<>();
        FileRes fileRes = new FileRes();
        fileRes.setUuid(uuid);
        fileRes = this.fileResService.selectOne(fileRes);
        map.put("message", I18n.getMessage("success"));
        map.put("status", true);
        map.put("url", fileRes.getPath().replaceAll("\\\\", "/"));
        return map;


    }

    /**
     * desc:
     * date: 2019/5/10 0010
     * @author cuihao
     * @param resourceId
     * @return
     */
    @RequestMapping(value = "/getWebUrl")
    public JsonResult getWebUrl(String resourceId) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(resourceId), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));

        return fileResService.getWebUrl(resourceId);
    }

    /**
     * desc:
     * date: 2019/5/10 0010
     * @author cuihao
     * @param resourceId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/download")
    public void downloadFromZhixueyun(String resourceId,HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(resourceId), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));

        fileResService.downloadFromZhixueyun(resourceId,request,response);
    }

    public static void main(String[] args) throws Exception{
        String name="Unit10 Planning Our Lives(Vocabulary).mp3";
        String suffix="mp3";
        String filename = new String((name+"."+suffix).getBytes("UTF-8"), "iso-8859-1");
        System.out.println(filename);
    }



}
