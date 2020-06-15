package com.yxtech.utils.mail;

import com.yxtech.common.advice.ServiceException;
import com.yxtech.sys.controller.BookController;
import com.yxtech.sys.service.ZhixueTokenService;
import com.yxtech.utils.qr.HttpTookit;
import com.yxtech.utils.runCode.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeUtility;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuihao
 * @create 2017-06-09-9:21
 */
@Component
public class YunEmail {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    @Value("#{configProperties['zhixueyun.host']}")
    private String host;
    @Autowired
    private ZhixueTokenService zhixueTokenService;

    public  void sendEmail(String emailTo, String subject, String content) {
        // 1.get请求，获取token
        String token = "";
        try {
            token = this.zhixueTokenService.getToken();
        } catch (Exception e) {
            log.error("获取智学云token失败！",e);
        }
        //2.发送邮件
        final String getIdUrl = host + "/mail/send?token=" + token;
        Map<String, String> params = new HashMap<>();
        try {
            String nick = MimeUtility.encodeText("文泉云盘");
            params.put("fromName", nick);
            params.put("emailTo", emailTo);
            params.put("subject", subject);
            params.put("content", content);

            String idReisult = HttpClientUtil.doPost(getIdUrl, params, "utf-8");
            System.out.println("YunEmail result="+idReisult);
        } catch (Exception e) {
            log.error("发送邮件失败",e);
            throw new ServiceException("发送邮件失败！");
        }
    }
}
