package com.yxtech.sys.vo.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.Book;

import java.util.Date;

/**
 * @author cuihao
 * @create 2017-05-26-18:08
 */

public class ClientBookDetailVo {
    private Integer id;

    /**
     * 作者
     */
    private String author;
    /**
     * 产品码
     */
    private String code;

    /**
     * 图书分类
     */
    private String category;


    /**
     * ISBN
     */
    private String isbn;

    /**
     * 书名
     */
    private String name;

    /**
     * 出版社
     */
    private String press;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 编辑
     */
    private String editor;


    private String qrName;

    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ClientBookDetailVo(){}
    public ClientBookDetailVo(Book book){
        this.setId(book.getId());
        this.setAuthor(book.getAuthor());
        this.setCode(book.getCode());
        this.setCategory(book.getCategory());
        this.setIsbn(book.getIsbn());
        this.setName(book.getName());
        this.setPress(book.getPress());
        this.setEditor(book.getEditor());
    }
}
