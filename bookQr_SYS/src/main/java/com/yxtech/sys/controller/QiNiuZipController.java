package com.yxtech.sys.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.qiniu.util.UrlSafeBase64;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.vo.yun.QiResult;
import com.yxtech.sys.vo.yun.QiStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/qiniuzip")
public class QiNiuZipController {

    private static final String testAccessKey = "JrmxpwW96ZmiCAycnew0_QEh4NDuLG1keyeGj6Gf";
    private static final String testSecretKey = "POG1w2tOUZVCB6JgzIx1I6czPzMcRYmoCUNFsS_q";
    private static final String bucket = "cuihao";
    private static final Auth testAuth = Auth.create(testAccessKey, testSecretKey);

    private static final String testMp4FileKey = "FlTC8aHrbxLWgaXHB4QhpVAM7gKt";//文件key,必须是已存在的文件


    /**
     * 调用七牛云压缩接口,压缩二维码下面所有资源文件（测试用的）
     * @param id 二维码id
     * @param request
     * @return
     */
    @RequestMapping(value = "/zip", method = RequestMethod.GET)
    public JsonResult zip(Integer id, HttpServletRequest request) {

        Zone zone = Zone.zone2();
        //构造fops
        String url1 = "http://oo682ne8m.bkt.clouddn.com/FlTC8aHrbxLWgaXHB4QhpVAM7gKt";
        String url2 = "http://oo682ne8m.bkt.clouddn.com/ltt-Vm75RoSqw0uYl3exRFGkigrE";
        String url3 = "http://oo682ne8m.bkt.clouddn.com/FpEd_tZ5C6XQB_OV7KGf3pw_iCmp";
        String name1 = "FlTC8aHrbxLWgaXHB4QhpVAM7gKt.jpg";//url1资源的别名
        String name2 = "ltt-Vm75RoSqw0uYl3exRFGkigrE.mp4";//url2资源的别名
        String name3 = "FpEd_tZ5C6XQB_OV7KGf3pw_iCmp.jpg";//url3资源的别名
        String zipName = "yasuobao.zip";                  //生成的压缩包名称,不可重复
        String encodedEntryURI = UrlSafeBase64.encodeToString(bucket + ":" + zipName);
        String fops = "";//fops参数
        try {
            fops = "mkzip/2"
                    + "/url/" + UrlSafeBase64.encodeToString(url1) + "/alias/" + UrlSafeBase64.encodeToString(name1)
                    + "/url/" + UrlSafeBase64.encodeToString(url2) + "/alias/" + UrlSafeBase64.encodeToString(name2)
                    + "/url/" + UrlSafeBase64.encodeToString(url3) + "/alias/" + UrlSafeBase64.encodeToString(name3)
                    + "|saveas/" + encodedEntryURI;

            System.out.println(fops);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String notifyURL = "http://file.ereading.ztydata.com.cn/bookQr/qiniuzip/getQiniuResult";//处理完成后,回调接口

        try {
            Configuration cfg = new Configuration(zone);
            OperationManager operationManager = new OperationManager(testAuth, cfg);
            String persistentId = operationManager.pfop(bucket, testMp4FileKey, fops, "", notifyURL, true);//持久化处理的进程ID

            String purl = "http://api.qiniu.com/status/get/prefop?id=" + persistentId;
            System.out.println(purl);

            return new JsonResult(true, "正在处理中,请稍后查看!");
        } catch (QiniuException e) {
            System.out.println(e.response.toString());
        }

        return new JsonResult(false, "程序错误！");
    }

    /**
     * 调用七牛云压缩完成后,回调接口
     * @param status
     * @return
     */
    @RequestMapping(value = "/getQiniuResult", method = RequestMethod.POST)
    public void getQiniuResult(@RequestBody QiStatus status) {
        int code = status.code;//状态码 0：成功，1：等待处理，2：正在处理，3：处理失败，4：成功但通知失败。
        String desc = status.desc;//与状态码相对应的详细描述。
        if (code==0) {
            String id = status.id;//持久化处理的进程 ID，即前文中的<persistentId>。

            List<QiResult> items = status.getItems();
            if(items!=null && items.size()!=0){
                QiResult result =items.get(0);
                String resultCmd =result.cmd;
                int resultCode =result.code;
                String resultDesc =result.desc;
                System.out.println("################resultCmd=="+resultCmd+" resultCode=="+resultCode+" resultDesc=="+resultDesc);
                if(resultCode==0){
                    String key =result.key;
                    System.out.println("压缩包名称是="+key);
                }

            }

        }else if(code==3){
            System.out.println("处理失败,原因是:"+desc);
        }else if(code ==1 || code==2){
            System.out.println("正在处理中...");
        }else if(code==4){
            System.out.println("处理成功但通知失败");
        }

    }

}
