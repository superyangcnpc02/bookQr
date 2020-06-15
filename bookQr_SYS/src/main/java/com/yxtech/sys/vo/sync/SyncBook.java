package com.yxtech.sys.vo.sync;

import java.util.List;

/**
 * create by zml on 2017/12/14 10:21
 */
public class SyncBook {

    private Integer code;
    private String message;
    private Integer page;
    private Integer count;
    private Integer total;
    private List<BookInfo> books;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<BookInfo> getBooks() {
        return books;
    }

    public void setBooks(List<BookInfo> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "SyncBook{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", page=" + page +
                ", count=" + count +
                ", total=" + total +
                ", books=" + books +
                '}';
    }
}
