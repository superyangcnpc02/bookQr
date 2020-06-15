package com.yxtech.sys.vo.user;

/**
 * 获取用户对二维码的审核状态
 * Created by liukailong on 2016/10/10.
 */
public class UserStatusForAutoVO {

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserStatusForAutoVO() {
    }

    public UserStatusForAutoVO(int status) {
        this.status = status;
    }
}
