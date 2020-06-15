package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.util.Date;

@Table(name = "t_resources")
public class Res {
    @Id
    private Integer id;

    /**
     * 文件类型 1文本， 2图片，3视频，4音频，5其他
     */
    @Column(name = "file_Type")
    private Integer fileType;

    /**
     * 排序
     */
    private Integer sorting;

    /**
     * 拥有者ID
     */
    private Integer onwer;

    /**
     * 1：不保密 2：保密
     */
    private Integer secrecy;

    /**
     * 文件存储路径
     */
    @Column(name = "file_Uuid")
    private String fileUuid;

    /**
     * 二维码ID
     */
    @Column(name = "qr_Id")
    private Integer qrId;

    /**
     * 是否直接发送客户 1：否 2：是
     */
    @Column(name = "is_send")
    private Integer isSend;

    /**
     * 下载次数
     */
    private Integer num;

    /**
     * 资源列表排序
     */
    private Integer indexs;

    /**
     * txt文件内容
     */

    private String text;


    /**
     * 是否可以下载  false：否   true：是
     */
    @Column(name = "down_Load")
    private Boolean downLoad;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 资源下载地址
     */
    @Transient
    private String downUrl;

    /**
     * 视频资源状态
     */
    @Transient
    private int status;


    public Boolean getDownLoad() {
        return downLoad;
    }

    public void setDownLoad(Boolean downLoad) {
        this.downLoad = downLoad;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取文件类型 1文本， 2图片，3视频，4音频，5其他
     *
     * @return file_Type - 文件类型 1文本， 2图片，3视频，4音频，5其他
     */
    public Integer getFileType() {
        return fileType;
    }

    /**
     * 设置文件类型 1文本， 2图片，3视频，4音频，5其他
     *
     * @param fileType 文件类型 1文本， 2图片，3视频，4音频，5其他
     */
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取排序
     *
     * @return sorting - 排序
     */
    public Integer getSorting() {
        return sorting;
    }

    /**
     * 设置排序
     *
     * @param sorting 排序
     */
    public void setSorting(Integer sorting) {
        this.sorting = sorting;
    }

    /**
     * 获取拥有者ID
     *
     * @return onwer - 拥有者ID
     */
    public Integer getOnwer() {
        return onwer;
    }

    /**
     * 设置拥有者ID
     *
     * @param onwer 拥有者ID
     */
    public void setOnwer(Integer onwer) {
        this.onwer = onwer;
    }

    /**
     * 获取1：不保密 2：保密
     *
     * @return secrecy - 1：不保密 2：保密
     */
    public Integer getSecrecy() {
        return secrecy;
    }

    /**
     * 设置1：不保密 2：保密
     *
     * @param secrecy 1：不保密 2：保密
     */
    public void setSecrecy(Integer secrecy) {
        this.secrecy = secrecy;
    }

    /**
     * 获取文件存储路径
     *
     * @return file_Uuid - 文件存储路径
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * 设置文件存储路径
     *
     * @param fileUuid 文件存储路径
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * 获取二维码ID
     *
     * @return qr_Id - 二维码ID
     */
    public Integer getQrId() {
        return qrId;
    }

    /**
     * 设置二维码ID
     *
     * @param qrId 二维码ID
     */
    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    /**
     * 获取是否直接发送客户 1：否 2：是
     *
     * @return is_send - 是否直接发送客户 1：否 2：是
     */
    public Integer getIsSend() {
        return isSend;
    }

    /**
     * 设置是否直接发送客户 1：否 2：是
     *
     * @param isSend 是否直接发送客户 1：否 2：是
     */
    public void setIsSend(Integer isSend) {
        this.isSend = isSend;
    }

    /**
     * 获取下载次数
     *
     * @return num - 下载次数
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置下载次数
     *
     * @param num 下载次数
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * 获取txt文件内容
     *
     * @return text - txt文件内容
     */
    public String getText() {
        return text;
    }

    /**
     * 设置txt文件内容
     *
     * @param text txt文件内容
     */
    public void setText(String text) {
        this.text = text;
    }

    @Transient
    private String suffix;
    @Transient
    private String fileName;
    @Transient
    private String fileId;
    @Transient
    private Integer size;
    @Transient
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getIndexs() {
        return indexs;
    }

    public void setIndexs(Integer indexs) {
        this.indexs = indexs;
    }
}