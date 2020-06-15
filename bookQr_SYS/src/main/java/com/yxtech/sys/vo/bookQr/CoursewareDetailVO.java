package com.yxtech.sys.vo.bookQr;

import com.yxtech.sys.domain.FileRes;

import java.util.List;

/**
 * 课件二维码明细
 * Created by hesufang on 2015/11/3.
 */
public class CoursewareDetailVO {

    //作者
    private String author;

    //是否审核 1：否 2：是
    private int check;

    //产品码
    private String code;

    //封面图片文件ID
    private String cover;

    //isbn
    private String isbn;

    //书名
    private String name;

    //出版社
    private String press;

    //简介
    private String remark;

    //课件下载提示
    private String  tip;

    //二维码图片地址
    private String url;

    private String netUrl;

    //书问地址
    private String bookaskUrl;


    //对应的课件资源
    private List<FileRes>  resource;


    //是否直接发送客户 1：否 2：是
    private int sendCustomer;

    private String openId;
    private String openStatus;
    private String openEmail;
    private Integer zipStatus;
    private Integer onlyWebchat;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public List<FileRes> getResource() {
        return resource;
    }

    public void setResource(List<FileRes> resource) {
        this.resource = resource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSendCustomer() {
        return sendCustomer;
    }

    public void setSendCustomer(int sendCustomer) {
        this.sendCustomer = sendCustomer;
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

    public String getOpenEmail() {
        return openEmail;
    }

    public void setOpenEmail(String openEmail) {
        this.openEmail = openEmail;
    }

    public Integer getZipStatus() {
        return zipStatus;
    }

    public void setZipStatus(Integer zipStatus) {
        this.zipStatus = zipStatus;
    }

    public Integer getOnlyWebchat() {
        return onlyWebchat;
    }

    public void setOnlyWebchat(Integer onlyWebchat) {
        this.onlyWebchat = onlyWebchat;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }
}

