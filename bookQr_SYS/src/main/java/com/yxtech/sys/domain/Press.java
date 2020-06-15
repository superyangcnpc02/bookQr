package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_press")
public class Press {
    /**
     * 出版社id
     */
    @Id
    private Integer id;

    /**
     * 出版社名称
     */
    private String name;

    /**
     * 出版社简介
     */
    private String remark;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 创建人id
     */
    @Column(name = "create_Id")
    private Integer createId;

    /**
     * 获取出版社id
     *
     * @return id - 出版社id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置出版社id
     *
     * @param id 出版社id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取出版社名称
     *
     * @return name - 出版社名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置出版社名称
     *
     * @param name 出版社名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取出版社简介
     *
     * @return remark - 出版社简介
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置出版社简介
     *
     * @param remark 出版社简介
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取创建时间
     *
     * @return create_Time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取创建人id
     *
     * @return create_Id - 创建人id
     */
    public Integer getCreateId() {
        return createId;
    }

    /**
     * 设置创建人id
     *
     * @param createId 创建人id
     */
    public void setCreateId(Integer createId) {
        this.createId = createId;
    }
}