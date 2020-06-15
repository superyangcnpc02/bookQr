package com.yxtech.sys.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "t_qr_export")
public class QrExport {
    /**
     * 刮刮乐二维码导出统计
     */
    @Id
    private Integer id;

    /**
     * 刮刮乐二维码id
     */
    @Column(name = "qr_Id")
    private Integer qrId;

    /**
     * 导出数量
     */
    private Integer num;

    @Column(name = "creator_Id")
    private Integer creatorId;

    @Column(name = "creator_Account")
    private String creatorAccount;

    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 1 刮刮乐 2 权限二维码
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 获取刮刮乐二维码导出统计
     *
     * @return id - 刮刮乐二维码导出统计
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置刮刮乐二维码导出统计
     *
     * @param id 刮刮乐二维码导出统计
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取刮刮乐二维码id
     *
     * @return qr_Id - 刮刮乐二维码id
     */
    public Integer getQrId() {
        return qrId;
    }

    /**
     * 设置刮刮乐二维码id
     *
     * @param qrId 刮刮乐二维码id
     */
    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    /**
     * 获取导出数量
     *
     * @return num - 导出数量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置导出数量
     *
     * @param num 导出数量
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * @return creator_Id
     */
    public Integer getCreatorId() {
        return creatorId;
    }

    /**
     * @param creatorId
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * @return creator_Account
     */
    public String getCreatorAccount() {
        return creatorAccount;
    }

    /**
     * @param creatorAccount
     */
    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    /**
     * @return create_Time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}