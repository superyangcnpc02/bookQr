package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_adv_book_relation")
public class AdvBookRelation {
    /**
     * 广告位图书关系表
     */
    @Id
    private Integer id;

    /**
     * 广告位ID
     */
    @Column(name = "adv_id")
    private Integer advId;

    /**
     * 图书ID
     */
    @Column(name = "book_id")
    private Integer bookId;

    /**
     * 绑定时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 获取广告位图书关系表
     *
     * @return id - 广告位图书关系表
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置广告位图书关系表
     *
     * @param id 广告位图书关系表
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取广告位ID
     *
     * @return adv_id - 广告位ID
     */
    public Integer getAdvId() {
        return advId;
    }

    /**
     * 设置广告位ID
     *
     * @param advId 广告位ID
     */
    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    /**
     * 获取图书ID
     *
     * @return book_id - 图书ID
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
     * 获取绑定时间
     *
     * @return create_time - 绑定时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置绑定时间
     *
     * @param createTime 绑定时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}