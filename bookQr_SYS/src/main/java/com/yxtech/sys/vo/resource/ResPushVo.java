package com.yxtech.sys.vo.resource;

import java.util.List;

/**
 * Created by zml on 2016/9/21.
 */
public class ResPushVo {
    private List<FilesVo> files;
    private int qrcodeId ;

    public List<FilesVo> getFiles() {
        return files;
    }

    public void setFiles(List<FilesVo> files) {
        this.files = files;
    }

    public int getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(int qrcodeId) {
        this.qrcodeId = qrcodeId;
    }
}
