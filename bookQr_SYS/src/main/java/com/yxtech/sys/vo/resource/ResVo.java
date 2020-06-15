package com.yxtech.sys.vo.resource;

import java.util.List;

/**
 * Created by zml on 2017/4/19.
 */
public class ResVo {

    private List<FilesVo> files;
    private int id;
    private int qrType;

    public List<FilesVo> getFiles() {
        return files;
    }

    public void setFiles(List<FilesVo> files) {
        this.files = files;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }
}
