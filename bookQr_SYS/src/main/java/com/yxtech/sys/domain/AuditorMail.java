package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_auditor_mail")
public class AuditorMail {
    @Id
    private Integer id;

    /**
     * 图书ID
     */
    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 审核邮箱
     */
    @Column(name = "auditor_Email")
    private String auditorEmail;

    /**
     * 提示信息
     */
    private String tip;

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
     * 获取图书ID
     *
     * @return book_Id - 图书ID
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * 设置图书ID
     *
     * @param bookId 图书ID
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取审核邮箱
     *
     * @return auditor_Email - 审核邮箱
     */
    public String getAuditorEmail() {
        return auditorEmail;
    }

    /**
     * 设置审核邮箱
     *
     * @param auditorEmail 审核邮箱
     */
    public void setAuditorEmail(String auditorEmail) {
        this.auditorEmail = auditorEmail;
    }

    /**
     * 获取提示信息
     *
     * @return tip - 提示信息
     */
    public String getTip() {
        return tip;
    }

    /**
     * 设置提示信息
     *
     * @param tip 提示信息
     */
    public void setTip(String tip) {
        this.tip = tip;
    }
}