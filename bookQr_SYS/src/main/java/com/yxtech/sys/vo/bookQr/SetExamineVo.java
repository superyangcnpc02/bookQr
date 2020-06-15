package com.yxtech.sys.vo.bookQr;

/**
 * @author cuihao
 * @create 2017-07-03-15:39
 */

public class SetExamineVo {
    private Integer bookId;
    private Integer flag;//1人工审核2自动

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
