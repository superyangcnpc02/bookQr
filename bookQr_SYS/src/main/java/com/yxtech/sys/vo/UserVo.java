package com.yxtech.sys.vo;

import com.yxtech.sys.domain.User;

/**
 * Created by Administrator on 2015/10/10.
 */
public class UserVo extends User {


    private  String newPassword;//新密码


    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
