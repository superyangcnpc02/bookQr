package com.yxtech.sys.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.vo.resource.ResourceDetail;

import java.util.Date;

/**
 * @author cuihao
 * @create 2016-09-10-10:43
 */

public class ReportListVo {

    private Integer id;

    private Integer bookId;

    private String bookName;

    private Integer qrId;

    private Integer tag;

    private String tagName;

    private Integer reportType;

    private String qrName;

    private String remark;


    private ResourceDetail resourceDetail;

    private String resourceName;

    private String resTypeName;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String reportTime;

    private Integer status;

    private String statusInfo;

    private String email;

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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ResourceDetail getResourceDetail() {
        return resourceDetail;
    }

    public void setResourceDetail(ResourceDetail resourceDetail) {
        this.resourceDetail = resourceDetail;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getResTypeName() {
        return resTypeName;
    }

    public void setResTypeName(String resTypeName) {
        this.resTypeName = resTypeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }
}
