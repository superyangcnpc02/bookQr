package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

@Table(name = "t_client_attribute")
public class ClientAttr {
    @Id
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "client_Id")
    private Integer clientId;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 书籍ID
     */
    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 二维码ID
     */
    @Column(name = "qr_Id")
    private Integer qrId;


    /**
     * 索取此书教学资源的目的
     */
    private String target;

    /**
     * 1:待审核  2：审核通过（发送邮件）3：不通过
     */
    private Integer status;

    /**
     * 驳回原因
     */
    private String reason;

    /**
     * 1:人工审核查看资源 2;自动审核查看资源
     */
    private Integer type;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_Time")
    private Date createTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return client_Id - 用户ID
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * 设置用户ID
     *
     * @param clientId 用户ID
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * 获取用户邮箱
     *
     * @return email - 用户邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户邮箱
     *
     * @param email 用户邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取书籍ID
     *
     * @return book_Id - 书籍ID
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * 设置书籍ID
     *
     * @param bookId 书籍ID
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取索取此书教学资源的目的
     *
     * @return target - 索取此书教学资源的目的
     */
    public String getTarget() {
        return target;
    }

    /**
     * 设置索取此书教学资源的目的
     *
     * @param target 索取此书教学资源的目的
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 获取1:待审核  2：审核通过（发送邮件）3：不通过
     *
     * @return status - 1:待审核  2：审核通过（发送邮件）3：不通过
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:待审核  2：审核通过（发送邮件）3：不通过
     *
     * @param status 1:待审核  2：审核通过（发送邮件）3：不通过
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取1:查看资源 2;申请样书
     *
     * @return type - 1:查看资源 2;申请样书
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置1:查看资源 2;申请样书
     *
     * @param type 1:查看资源 2;申请样书
     */
    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}