package com.yxtech.utils.mail;

import java.io.Serializable;

/**
 * @author cuihao
 * @create 2017-06-02-8:29
 */

public class EmailVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String toEmail;
    private String subject;
    private String content;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public EmailVo(){}
    public EmailVo(String toEmail, String subject, String content){
        this.toEmail = toEmail;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public String toString() {
        return "[toEmail="+toEmail+" subject="+subject+" content="+content+"]";
    }
}
