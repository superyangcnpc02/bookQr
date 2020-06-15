package com.yxtech.sys.vo.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.Client;
import com.yxtech.sys.domain.ClientAttr;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author cuihao
 * @create 2016-09-10-17:23
 */

public class ClientAttrVo extends Client {
    private String target;
    private int status;
    private int bookId;
    private String bookName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Override
    public String getBookName() {
        return bookName;
    }

    @Override
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public ClientAttrVo(){}

    public ClientAttrVo(Client client) {
        this.setName(client.getName());
        this.setJob(client.getJob());
        this.setMajor(client.getMajor());
        this.setLesson(client.getLesson());
        this.setSchool(client.getSchool());
        this.setDepart(client.getDepart());
        this.setPhone(client.getPhone());
        this.setQq(client.getQq());
        this.setNum(client.getNum());
        this.setLeaderPhone(client.getLeaderPhone());
        this.setBookName(client.getBookName());
        this.setDesire(client.getDesire());
        this.setEditor(client.getEditor());
        this.setEmail(client.getEmail());
        this.setObjective(client.getObjective());
        this.setAge(client.getAge());
        this.setSex(client.getSex());
        this.setSeat(client.getSeat());



        this.setPlan(client.getPlan());
        this.setPlanTime(client.getPlanTime());
        this.setProgress(client.getProgress());
        this.setAddress(client.getAddress());
    }
}
