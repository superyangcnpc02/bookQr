package com.yxtech.sys.vo.bookQr;

/**
 * 二维码设置
 * Created by hesufang on 2015/11/4.
 */
public class CoursewareSetlVO {

    //2：要审核 1：共享
    private Integer check;

    //图书ID
    private Integer id;


    //是否直接发送客户 1 否   2：是

    private Integer sendCustomer;


    public Integer getCheck() {
        return check;
    }

    public void setCheck(Integer check) {
        this.check = check;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSendCustomer() {
        return sendCustomer;
    }

    public void setSendCustomer(Integer sendCustomer) {
        this.sendCustomer = sendCustomer;
    }
}

