package com.yxtech.sys.vo.bookQr;

/**
 * 二维码设置
 * Created by hesufang on 2015/11/4.
 */
public class CoursewareSetNewVO {

    //2：要审核 1：共享
    private Integer check;

    //二维码ids,逗号分隔
    private String ids;


    //是否直接发送客户 1 否   2：是

    private Integer sendCustomer;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getCheck() {
        return check;
    }

    public void setCheck(Integer check) {
        this.check = check;
    }


    public Integer getSendCustomer() {
        return sendCustomer;
    }

    public void setSendCustomer(Integer sendCustomer) {
        this.sendCustomer = sendCustomer;
    }
}

