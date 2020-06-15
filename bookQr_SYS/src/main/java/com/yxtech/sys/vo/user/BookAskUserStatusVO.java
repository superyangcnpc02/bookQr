package com.yxtech.sys.vo.user;

/**
 * 获取用户对二维码的审核状态
 * Created by liukailong on 2016/10/10.
 */
public class BookAskUserStatusVO {

    private Integer status;

    private String url;

    private String email;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BookAskUserStatusVO() {
    }

    public BookAskUserStatusVO(Integer status, String url, String email) {
        this.status = status;
        this.url = url;
        this.email = email;
    }
}
