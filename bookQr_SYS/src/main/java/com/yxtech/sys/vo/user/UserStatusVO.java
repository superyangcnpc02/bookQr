package com.yxtech.sys.vo.user;

/**
 * 获取用户对二维码的审核状态
 * Created by liukailong on 2016/10/10.
 */
public class UserStatusVO {

    private String status;
    private String email;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatusVO() {
    }

    public UserStatusVO(String status, String email) {
        this.status = status;
        this.email = email;
    }
}
