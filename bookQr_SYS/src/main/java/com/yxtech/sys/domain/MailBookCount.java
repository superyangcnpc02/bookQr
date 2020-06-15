package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_mail_book_count")
public class MailBookCount {
    @Id
    private Integer id;

    /**
     * 邮箱ID
     */
    @Column(name = "mail_Id")
    private Integer mailId;

    /**
     * 书籍ID
     */
    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 发送次数
     */
    private Integer num;

    /**
     * 课件下载次数
     */
    @Column(name = "down_Num")
    private Integer downNum;

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
     * 获取邮箱ID
     *
     * @return mail_Id - 邮箱ID
     */
    public Integer getMailId() {
        return mailId;
    }

    /**
     * 设置邮箱ID
     *
     * @param mailId 邮箱ID
     */
    public void setMailId(Integer mailId) {
        this.mailId = mailId;
    }

    /**
     * 获取书籍ID
     *
     * @return book_Id - 书籍ID
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * 设置书籍ID
     *
     * @param bookId 书籍ID
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取发送次数
     *
     * @return num - 发送次数
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置发送次数
     *
     * @param num 发送次数
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 获取课件下载次数
     *
     * @return down_Num - 课件下载次数
     */
    public Integer getDownNum() {
        return downNum;
    }

    /**
     * 设置课件下载次数
     *
     * @param downNum 课件下载次数
     */
    public void setDownNum(Integer downNum) {
        this.downNum = downNum;
    }
}