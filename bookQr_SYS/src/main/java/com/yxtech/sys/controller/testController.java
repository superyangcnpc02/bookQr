package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.Res;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.yun.ZipPostVo;
import com.yxtech.utils.file.FileMd5Util;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.NameUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.mail.MailSender;
import com.yxtech.utils.password.PayEncrypt;
import com.yxtech.utils.password.WqjiaoxueVo;
import com.yxtech.utils.qr.HttpTookit;
import com.yxtech.utils.runCode.HttpClientUtil;
import com.yxtech.utils.wechat.CheckoutUtil;
import jodd.datetime.JDateTime;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/10/9.
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/test1")
public class testController {
    @Autowired
    private FileResService fileResService;
    @Autowired
    private FileResMapper fileResMapper;
    @Autowired
    private ResService resService;
    /**
     * 上传文件
     *
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "/upload")
    public Map<String, Object> upload(
            HttpServletRequest request, @RequestParam(value = "data",required = false) MultipartFile multipartFile) throws IllegalStateException, IOException, Exception {

        String action = request.getParameter("action");
        String uuid = request.getParameter("uuid");
        String fileName = request.getParameter("name");
        String size = request.getParameter("size");//总大小
        int total = Integer.valueOf(request.getParameter("total"));//总片数
        int index = Integer.valueOf(request.getParameter("index"));//当前是第几片
        String fileMd5 = request.getParameter("filemd5"); //整个文件的md5
        String date = request.getParameter("date"); //文件第一个分片上传的日期(如:20170122)
        String md5 = request.getParameter("md5"); //分片的md5

        //生成上传文件的路径信息，按天生成
        String savePath = Constant.FILE_PATH + File.separator + date;
        String saveDirectory = PathUtil.getAppRootPath(request) + savePath + File.separator + uuid;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()) {
            path.mkdirs();
        }
        //文件分片位置
        File file = new File(saveDirectory, uuid + "_" + index);

        //根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
        Map<String, Object> map = null;
        if("check".equals(action)){
            String md5Str = FileMd5Util.getFileMD5(file);
            if (md5Str != null && md5Str.length() == 31) {
                System.out.println("check length =" + md5.length() + " md5Str length" + md5Str.length() + "   " + md5 + " " + md5Str);
                md5Str = "0" + md5Str;
            }
            if (md5Str != null && md5Str.equals(md5)) {
                //分片已上传过
                map = new HashMap<>();
                map.put("flag", "2");
                map.put("fileId", uuid);
                map.put("status", true);
                return map;
            } else {
                //分片未上传
                map = new HashMap<>();
                map.put("flag", "1");
                map.put("fileId", uuid);
                map.put("status", true);
                return map;
            }
        }else if("upload".equals(action)){
            //分片上传过程中出错,有残余时需删除分块后,重新上传
            if (file.exists()) {
                file.delete();
            }
            //上传分片
            multipartFile.transferTo(new File(saveDirectory, uuid + "_" + index));

            if(index == 1){
                //文件第一个分片上传时记录到数据库
                FileRes fileRes = new FileRes();
                String name = NameUtil.getFileNameNoEx(fileName);
                if (name.length() > 50) {
                    name = name.substring(0, 50);
                }
                fileRes.setName(name);
                fileRes.setSuffix(NameUtil.getExtensionName(fileName));
                fileRes.setUuid(uuid);
                fileRes.setPath(savePath + File.separator + uuid + "." + fileRes.getSuffix());
                fileRes.setSize(Integer.parseInt(size));
                fileRes.setMd5(fileMd5);
                fileRes.setStatus(Constant.ZERO);
                fileRes.setCreateTime(new Date());
                this.fileResService.insert(fileRes);
            }

            if (path.isDirectory()) {
                File[] fileArray = path.listFiles();
                if (fileArray != null) {
                    if (fileArray.length == total) {
                        //分块全部上传完毕,合并
                        String suffix = NameUtil.getExtensionName(fileName);

                        File newFile = new File(PathUtil.getAppRootPath(request) + savePath, uuid + "." + suffix);
                        FileOutputStream outputStream = new FileOutputStream(newFile, true);//文件追加写入
                        byte[] byt = new byte[10 * 1024 * 1024];
                        int len;

                        FileInputStream temp = null;//分片文件
                        for (int i = 0; i < total; i++) {
                            int j = i + 1;
                            temp = new FileInputStream(new File(saveDirectory, uuid + "_" + j));
                            while ((len = temp.read(byt)) != -1) {
                                System.out.println("-----" + len);
                                outputStream.write(byt, 0, len);
                            }
                        }
                        //关闭流
                        temp.close();
                        outputStream.close();
                        //修改FileRes记录为上传成功
                        Example example = new Example(FileRes.class);
                        Example.Criteria criteria = example.createCriteria();
                        criteria.andEqualTo("md5",fileMd5);
                        FileRes fileRes = new FileRes();
                        fileRes.setStatus(Constant.ONE);
                        fileResService.updateByExampleSelective(fileRes,example);

                        map = new HashMap<>();
                        map.put("flag", "3");
                        map.put("complete", "1");
                        map.put("fileId", uuid);
                        map.put("status", true);
                        return map;
                    }
                }
            }
        }

        map = new HashMap<>();
        map.put("flag", "3");
        map.put("complete", "0");
        map.put("fileId", uuid);
        map.put("status", true);
        return map;
    }

    /**
     * 上传文件前校验
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/isUpload")
    public Map<String, Object> isUpload(HttpServletRequest request) throws Exception {

        String md5 = request.getParameter("md5");

        Example example = new Example(FileRes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("md5", md5);
        List<FileRes> list = fileResService.selectByExample(example);

        Map<String, Object> map = null;
        if (list == null || list.size() == 0) {
            //没有上传过文件
            String uuid = UUID.randomUUID().toString();
            map = new HashMap<>();
            map.put("flag", "1");
            map.put("fileId", uuid);
            map.put("date", new JDateTime().toString("YYYYMMDD"));
            map.put("status", true);
        } else {
            FileRes fileRes = list.get(0);
            //求文件上传日期
            SimpleDateFormat sdf=new SimpleDateFormat("YYYYMMdd");
            Date date=fileRes.getCreateTime();
            String strDate=sdf.format(date);
            if(fileRes.getStatus()==0){
                //文件上传部分
                map = new HashMap<>();
                map.put("flag", "2");
                map.put("fileId", fileRes.getUuid());
                map.put("date",strDate);
                map.put("status", true);
            }else if(fileRes.getStatus()==1){
                //文件上传成功
                map = new HashMap<>();
                map.put("flag", "3");
                map.put("fileId", fileRes.getUuid());
                map.put("date",strDate);
                map.put("status", true);
            }

        }

        return map;
    }

    @RequestMapping(value = "/testout")
    public void testout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("请求进来了...");
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        PrintWriter print;
        if (isGet) {
            // 微信加密签名
            String signature = request.getParameter("signature");
            // 时间戳
            String timestamp = request.getParameter("timestamp");
            // 随机数
            String nonce = request.getParameter("nonce");
            // 随机字符串
            String echostr = request.getParameter("echostr");
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
                try {
                    print = response.getWriter();
                    print.write(echostr);
                    print.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Autowired
    private MailSender mailSender;
    @RequestMapping(value = "/zz")
    public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("请求进来了...");
        String subject = "文泉云盘移动阅读平台";
        String context = "感谢使用文泉云盘移动阅读平台1111";
        String[] toMail = new String[]{"871326692@qq.com"};
        mailSender.sendRichTextMail(context, subject, toMail);
    }

    @Autowired
    private UserCenterService userCenterService;
    @RequestMapping(value = "/testTeacherAuth")
    public void testTeacherAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String key = "C234e017F54c6E848763B8fF43C330bD";
        final String url = "http://testw.wqjiaoxue.com/v1/login/callbackyp?token=";
        String mobile = "15839661369";
        String source = "yunpan";
        String returnUrl = "http://testw.wqjiaoxue.com/#/identify";
        String exp =String.valueOf(System.currentTimeMillis() + 3600*1000).substring(0,10);
//        Map<String, String> params = new HashMap<>();
//        params.put("mobile", mobile);
//        params.put("source", source);
//        params.put("exp", exp);
//        params.put("returnUrl", returnUrl);

        WqjiaoxueVo vo = new WqjiaoxueVo();
        vo.setMobile(mobile);
        vo.setSource(source);
        vo.setExp(exp);
        vo.setReturnUrl(returnUrl);

        String jsonParams = JSON.toJSONString(vo);
        System.out.println("jsonParams:"+jsonParams);

        String token = PayEncrypt.encryptMode(key, jsonParams).replace("\n","");
        System.out.println("解密结果：" + PayEncrypt.decryptMode(key, token));
        String sendUrl = url + token;
        System.out.println("跳转地址："+sendUrl);
        response.sendRedirect(sendUrl);

    }

    @RequestMapping(value = "/queryAliVideoStatus")
    public void queryAliVideoStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("请求进来了...");
        String videoId = "f8226c8b3bc44952b3bd4a68519481a6";
        int resultCode = resService.queryAliVideoStatus(videoId);
        System.out.println(resultCode);
    }

    @Autowired
    private ZhixueTokenService zhixueTokenService;
    @RequestMapping(value = "/zip")
    public void zip(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("请求进来了...");
        // 1.获取token
        String token = "";
        token = this.zhixueTokenService.getToken();

        List<ZipPostVo> list = new ArrayList<>();
        ZipPostVo vo1 = new ZipPostVo();
        vo1.setRname("2018-12-04.mp4");
        vo1.setResourceId("4305043861");

        ZipPostVo vo2 = new ZipPostVo();
        vo2.setRname("3.jpg");
        vo2.setResourceId("4305043863");

        list.add(vo1);
        list.add(vo2);

        Map<String, Object> params = new HashMap<>();
        params.put("zipFiles", list);
        params.put("userId", "59e95a4689eeb92f380f4ab2");

        String strjson = new com.alibaba.fastjson.JSONObject(params).toString();
        System.out.println("strjson==" + strjson);

        String strResponse = "";
        // 2.向云端发送压缩指令
//        String url = "http://api.izhixue.cn/FileManagement/ZipFile?ssotoken="+token;
        String url = "http://test.open.izhixue.cn" + "/resource/zip?token=" + token;
        System.out.println("url==" + url);

        try {
            strResponse = HttpClientUtil.sendPost(url, strjson);
            if (StringUtils.isEmpty(strResponse)) {
                throw new ServiceException("调用智学云接口返回空!");
            }
            System.out.println("strResponse:" + strResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        com.alibaba.fastjson.JSONObject object = JSON.parseObject(strResponse);
        String code = object.getString("code");
        if (!"0".equals(code)) {
            throw new ServiceException("调用智学云压缩命令失败!");
        }
        JSONObject data = object.getJSONObject("data");
        String PersistentId = data.getString("property");
        System.out.println("code==" + code + " PersistentId=" + PersistentId);
    }


    @RequestMapping(value = "/dataConvert")
    public void dataConvert(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("开始执行数据转换...");
        // TODO: 2019/4/29 0029 除去 3ds Max 2017/Vray效果图制作实例教程
        List<String> errorPathList = new ArrayList<>();
        //1.修改t_file_res表
        String keyword = "WEB-INF/books";
        Example example = new Example(FileRes.class);
        example.selectProperties("id,path");
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("path", "%" + keyword + "%");
        List<FileRes> fileResList = fileResMapper.selectByExample(example);
        for(FileRes fileRes:fileResList){
            String path = fileRes.getPath();
            String[] arrFrist = path.split("/");
            if(arrFrist.length == 4){
                String concatStr = arrFrist[2];
                String[] arrSecond = concatStr.split("-");
                concatStr = arrSecond[arrSecond.length - 1];
                //拼结果
                String pathResult = arrFrist[0] + "/" + arrFrist[1] + "/" + concatStr + "/" + arrFrist[3];
                System.out.println("pathResult:" + pathResult);

                fileRes.setPath(pathResult);
                fileResMapper.updateByPrimaryKeySelective(fileRes);
            }else{
                errorPathList.add(path);

                String concatStr = arrFrist[arrFrist.length - 2];
                String[] arrSecond = concatStr.split("-");
                concatStr = arrSecond[arrSecond.length - 1];
                //拼结果
                String pathResult = arrFrist[0] + "/" + arrFrist[1] + "/" + concatStr + "/" + arrFrist[arrFrist.length - 1];
                System.out.println("-----------path:"+path);
                System.out.println("------pathResult:" + pathResult);

                fileRes.setPath(pathResult);
                fileResMapper.updateByPrimaryKeySelective(fileRes);
            }
        }
        System.out.println("-----------errorPathList.size():"+errorPathList.size());
        //2. 重命名/WEB-INF/books目录下面
        String bookPath = PathUtil.getAppRootPath(request) + BookQrService.QR_ROOT_PATH;
        File booksFile = new File(bookPath);
        File[] listFiles = booksFile.listFiles();
        for(File oldFile: listFiles){
            String oldFileName = oldFile.getName();
            String[] arrFileName = oldFileName.split("-");
            if(arrFileName.length == 3){
                String newFileName = arrFileName[arrFileName.length -1];
                File newFile = new File(bookPath + File.separator + newFileName);
                oldFile.renameTo(newFile);
            }
        }

    }
}
