package com.yxtech.utils.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeUtility;
import java.util.Properties;

/**
 * @author cuihao
 * @create 2016-11-22-17:28
 */

public class test {
    public static void main(String[] args) throws Exception{
//        String nick = MimeUtility.encodeText("文泉云盘");
//
//        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
//        senderImpl.setHost("smtp.163.com");// 设定smtp邮件服务器
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(nick +"<"+"feixunii@163.com"+">");
//        mailMessage.setSubject("标题");
//        mailMessage.setText("内容。。。17:38");
//        senderImpl.setUsername("feixunii@163.com"); // 根据自己的情况,设置username
//        senderImpl.setPassword("zml1314++"); // 根据自己的情况, 设置password
//        Properties prop = new Properties();
//        prop.put(" mail.smtp.auth ", " true "); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
//        prop.put(" mail.smtp.timeout ", " 25000 ");
//        senderImpl.setJavaMailProperties(prop);
//        // 发送邮件
//        mailMessage.setTo("w871326692@163.com");
//        senderImpl.send(mailMessage);
//        System.out.println("send OK by lkmgydx");


        MailSender.sendMail("nihaodddddddd","title","w871326692@163.com");
    }
}
