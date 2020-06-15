package com.yxtech.sys.vo.resource;

/**
 * @author cuihao
 * @create 2017-08-22-18:30
 */

public class ResIndexVo {
    private int flag;//	number	1上调2下调
    private int qrId;//	number	二维码id
    private int resId;//number	资源id

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getQrId() {
        return qrId;
    }

    public void setQrId(int qrId) {
        this.qrId = qrId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
