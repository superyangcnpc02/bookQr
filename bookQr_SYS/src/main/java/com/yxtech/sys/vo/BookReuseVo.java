package com.yxtech.sys.vo;

/**
 * @author cuihao
 * @create 2017-11-23-16:59
 */

public class BookReuseVo {
    private Integer newBookId;
    private Integer newQuoteId;

    public Integer getNewBookId() {
        return newBookId;
    }

    public void setNewBookId(Integer newBookId) {
        this.newBookId = newBookId;
    }

    public Integer getNewQuoteId() {
        return newQuoteId;
    }

    public void setNewQuoteId(Integer newQuoteId) {
        this.newQuoteId = newQuoteId;
    }

    public BookReuseVo(Integer newBookId, Integer newQuoteId) {
        this.newBookId = newBookId;
        this.newQuoteId = newQuoteId;
    }

    public BookReuseVo() {
    }
}
