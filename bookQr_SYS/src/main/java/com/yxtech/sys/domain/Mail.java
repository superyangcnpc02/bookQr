package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_mail")
public class Mail {
    @Id
    private Integer id;

    private String email;

    /**
     * 最后发送时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date time;

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
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取最后发送时间
     *
     * @return time - 最后发送时间
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置最后发送时间
     *
     * @param time 最后发送时间
     */
    public void setTime(Date time) {
        this.time = time;
    }
}