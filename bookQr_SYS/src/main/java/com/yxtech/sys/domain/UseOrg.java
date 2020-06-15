package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_use_org")
public class UseOrg {
    @Id
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_Id")
    private Integer userId;

    /**
     * 组织机构ID
     */
    @Column(name = "org_Id")
    private Integer orgId;

    /**
     * 1：新增 2：删除 3：编辑 4：查询
     */
    @Column(name = "method_Id")
    private Integer methodId;

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
     * @return user_Id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取组织机构ID
     *
     * @return org_Id - 组织机构ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织机构ID
     *
     * @param orgId 组织机构ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取1：新增 2：删除 3：编辑 4：查询
     *
     * @return method_Id - 1：新增 2：删除 3：编辑 4：查询
     */
    public Integer getMethodId() {
        return methodId;
    }

    /**
     * 设置1：新增 2：删除 3：编辑 4：查询
     *
     * @param methodId 1：新增 2：删除 3：编辑 4：查询
     */
    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public UseOrg(Integer userId, Integer orgId) {
        this.userId = userId;
        this.orgId = orgId;
    }

    public UseOrg(Integer userId, Integer orgId, Integer methodId) {
        this.userId = userId;
        this.orgId = orgId;
        this.methodId = methodId;
    }

    public UseOrg() {
    }
}