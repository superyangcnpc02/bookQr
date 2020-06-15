package com.yxtech.sys.controller;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.FileResCopyMapper;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.FileResCopy;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.qr.HttpTookit;
import com.yxtech.utils.runCode.HttpClientUtil;
import com.yxtech.utils.runCode.LetvCloudV1;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author cuihao
 * @create 2017-04-11-17:07
 */
@RestController
@RequestMapping(value = "/yun")
public class YunController {
    public final static Logger log = LoggerFactory.getLogger(YunController.class);
    @Autowired
    private FileResCopyMapper fileResCopyMapper;

    /**
     * 乐视云
     */
    final static String USER_UNIQUE = "066ccb050a";
    final static String SECRET_KEY = "bba1a6ccd48610b6baf4c6096678e250";

    /**
     *
     * @return
     */
    @RequestMapping(value = "/setClose", method = RequestMethod.GET)
    public JsonResult setClose() {

        this.kaiguan=false;

        return new JsonResult(true, "设置成功!");
    }


    /**
     * 文件转移(视频文件存到乐视云,非视频文件存到七牛云)
     * @param id   fileRes的id
     * @param flag 1等于;2大于
     * @param basePath
     * @param request
     * @return
     */
    private boolean kaiguan=false;
    private boolean isRun=false;
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public JsonResult upload(HttpServletRequest request) {
        if (isRun){
            return new JsonResult(true, "has run.");
        }
        isRun = true;
        this.kaiguan = true;
        String basePath = PathUtil.getAppRootPath(request);
        List<FileResCopy> fileResList = fileResCopyMapper.getFilesNoUpload();
        for (FileResCopy fileResCopy : fileResList) {
            if (!kaiguan){
                break;
            }
            String filePath = basePath + fileResCopy.getPath();//资源绝对路径
            File f= new File(filePath);
            if (!f.exists() ){
                try{
                    FileUtils.write(new File(basePath+"noupload.txt"),"文件id:"+fileResCopy.getId()+"  路径:"+filePath+" 不存在\r\n","utf-8",true);
                }catch (IOException e){
                    log.error("文件id:"+fileResCopy.getId()+"  路径:"+filePath+"不存在写入错误");
                }
                continue;
            }
            //判断资源格式
            String suffix = fileResCopy.getSuffix();
            if ("mp4".equals(suffix)) {
                //视频向乐视传
                String fileName = fileResCopy.getName();
                BaseVo vo = leShi(fileName, filePath);
                if(vo==null){
                    try{
                        FileUtils.write(new File(basePath+"noupload.txt"),"视频文件id:"+fileResCopy.getId()+"  路径:"+filePath+" 上传乐视失败\r\n","utf-8",true);
                    }catch (IOException e){
                        log.error("文件id:"+fileResCopy.getId()+"  路径:"+filePath+"上传乐视失败写入错误");
                    }
                    continue;
                }

                System.out.println("zhixueId=="+vo.getZhixueId()+" viewUrl=="+vo.getViewUrl());

                Map<String,Object> params  = new HashMap<>();
                params.put("id",fileResCopy.getId());
                params.put("zhixueid",vo.getZhixueId());
                params.put("viewurl",vo.getViewUrl());
                fileResCopyMapper.updateFileResCopy(params);


                //视频向七牛传
                BaseVo voQiniu = QiNiu(filePath);
                if(voQiniu==null){
                    try{
                        FileUtils.write(new File(basePath+"noupload.txt"),"视频文件id:"+fileResCopy.getId()+"  路径:"+filePath+" 上传七牛失败\r\n","utf-8",true);
                    }catch (IOException e){
                        log.error("文件id:"+fileResCopy.getId()+"  路径:"+filePath+"上传七牛失败写入错误");
                    }
                    continue;
                }

                System.out.println("zhixueId=="+voQiniu.getZhixueId()+" viewUrl=="+voQiniu.getViewUrl());

                Example example = new Example(FileResCopy.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id",fileResCopy.getId());

                FileResCopy resCopy = new FileResCopy();
                resCopy.setQizhixueid(Long.valueOf(voQiniu.getZhixueId()));
                fileResCopyMapper.updateByExampleSelective(resCopy,example);
            } else {
                BaseVo vo = QiNiu(filePath);
                if(vo==null){
                    try{
                        FileUtils.write(new File(basePath+"noupload.txt"),"文件id:"+fileResCopy.getId()+"  路径:"+filePath+" 上传失败\r\n","utf-8",true);
                    }catch (IOException e){
                        log.error("文件id:"+fileResCopy.getId()+"  路径:"+filePath+"上传失败写入错误");
                    }
                    continue;
                }

                System.out.println("zhixueId=="+vo.getZhixueId()+" viewUrl=="+vo.getViewUrl());

                Map<String,Object> params  = new HashMap<>();
                params.put("id",fileResCopy.getId());
                params.put("zhixueid",vo.getZhixueId());
                params.put("viewurl",vo.getViewUrl());
                fileResCopyMapper.updateFileResCopy(params);
            }
        }
        isRun = false;
        return new JsonResult(true, "");
    }

    /**
     * @param fileName
     * @param filePath
     */
    private BaseVo leShi(String fileName, String filePath) {
        System.out.println("fileName=="+fileName+" filePath=="+filePath);
        LetvCloudV1 client = new LetvCloudV1(USER_UNIQUE, SECRET_KEY);

        //定义输出格式 (json|xml)
        client.format = "json";

        String fileKey = fileName;
        String video_id = "";//视频ID
        String upload_url = "";//视频上传地址
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        String fsize = "";
        String video_unique = "";

        File f= new File(filePath);
        if (f.exists() && f.isFile()){
            fsize=String.valueOf(f.length());
        }else{
            log.error("文件"+filePath+"不存在!");
            return null;
        }

        //视频上传初始化（Web方式）
        try {
            String response = client.videoUploadInit(fileKey);
            System.out.println(response);

            JSONObject tokenObj = JSONObject.fromObject(response);
            String status = tokenObj.getString("code");
            if (!"0".equals(status)) {
                log.error("初始化乐视云,返回code错误！");
                return null;
            }
            JSONObject data = tokenObj.getJSONObject("data");
            video_id = data.getString("video_id");//视频ID
            video_unique = data.getString("video_unique");//视频唯一标识码
            System.out.println("video_id=="+video_id);
            System.out.println("video_unique=="+video_unique);
            upload_url = data.getString("upload_url");//视频上传地址
            System.out.println("####################视频上传地址==" + upload_url + "######################");
        } catch (Exception e) {
            log.error("初始化乐视云,解析结果错误！",e);
            return null;
        }
        //视频上传（Web方式）
        try {
            String response2 = client.videoUpload(filePath, upload_url);
            System.out.println(response2);

            JSONObject tokenObj = JSONObject.fromObject(response2);
            String status = tokenObj.getString("code");
            if (!"0".equals(status)) {
                log.error("上传乐视云,返回code错误！");
                return null;
            }
        } catch (Exception e) {
            log.error("上传乐视云,解析结果错误！",e);
            return null;
        }

        // 1.get请求，获取token
        String token = "";
        final String getTokenUrl = "http://api.izhixue.cn/Account/Login?email=zhaotaiyuan@zhixue.cn&password=1geqkZ8v";
        String tokenResult = HttpTookit.doGet(getTokenUrl, null, "utf-8", true);
        try {
            token = tokenResult.replace("\"", "").replace("\r\n", "");
            System.out.println(token);
        } catch (Exception e) {
            log.error("乐视获取智学云token失败！",e);
            return null;
        }

        // 2.取得智学云id
        String zhixueId = "";
        final String getIdUrl = "http://api.izhixue.cn/FileManagement/CreateVideoFile?ssotoken=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("key", fileKey);
        params.put("ext", ext);
        params.put("length", fsize);
        params.put("uu", USER_UNIQUE);
        params.put("vu", video_unique);
        params.put("videoId", video_id);
        try {
            String idResult = HttpClientUtil.doPost(getIdUrl, params, "utf-8");
            zhixueId = idResult.replace("\"", "");
            System.out.println(zhixueId);
        } catch (Exception e) {
            log.error("乐视获取智学云id失败！",e);
            return null;
        }

        //3.校验是否成功
        String url="http://yuntv.letv.com/bcloud.html?uu="+USER_UNIQUE+"&vu="+video_unique+"&auto_play=1&width=800&height=450";

        BaseVo vo = new BaseVo();
        vo.setZhixueId(zhixueId);
        vo.setViewUrl(url);

        return vo;

    }

    /**
     * @param filePath
     * @param filePath
     */
    private BaseVo QiNiu(String filePath) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "FQy_HVQpTeQPGc4riZqm0WBglJzwE2G2G5OJ5qf6";
        String secretKey = "4KsDbGPJjP8zAfapyMPav-mUdPUR8PkP-SqA1i0C";
        String bucket = "pdfconvertion";
        String fsize = "";
        String ext = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String uuid = UUID.randomUUID().toString();
        String key = uuid+"."+ext;
        Auth auth = Auth.create(accessKey, secretKey);
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        long expireSeconds = 3600;
        String upToken = auth.uploadToken(bucket, null, expireSeconds, putPolicy);

        try {
            Response response = uploadManager.put(filePath, key, upToken);
            //解析上传成功的结果
            ReturnBody putRet = new Gson().fromJson(response.bodyString(), ReturnBody.class);
            key = putRet.getKey();
            fsize = putRet.getFsize();
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            System.out.println(putRet.bucket);
            System.out.println(putRet.fsize);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            log.error("七牛上传文件失败！",r.toString());
            try {
                System.err.println(r.bodyString());
                log.error("七牛上传文件失败！",r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
                ex2.printStackTrace();
            }
            return null;
        }

        // 1.get请求，获取token
        String token = "";
        final String getTokenUrl = "http://api.izhixue.cn/Account/Login?email=zhaotaiyuan@zhixue.cn&password=1geqkZ8v";
        String tokenResult = HttpTookit.doGet(getTokenUrl, null, "utf-8", true);
        try {
            token = tokenResult.replace("\"", "").replace("\r\n", "");
            System.out.println(token);
        } catch (Exception e) {
            log.error("七牛获取智学云token失败！",e);
            return null;
        }

        // 2.取得智学云id
        String zhixueId = "";
        final String getIdUrl = "http://api.izhixue.cn/FileManagement/CreateFile?ssotoken=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("ext", ext);
        params.put("length", fsize);
        try {
            String idResult = HttpClientUtil.doPost(getIdUrl, params, "utf-8");
            zhixueId = idResult.replace("\"", "");
            System.out.println(zhixueId);
        } catch (Exception e) {
            log.error("七牛获取智学云id失败！",e);
            return null;
        }

        //3.校验是否成功
        String url="http://cms.izhixue.cn/FileManage/DownLoad?resourceID="+zhixueId;

        BaseVo vo = new BaseVo();
        vo.setZhixueId(zhixueId);
        vo.setViewUrl(url);

        return vo;
    }

    /**
     * 七牛返回的对象
     */
    private class ReturnBody {
        public String hash;
        public String key;
        public String bucket;
        public String fsize;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getFsize() {
            return fsize;
        }

        public void setFsize(String fsize) {
            this.fsize = fsize;
        }
    }


    /**
     * 存储智学云生成的id和预览url
     */
    private class BaseVo{
        private String zhixueId;//智学云id
        private String viewUrl;//预览url

        public String getZhixueId() {
            return zhixueId;
        }

        public void setZhixueId(String zhixueId) {
            this.zhixueId = zhixueId;
        }

        public String getViewUrl() {
            return viewUrl;
        }

        public void setViewUrl(String viewUrl) {
            this.viewUrl = viewUrl;
        }

    }

}
