package com.yxtech.sys.vo.count;

import java.io.Serializable;

/**
 * Created by liukailong on 2016-09-10.
 */
public class QrCountVo implements Serializable {

    private int qrId;
    private String qrName;
    private int qrViewNum;

    public int getQrId() {
        return qrId;
    }

    public void setQrId(int qrId) {
        this.qrId = qrId;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public int getQrViewNum() {
        return qrViewNum;
    }

    public void setQrViewNum(int qrViewNum) {
        this.qrViewNum = qrViewNum;
    }

    @Override
    public String toString() {
        return qrName+":"+qrViewNum;
    }
}
