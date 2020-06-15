package com.yxtech.sys.vo.bookQr;

/**
 * Created by zml on 2017/8/1.
 */
public class BookQrParamVO {
    private String qrId;    // 支持批量更新 qrIds
    private int flag;       // 开关标示 1：直接读取 2：限制读取

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
