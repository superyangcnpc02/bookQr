package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_zip_order")
public class ZipOrder {
    @Id
    private Integer id;

    /**
     * 二维码id
     */
    @Column(name = "qr_Id")
    private Integer qrId;

    /**
     * 压缩持久化处理的进程 ID
     */
    @Column(name = "zip_Persistentid")
    private String zipPersistentid;

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
     * 获取二维码id
     *
     * @return qr_Id - 二维码id
     */
    public Integer getQrId() {
        return qrId;
    }

    /**
     * 设置二维码id
     *
     * @param qrId 二维码id
     */
    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    /**
     * 获取压缩持久化处理的进程 ID
     *
     * @return zip_Persistentid - 压缩持久化处理的进程 ID
     */
    public String getZipPersistentid() {
        return zipPersistentid;
    }

    /**
     * 设置压缩持久化处理的进程 ID
     *
     * @param zipPersistentid 压缩持久化处理的进程 ID
     */
    public void setZipPersistentid(String zipPersistentid) {
        this.zipPersistentid = zipPersistentid;
    }
}