package com.yxtech.utils.mail;

import com.sun.mail.util.MailSSLSocketFactory;
import com.yxtech.sys.controller.MailController;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author cuihao
 * @create 2017-05-18-9:38
 */

public class QqMailSender {


    private static final String account="service@tupmbook.com";
	private static final String password="Abcd123";
    private static final Session session;
    private static final Message message;

    /*加载email配置*/
    static {
        MailSSLSocketFactory msf = null;
        try {
            msf = new MailSSLSocketFactory();
            msf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }

        Properties props = new Properties();
        // 开启调试
        props.setProperty("mail.debug", "true");
        // 是否需要验证
        props.setProperty("mail.smtp.auth", "true");
        // 发送邮件服务器
        props.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", msf);

        // 使用匿名内部类，用邮箱进行验证
        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 通过用户名和密码进行验证
                return new PasswordAuthentication(account,
                        password);
            }
        });

        message = new MimeMessage(session);
    }

    /**
     * 通过qq邮箱发送邮件,qq邮箱需要在设置里开启POP3/SMTP的授权，通过用户名+授权码方式才能发邮件
     * @param toEmail
     * @param subject
     * @param content
     */
    public static void qqSender(String toEmail, String subject, String content) throws UnsupportedEncodingException,MessagingException{

        try {
             // 设置邮件发送方
            String nick = MimeUtility.encodeText("文泉云盘");
            message.setFrom(new InternetAddress(nick + "<"+account+">"));
            // 设置邮件标题
            String nowTime = " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            message.setSubject(subject+nowTime);
            // 设置邮件内容
            message.setContent(content, "text/html;charset=utf-8");
            // 设置邮件接收方
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(
                    toEmail));

            // 发送邮件
            Transport.send(message);

        } catch (AddressException e) {
            e.printStackTrace();
        }
    }
}
