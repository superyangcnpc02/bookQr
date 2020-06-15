package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_client_plan")
public class ClientPlan {
    @Id
    private Integer id;

    /**
     * 预计出版量
     */
    private Integer num;

    @Column(name = "client_Id")
    private Integer clientId;

    /**
     * 教材名称
     */
    private String name;

    /**
     * 出版时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "publish_Time")
    private Date publishTime;

    /**
     * 1:计划中 2写作中 3已定稿 4有讲义
     */
    private Integer status;

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
     * 获取预计出版量
     *
     * @return num - 预计出版量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置预计出版量
     *
     * @param num 预计出版量
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * @return client_Id
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * 获取教材名称
     *
     * @return name - 教材名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置教材名称
     *
     * @param name 教材名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取出版时间
     *
     * @return publish_Time - 出版时间
     */
    public Date getPublishTime() {
        return publishTime;
    }

    /**
     * 设置出版时间
     *
     * @param publishTime 出版时间
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    /**
     * 获取1:计划中 2写作中 3已定稿 4有讲义
     *
     * @return status - 1:计划中 2写作中 3已定稿 4有讲义
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:计划中 2写作中 3已定稿 4有讲义
     *
     * @param status 1:计划中 2写作中 3已定稿 4有讲义
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}