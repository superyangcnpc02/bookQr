package com.yxtech.sys.vo.user;

/**
 * desc:  <br>
 * date: 2019/8/1 0001
 *
 * @author cuihao
 */
public class UpdateClientInfoVo {
    private Integer id;
    private int qrcodeId;
    private String email;
    private String phone;
    private String school;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(int qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
