package com.yxtech.sys.vo.bookQr;

import com.yxtech.sys.domain.Res;

import java.util.List;

/**
 * Created by Chenxh on 2015/10/16.
 */
public class BookQrDetailVO {
    private String bookName;
    private String name;
    private String note;
    private String url;
    private String netUrl;
    private String bookaskUrl;//书问地址
    private List<Res> resource;
    private String openId;
    private String unionid;
    private String openStatus;
    private String openEmail;
    private Integer zipStatus;
    private Integer rightStatus;
    private Integer canRead;
    private Integer categoryId;
    //课件下载提示
    private String  tip;
    //是否直接发送客户 1：否 2：是
    private int sendCustomer;
    private Integer onlyWebchat;

    //图书信息
    private String author;
    private String code;
    private String press;
    private String remark;

    //扫码的用户手机号(只有课件二维码有用)
    private String phone;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getOpenEmail() {
        return openEmail;
    }

    public void setOpenEmail(String openEmail) {
        this.openEmail = openEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Res> getResource() {
        return resource;
    }

    public void setResource(List<Res> resource) {
        this.resource = resource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBookaskUrl() {
        return bookaskUrl;
    }

    public void setBookaskUrl(String bookaskUrl) {
        this.bookaskUrl = bookaskUrl;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public Integer getZipStatus() {
        return zipStatus;
    }

    public void setZipStatus(Integer zipStatus) {
        this.zipStatus = zipStatus;
    }

    public Integer getRightStatus() {
        return rightStatus;
    }

    public void setRightStatus(Integer rightStatus) {
        this.rightStatus = rightStatus;
    }

    public Integer getCanRead() {
        return canRead;
    }

    public void setCanRead(Integer canRead) {
        this.canRead = canRead;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getOnlyWebchat() {
        return onlyWebchat;
    }

    public void setOnlyWebchat(Integer onlyWebchat) {
        this.onlyWebchat = onlyWebchat;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getSendCustomer() {
        return sendCustomer;
    }

    public void setSendCustomer(int sendCustomer) {
        this.sendCustomer = sendCustomer;
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

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}

