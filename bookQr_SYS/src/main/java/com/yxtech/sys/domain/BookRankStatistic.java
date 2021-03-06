package com.yxtech.sys.domain;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *  扫码统计
 * @author wzf
 * @date 2019/04/18
 */
@Table(name = "t_book_rank_statistic")
public class BookRankStatistic implements Cloneable,Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Integer id;

    private Integer bookId;

    private Integer num;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BookRankStatistic{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", num=" + num +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}