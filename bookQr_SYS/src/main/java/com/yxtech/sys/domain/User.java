package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_user")
public class User implements Serializable{

    @Id
    private Integer id;

    /**
     * 登陆次数
     */
    @Column(name = "user_Id")
    private String userId;

    /**
     * 邮箱 即账号
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 最后登录时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "login_Time")
    private Date loginTime;

    /**
     * 登陆次数
     */
    @Column(name = "login_Num")
    private Integer loginNum;

    /**
     * 电话
     */
    private String phone;

    /**
     * 用户名称
     */
    @Column(name = "user_Name")
    private String userName;

    /**
     * 0：管理员；1：编辑员；2：总编  3:审核员
     */
    private Integer role;

    /**
     * 头像路径
     */
    private String photo;

    /**
     * 当前组织机构ID
     */
    @Column(name = "org_Id")
    private Integer orgId;

    /**
     * 分社ID
     */
    @Column(name = "press_Id")
    private Integer pressId;

    /**
     *  1:正常2:未激活 3 删除 4待审核
     */
    @Column(name = "status")
    private Integer status;

    @Transient
    private String press;

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
     * 获取邮箱 即账号
     *
     * @return email - 邮箱 即账号
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱 即账号
     *
     * @param email 邮箱 即账号
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取最后登录时间
     *
     * @return login_Time - 最后登录时间
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置最后登录时间
     *
     * @param loginTime 最后登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 获取登陆次数
     *
     * @return login_Num - 登陆次数
     */
    public Integer getLoginNum() {
        return loginNum;
    }

    /**
     * 设置登陆次数
     *
     * @param loginNum 登陆次数
     */
    public void setLoginNum(Integer loginNum) {
        this.loginNum = loginNum;
    }

    /**
     * 获取电话
     *
     * @return phone - 电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     *
     * @param phone 电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取用户名称
     *
     * @return user_Name - 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名称
     *
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取0：管理员；1：编辑员；2：总编  3:审核员
     *
     * @return role - 0：管理员；1：编辑员；2：总编  3:审核员
     */
    public Integer getRole() {
        return role;
    }

    /**
     * 设置0：管理员；1：编辑员；2：总编  3:审核员
     *
     * @param role 0：管理员；1：编辑员；2：总编  3:审核员
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * 获取头像路径
     *
     * @return photo - 头像路径
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * 设置头像路径
     *
     * @param photo 头像路径
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * 获取当前组织机构ID
     *
     * @return org_Id - 当前组织机构ID
     */
    public Integer getOrgId() {
        return orgId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * 设置当前组织机构ID
     *
     * @param orgId 当前组织机构ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public User(Integer id,Integer role, String photo,String userName,String phone,Integer pressId,String press) {
        this.id = id;
        this.role = role;
        this.userName = userName;
        this.photo = photo;
        this.phone=phone;
        this.pressId=pressId;
        this.press=press;
    }

    public User() {
    }

    public Integer getPressId() {
        return pressId;
    }

    public void setPressId(Integer pressId) {
        this.pressId = pressId;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}