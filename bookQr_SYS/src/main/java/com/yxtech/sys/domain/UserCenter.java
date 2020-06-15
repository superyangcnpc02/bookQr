package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_user_center")
public class UserCenter {
    @Id
    private Integer id;

    /**
     * 微信用户的唯一标识
     */
    private String openid;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "create_time")
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
     * 获取微信用户的唯一标识
     *
     * @return openid - 微信用户的唯一标识
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * 设置微信用户的唯一标识
     *
     * @param openid 微信用户的唯一标识
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}