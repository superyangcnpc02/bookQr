package com.yxtech.sys.vo.bookQr;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.Mail;
import com.yxtech.sys.domain.QrExport;
import com.yxtech.sys.vo.EmailBookVO;

import java.util.Date;
import java.util.List;

/**
 * @author cuihao
 * @create 2016-12-14-8:42
 */

public class MailVo extends Mail {
    private int num;
    private int downNum;
    private String bookName;
    private String sendTime;
    private List<EmailBookVO> books;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getDownNum() {
        return downNum;
    }

    public void setDownNum(int downNum) {
        this.downNum = downNum;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public List<EmailBookVO> getBooks() {
        return books;
    }

    public void setBooks(List<EmailBookVO> books) {
        this.books = books;
    }

    public MailVo(){}
    public MailVo(Mail mail){
        this.setId(mail.getId());
        this.setEmail(mail.getEmail());
        this.setTime(mail.getTime());
    }
}
