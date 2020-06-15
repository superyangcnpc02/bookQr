package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_permission")
public class Perm {
    @Id
    private Integer id;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 角色ID
     */
    @Column(name = "role_Id")
    private Integer roleId;

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
     * 获取访问路径
     *
     * @return url - 访问路径
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置访问路径
     *
     * @param url 访问路径
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取角色ID
     *
     * @return role_Id - 角色ID
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * 设置角色ID
     *
     * @param roleId 角色ID
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}