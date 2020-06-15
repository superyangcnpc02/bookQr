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

/**
 * 只用一次
 * @author cuihao
 * @create 2017-04-11-17:07
 */
@RestController
@RequestMapping(value = "/qiyun")
public class QiYunController {
    public final static Logger log = LoggerFactory.getLogger(QiYunController.class);
    @Autowired
    private FileResCopyMapper fileResCopyMapper;

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

        Example exampleList = new Example(FileResCopy.class);
        Example.Criteria criteriaList = exampleList.createCriteria();
        criteriaList.andEqualTo("suffix","mp4");
        criteriaList.andIsNull("qizhixueid");

        List<FileResCopy> fileResList = fileResCopyMapper.selectByExample(exampleList);
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

                Example example = new Example(FileResCopy.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id",fileResCopy.getId());

                FileResCopy resCopy = new FileResCopy();
                resCopy.setQizhixueid(Long.valueOf(vo.getZhixueId()));
                fileResCopyMapper.updateByExampleSelective(resCopy,example);

            }
        }
        isRun = false;
        return new JsonResult(true, "");
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
        //文件路径,绝对地址
//        String filePath = "F:\\qiniu\\Tulips.jpg";
//        String filePath = "/home/qiniu/test.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        String fsize = "";
        String ext = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
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
