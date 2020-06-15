package com.yxtech.sys.vo;

import java.util.ArrayList;

/**
 * @author cuihao
 * @create 2016-09-19-9:25
 */

public class ExamineSendVo {
    private int flag;  // 1：通过；2：驳回
    private String reason;  // 驳回原因
    private ArrayList<Object> ids;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<Object> getIds() {
        return ids;
    }

    public void setIds(ArrayList<Object> ids) {
        this.ids = ids;
    }
}
