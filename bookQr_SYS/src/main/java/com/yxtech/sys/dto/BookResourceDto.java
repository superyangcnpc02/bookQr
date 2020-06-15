package com.yxtech.sys.dto;

/**
 * 图书资源DTO
 * @author wzf
 * @date 2019/04/30
 */
public class BookResourceDto {
    private Integer bookId;
    private Integer qrType;
    private Integer resId;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQrType() {
        return qrType;
    }

    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }
}
