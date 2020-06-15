package com.yxtech.sys.vo;

/**
 * Created by hesufang on 2015/10/19.
 */
public class SendEmailVo {

    private  Integer  id;//二维码id
    private String url;//网址
    private String email;//邮箱


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
