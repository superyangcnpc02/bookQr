package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_file_res_copy")
public class FileResCopy {
    @Id
    private Integer id;

    /**
     * 文件存储路径
     */
    private String path;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * 上传时文件名称
     */
    private String name;

    /**
     * 实际存储的名称
     */
    private String uuid;

    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 文件md5值
     */
    private String md5;

    /**
     * 1 正常; 0 非法(未上传完毕)
     */
    private Integer status;

    private Long zhixueid;

    private String viewurl;

    /**
     * 视频在七牛云生成的智学云id
     */
    private Long qizhixueid;

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
     * 获取文件存储路径
     *
     * @return path - 文件存储路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置文件存储路径
     *
     * @param path 文件存储路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取文件大小
     *
     * @return size - 文件大小
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 设置文件大小
     *
     * @param size 文件大小
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 获取文件后缀
     *
     * @return suffix - 文件后缀
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * 设置文件后缀
     *
     * @param suffix 文件后缀
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获取上传时文件名称
     *
     * @return name - 上传时文件名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置上传时文件名称
     *
     * @param name 上传时文件名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取实际存储的名称
     *
     * @return uuid - 实际存储的名称
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置实际存储的名称
     *
     * @param uuid 实际存储的名称
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return create_Time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取文件md5值
     *
     * @return md5 - 文件md5值
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 设置文件md5值
     *
     * @param md5 文件md5值
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 获取1 正常; 0 非法(未上传完毕)
     *
     * @return status - 1 正常; 0 非法(未上传完毕)
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1 正常; 0 非法(未上传完毕)
     *
     * @param status 1 正常; 0 非法(未上传完毕)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return zhixueid
     */
    public Long getZhixueid() {
        return zhixueid;
    }

    /**
     * @param zhixueid
     */
    public void setZhixueid(Long zhixueid) {
        this.zhixueid = zhixueid;
    }

    /**
     * @return viewurl
     */
    public String getViewurl() {
        return viewurl;
    }

    /**
     * @param viewurl
     */
    public void setViewurl(String viewurl) {
        this.viewurl = viewurl;
    }

    public Long getQizhixueid() {
        return qizhixueid;
    }

    public void setQizhixueid(Long qizhixueid) {
        this.qizhixueid = qizhixueid;
    }
}