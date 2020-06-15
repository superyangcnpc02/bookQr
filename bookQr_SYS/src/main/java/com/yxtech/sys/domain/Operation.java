package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_operation")
public class Operation {
    @Id
    private Integer id;

    /**
     * 账号
     */
    private String email;

    /**
     * 1图书2二维码3资源
     */
    private Integer type;

    /**
     * 根据type对应的id
     */
    @Column(name = "obj_id")
    private Integer objId;

    /**
     * 操作者ip
     */
    private String ip;

    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 内容
     */
    private String content;

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
     * 获取账号
     *
     * @return email - 账号
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置账号
     *
     * @param email 账号
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取1图书2二维码3资源
     *
     * @return type - 1图书2二维码3资源
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置1图书2二维码3资源
     *
     * @param type 1图书2二维码3资源
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取根据type对应的id
     *
     * @return obj_id - 根据type对应的id
     */
    public Integer getObjId() {
        return objId;
    }

    /**
     * 设置根据type对应的id
     *
     * @param objId 根据type对应的id
     */
    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    /**
     * 获取操作者ip
     *
     * @return ip - 操作者ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置操作者ip
     *
     * @param ip 操作者ip
     */
    public void setIp(String ip) {
        this.ip = ip;
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

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}