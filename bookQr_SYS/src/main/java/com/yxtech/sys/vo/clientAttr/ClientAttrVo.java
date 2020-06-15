package com.yxtech.sys.vo.clientAttr;

import com.yxtech.sys.domain.ClientAttr;

/**
 * Created by zml on 2016/9/13.
 */
public class ClientAttrVo extends ClientAttr {
    // 学校
    private String school;
    // 图书名称
    private String bookName;
    // 职务
    private String job;
    // 专业方向
    private String major;
    // 客户名称
    private String name;
    // 联系电话
    private String phone;

    public ClientAttrVo(ClientAttr clientAttr){
        this.setStatus(clientAttr.getStatus());
        this.setType(clientAttr.getType());
        this.setBookId(clientAttr.getBookId());
        this.setClientId(clientAttr.getClientId());
        this.setEmail(clientAttr.getEmail());
        this.setId(clientAttr.getId());
        this.setPassword(clientAttr.getPassword());
        this.setTarget(clientAttr.getTarget());
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
