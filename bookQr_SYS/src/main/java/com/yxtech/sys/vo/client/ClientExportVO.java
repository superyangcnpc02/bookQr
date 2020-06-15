package com.yxtech.sys.vo.client;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 用于客户信息导出VO
 * Created by hesufang on 2016/3/17.
 * update by zml on 2017/08/24
 */
public class ClientExportVO{

    /**
     * 申请密码所发送到的邮箱
     */
    private String email;

    /**
     * 姓名
     */
    private String clientName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 院系名称
     */
    private String depart;

    /**
     * 所在地
     */
    private String seat;

    /**
     * 教授课程名
     */
    private String lesson;

    /**
     * 产品编号
     */
    private String code;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 作者
     */
    private String author;

    /**
     * 编辑
     */
    private String editor;

    /**
     * 出版社
     */
    private String press;

    /**
     * 图书分类
     */
    private String category;

    /**
     * ISBN
     */
    private String isbn;

    /**
     * 二维码名称
     */
    private String qrName;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    private Date createTime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
