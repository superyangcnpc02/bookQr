package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_qr_auth")
public class BookQrAuth {
    /**
     * 二维码ID
     */
    @Id
    private Integer id;

    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 二维码存储路径/文件ID
     */
    private String url;

    /**
     * 二维码名称
     */
    private String name;

    /**
     * 拥有者ID
     */
    private Integer onwer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 获取二维码ID
     *
     * @return id - 二维码ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置二维码ID
     *
     * @param id 二维码ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return book_Id
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * @param bookId
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取二维码存储路径/文件ID
     *
     * @return url - 二维码存储路径/文件ID
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置二维码存储路径/文件ID
     *
     * @param url 二维码存储路径/文件ID
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取二维码名称
     *
     * @return name - 二维码名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置二维码名称
     *
     * @param name 二维码名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取拥有者ID
     *
     * @return onwer - 拥有者ID
     */
    public Integer getOnwer() {
        return onwer;
    }

    /**
     * 设置拥有者ID
     *
     * @param onwer 拥有者ID
     */
    public void setOnwer(Integer onwer) {
        this.onwer = onwer;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}