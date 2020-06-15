package com.yxtech.sys.vo.book;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.Book;

import java.util.Date;

/**
 * Created by zml on 2017/8/1.
 */
public class BookPowerVO {

    private String cover;       // 封面图片
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;    // 创建时间
    private Integer id;         // 图书 ID
    private String name;        // 图书名称

    public BookPowerVO(){}

    public BookPowerVO(Book book){
        this.setId(book.getId());
        this.setName(book.getName());
        this.setCreateTime(book.getCreateTime());
        this.setCover(book.getCover());
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
