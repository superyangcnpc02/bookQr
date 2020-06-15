package com.yxtech.sys.vo.bookQr;

/**
 * @author cuihao
 * @create 2017-12-12-10:52
 */

public class AddWebQr {
    private String name;//链接名称
    private String netUrl;//链接地址
    private Integer qrType;//二维码类型,只有	1：课件资源；2：扩展资源
    private Integer bookId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public Integer getQrType() {
        return qrType;
    }

    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
