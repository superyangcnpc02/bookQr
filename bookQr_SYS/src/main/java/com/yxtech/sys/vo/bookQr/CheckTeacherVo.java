package com.yxtech.sys.vo.bookQr;

/**
 * desc:  <br>
 * date: 2019/6/6 0006
 *
 * @author cuihao
 */
public class CheckTeacherVo {
    private Integer status;
    private String email;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CheckTeacherVo() {
    }

    public CheckTeacherVo(Integer status, String email) {
        this.status = status;
        this.email = email;
    }
}
