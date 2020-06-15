package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_sys_config")
public class SysConfig {
    /**
     * 主键id
     */
    @Id
    private Integer id;

    /**
     * 类型 1：同步时间
     */
    private Integer type;

    /**
     * 记录同步时间
     */
    @Column(name = "sync_time")
    private Date syncTime;

    /**
     * 说明
     */
    private String remark;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取类型 1：同步时间
     *
     * @return type - 类型 1：同步时间
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型 1：同步时间
     *
     * @param type 类型 1：同步时间
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取记录同步时间
     *
     * @return sync_time - 记录同步时间
     */
    public Date getSyncTime() {
        return syncTime;
    }

    /**
     * 设置记录同步时间
     *
     * @param syncTime 记录同步时间
     */
    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    /**
     * 获取说明
     *
     * @return remark - 说明
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置说明
     *
     * @param remark 说明
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return create_time
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

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}