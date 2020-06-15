package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.BaseService;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.Res;
import com.yxtech.sys.vo.bookQr.CheckMessVo;
import com.yxtech.sys.vo.bookQr.GetWebUrlVo;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.qr.HttpTookit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Administrator on 2015/10/12.
 */

@Service
public class FileResService extends BaseService<FileRes> {

    private FileResMapper fileResMapper;
    @Autowired
    private ZhixueTokenService zhixueTokenService;

    public FileResMapper getFileResMapper() {
        return fileResMapper;
    }

    @Resource(name = "fileResMapper")
    public void setFileResMapper(FileResMapper fileResMapper) {
        setMapper(fileResMapper);
        this.fileResMapper = fileResMapper;
    }
    @Value("#{configProperties['zhixueyun.host']}")
    private String baseUrl;

    public void deleteForFileByUUID(String uuid,HttpServletRequest request) {
        Example ep = new Example(FileRes.class);
        ep.createCriteria().andEqualTo("uuid",uuid);
        List<FileRes> fileResList = this.fileResMapper.selectByExample(ep);
        if (null != fileResList && fileResList.size() > 0){
            for (FileRes fr : fileResList) {
                String fileName = PathUtil.getAppRootPath(request) + fr.getPath();
                File file = new File(fileName);
                this.fileResMapper.delete(fr);
                delFile(file);
            }
        }
    }

    /**
     * 根据uuid删除多条数据及文件
     * @param uuid
     * @param request
     */
    public void deleteForDataAndFileBySetUUID(Set<String> uuid,HttpServletRequest request) {
        Iterator<String> iterator = uuid.iterator();
        while(iterator.hasNext()){
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid",iterator.next());
            List<FileRes> fileResList = this.fileResMapper.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0){
                for (FileRes fr : fileResList) {
                    String fileName = PathUtil.getAppRootPath(request) + fr.getPath();
                    File file = new File(fileName);
                    this.fileResMapper.delete(fr);
                    delFile(file);
                }
            }
        }
    }

    public void deleteForFileById(int id,HttpServletRequest request){
        FileRes fileRes=this.fileResMapper.selectByPrimaryKey(id);
        String fileName = PathUtil.getAppRootPath(request) + fileRes.getPath();
        File file = new File(fileName);
        this.fileResMapper.delete(fileRes);
        delFile(file);
    }

    private boolean delFile(File file){
        if(file.exists() && file.isFile()){
            return file.delete();
        }
        return false;
    }

    public FileRes select4UUID(String uuid) {
        FileRes fileRes = new FileRes();
        fileRes.setUuid(uuid);
        return this.fileResMapper.selectOne(fileRes);

    }


    /**
     * 根据List<Res> 查询资源 id返回的是t_resources 表中id  删除时要使用
     * @param reses
     * @return
     * @author hesufang
     * @date 2015.11.3
     */
    public List<FileRes> getFileResList(List<Res> reses) {
        Map<String,List<Res>> map  = new HashMap<>();
        map.put("resList",reses);
        return this.fileResMapper.getFileResList(map);

    }


    /**
     * 修改文件路径
     * @param bookQrs
     * @param newName
     * @param oldName
     * @param newbookName
     * @param oldbookName
     */

  public void updateFilePath(HttpServletRequest request,List<BookQr> bookQrs,String newName,String oldName,String newbookName,String oldbookName){
      Map<String,Object> map  = new HashMap<>();

      map.put("bookQrs",bookQrs);
      map.put("newName",newName);
      map.put("oldName",oldName);

      map.put("oldbookName",oldbookName+".");
      map.put("newbookName",newbookName+".");

      fileResMapper.updateFilePath(map);

      for (BookQr bookQr : bookQrs){
          if (bookQr.getQrType() == 1){//课件二维码
              map.put("bookQr",bookQr);
              fileResMapper.updateCourseQrFileNamePath(map);
              File oldFile = new File(PathUtil.getAppRootPath(request) + BookQrService.QR_ROOT_PATH + newName +File.separator + oldbookName+".png");
              File newFile = new File(PathUtil.getAppRootPath(request) + BookQrService.QR_ROOT_PATH + newName+File.separator + newbookName+".png");
              oldFile.renameTo(newFile);// 重命名

              File pdfoldFile = new File(PathUtil.getAppRootPath(request) + BookQrService.QR_ROOT_PATH + newName +File.separator + oldbookName+".pdf");
              File pdfnewFile = new File(PathUtil.getAppRootPath(request) + BookQrService.QR_ROOT_PATH + newName+File.separator + newbookName+".pdf");
              pdfoldFile.renameTo(pdfnewFile);// 重命名
          }
      }
    }

    public JsonResult getWebUrl(String resourceId) throws Exception {
        //get download url
        String token = zhixueTokenService.getToken();
        final String getWebUrl = baseUrl + "/resource/web/url?resourceId="+resourceId+"&token=" + token;
        String result = HttpTookit.doGet(getWebUrl, null, "utf-8", true);
        System.out.println("----------------result==" + result);
        JSONObject jsonObject = JSON.parseObject(result);
        String code = jsonObject.getString("code");
        if (!"0".equals(code)) {
            throw new ServiceException("getWebUrl调用失败:" + result);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        String playAuth = data.getString("PlayAuth");
        System.out.println("playAuth：" + playAuth);
        //中文转码
        int beginIndex = playAuth.lastIndexOf("/");
        int endIndex =playAuth.indexOf("?");
        String leftStr = playAuth.substring(0,beginIndex+1);
        String rightStr = playAuth.substring(endIndex,playAuth.length());
        String keyWord = playAuth.substring(beginIndex+1,endIndex);
        System.out.println("leftStr:"+leftStr);
        System.out.println("rightStr:"+rightStr);
        System.out.println("keyWord:"+keyWord);
        String encodeKeyWord = URLEncoder.encode(keyWord, "utf-8");
        //encode后空格会变+号，需要变回去
        encodeKeyWord=encodeKeyWord.replace("+", "%20");
        System.out.println("encodeKeyWord:"+encodeKeyWord);

        String encodePlayAuth = leftStr + encodeKeyWord + rightStr;
        System.out.println("encodePlayAuth:"+encodePlayAuth);
        return new JsonResultData<>(new GetWebUrlVo(encodePlayAuth));
    }

    public void downloadFromZhixueyun(String resourceId,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //get download url
        String token = zhixueTokenService.getToken();
        final String updateUrl = baseUrl + "/resource/download?token=" + token;
        Map<String, String> paramsR = new HashMap<>();
        paramsR.put("resourceId", resourceId);
        String result = HttpTookit.doPostJson(updateUrl, paramsR);
        System.out.println("----------------result==" + result);
        JSONObject jsonObject = JSON.parseObject(result);
        String code = jsonObject.getString("code");
        if (!"0".equals(code)) {
            throw new ServiceException("获取智学云下载地址错误!信息:" + result);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        String playAuth = data.getString("PlayAuth");
        System.out.println("playAuth：" + playAuth);
        //中文转码
        int beginIndex = playAuth.lastIndexOf("/");
        int endIndex =playAuth.indexOf("?");
        String leftStr = playAuth.substring(0,beginIndex+1);
        String rightStr = playAuth.substring(endIndex,playAuth.length());
        String keyWord = playAuth.substring(beginIndex+1,endIndex);
        System.out.println("leftStr:"+leftStr);
        System.out.println("rightStr:"+rightStr);
        System.out.println("keyWord:"+keyWord);
        String encodeKeyWord = URLEncoder.encode(keyWord, "utf-8");
        //encode后空格会变+号，需要变回去
        encodeKeyWord=encodeKeyWord.replace("+", "%20");
        System.out.println("encodeKeyWord:"+encodeKeyWord);

        String encodePlayAuth = leftStr + encodeKeyWord + rightStr;
        System.out.println("encodePlayAuth:"+encodePlayAuth);
        response.sendRedirect(encodePlayAuth);
    }
}
