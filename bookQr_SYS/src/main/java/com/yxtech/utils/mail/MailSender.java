package com.yxtech.utils.mail;


import com.yxtech.sys.controller.MailController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件发送相关
 * @author hesufang
 *
 */
@Component
public class MailSender {
	private static final Logger log = LoggerFactory.getLogger(MailSender.class);
	private static JavaMailSenderImpl javaMailSender;

	private static String exchange;

	private static String dataBaseKey;

	@Autowired
	private YunEmail yunEmail;

	public  String getExchange() {
		return exchange;
	}

	public  void setExchange(String exchange) {
		MailSender.exchange = exchange;
	}

	public  String getDataBaseKey() {
		return dataBaseKey;
	}

	public  void setDataBaseKey(String dataBaseKey) {
		MailSender.dataBaseKey = dataBaseKey;
	}


	/**
	 * 给1-n个人发 普通文本 邮件
	 * @param content 邮件内容
	 * @param subject 邮件主题
	 * @param toMail  邮件接收者 1-n个人 可以传数组 
	 */
	public static void sendMail(String content,String subject,String...toMail){
		try {
			String nick = MimeUtility.encodeText("文泉云盘");
			SimpleMailMessage smm = new SimpleMailMessage();
			smm.setFrom(nick +"<"+javaMailSender.getUsername()+">");
			smm.setSubject(subject);
			smm.setText(content);
			smm.setTo(toMail);
			javaMailSender.send(smm);
			System.out.println("from=="+smm.getFrom());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * 富文本形式发送邮件 发送html
	 * @param content 邮件内容
	 * @param subject 邮件主题
	 * @param toMail  邮件接收者 1-n个人 可以传数组 
	 */
	public  void sendRichTextMail(String content,String subject,String...toMail){
//		MimeMessage msg = javaMailSender.createMimeMessage();
//	    MimeMessageHelper helper;
//		try {
//			String nick = MimeUtility.encodeText("文泉云盘");
//			helper = new MimeMessageHelper(msg, true);
//			helper.setFrom(nick +"<"+javaMailSender.getUsername()+">");
//		    helper.setTo(toMail);
//		    helper.setSubject(subject);
//		    helper.setText(content,true);
//		    javaMailSender.send(msg);
//			System.out.println("from=="+nick +"<"+javaMailSender.getUsername()+">");

//			EmailVo emailVo = new EmailVo(toMail[0], subject, content);

		yunEmail.sendEmail(toMail[0], subject, content);

//		}catch (AuthenticationFailedException e){
//			log.error("发送邮件失败！",e);
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

	}

	/**
	 * 富文本形式发送邮件 发送html
	 * @param content 邮件内容
	 * @param subject 邮件主题
	 * @param toMail  邮件接收者 1-n个人 可以传数组
	 */
	public static void sendRichTextMailOld(String content,String subject,String...toMail){
		MimeMessage msg = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper;
		try {
			String nick = MimeUtility.encodeText("文泉云盘");
			helper = new MimeMessageHelper(msg, true);
			helper.setFrom(nick +"<"+javaMailSender.getUsername()+">");
		    helper.setTo(toMail);
		    helper.setSubject(subject);
		    helper.setText(content,true);
		    javaMailSender.send(msg);
			System.out.println("from=="+nick +"<"+javaMailSender.getUsername()+">");
		}catch (AuthenticationFailedException e){
			log.error("发送邮件失败！",e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	

	
	/**
	 * 富文本 发送带附件邮件
	 * @param content
	 * @param subject
	 * @param attachmentMap  key 附件显示名称  value InputStream 附件文件 (抽象类)
	 */
	public static void sendMailWithAttachments(String content,String subject,final Map<String,InputStream> attachmentMap,String...toMail){
		MimeMessage msg = javaMailSender.createMimeMessage();
		//第二个参数为true 说明发送html
	    MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(msg, true);
			helper.setFrom(javaMailSender.getUsername());
		    helper.setTo(toMail);
		    helper.setSubject(subject);
		    helper.setText(content,true);
		    if(attachmentMap!=null && !attachmentMap.isEmpty()){
		    	for(final String key:attachmentMap.keySet()){
		    		helper.addAttachment(new  String(key.getBytes( "iso-8859-1"), javaMailSender.getDefaultEncoding()),
		    				new  InputStreamSource() {
				    			@Override
				    			public InputStream getInputStream() throws IOException {
				    				return attachmentMap.get(key);
				    			}
				    		}
		    		);
		    	}
		    }
		    
		    javaMailSender.send(msg);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    public static JavaMailSenderImpl getJavaMailSender() {
        return javaMailSender;
    }

    public static void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
        MailSender.javaMailSender = javaMailSender;
    }

    /*加载email配置*/
    static {

        javaMailSender = new JavaMailSenderImpl();
        Properties prop = new Properties();
        InputStream in = MailController.class.getResourceAsStream("/config/email.properties");
        try {
            prop.load(in);
            // 设定mail server
            javaMailSender.setHost(prop.getProperty("serverHost").trim());
            javaMailSender.setPassword(prop.getProperty("password").trim());
            javaMailSender.setDefaultEncoding("utf-8");
            javaMailSender.setUsername(prop.getProperty("username").trim());
            javaMailSender.setJavaMailProperties(prop);

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
	
}
