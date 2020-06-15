package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_client_textbook")
public class ClientBook {
    @Id
    private Integer id;

    /**
     * 曾经出版的教材名称
     */
    private String name;

    /**
     * 曾经出版教材的出版社
     */
    private String press;

    /**
     * 出版日期
     */
    @Column(name = "prss_Time")
    private String prssTime;

    /**
     * 客户ID
     */
    @Column(name = "client_Id")
    private Integer clientId;

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
     * 获取曾经出版的教材名称
     *
     * @return name - 曾经出版的教材名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置曾经出版的教材名称
     *
     * @param name 曾经出版的教材名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取曾经出版教材的出版社
     *
     * @return press - 曾经出版教材的出版社
     */
    public String getPress() {
        return press;
    }

    /**
     * 设置曾经出版教材的出版社
     *
     * @param press 曾经出版教材的出版社
     */
    public void setPress(String press) {
        this.press = press;
    }

    /**
     * 获取出版日期
     *
     * @return prss_Time - 出版日期
     */
    public String getPrssTime() {
        return prssTime;
    }

    /**
     * 设置出版日期
     *
     * @param prssTime 出版日期
     */
    public void setPrssTime(String prssTime) {
        this.prssTime = prssTime;
    }

    /**
     * 获取客户ID
     *
     * @return client_Id - 客户ID
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * 设置客户ID
     *
     * @param clientId 客户ID
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}