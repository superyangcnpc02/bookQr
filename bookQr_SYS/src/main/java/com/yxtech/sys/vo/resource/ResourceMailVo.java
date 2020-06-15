package com.yxtech.sys.vo.resource;

import com.yxtech.sys.domain.Client;
import com.yxtech.sys.domain.Res;

import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author cuihao
 * @create 2016-09-30-15:46
 */

public class ResourceMailVo extends Res {
    /**
     * 举报原因
     */
    private String remark;
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
    private String resName;

    /**
     * 二维码名称
     */
    private String qrName;

    /**
     * 书籍名称
     */
    private String bookName;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public ResourceMailVo(Res res){
        this.setId(res.getId());
        this.setFileType(res.getFileType());
        this.setOnwer(res.getOnwer());
        this.setSecrecy(res.getSecrecy());
        this.setFileUuid(res.getFileUuid());
        this.setQrId(res.getQrId());
        this.setIsSend(res.getIsSend());
        this.setNum(res.getNum());
    }
}
