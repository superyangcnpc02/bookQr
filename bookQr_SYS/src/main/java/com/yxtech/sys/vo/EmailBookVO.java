package com.yxtech.sys.vo;

import com.yxtech.sys.domain.Book;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 * 图书vo
 * Created by yanfei on 2015/10/19.
 */

public class EmailBookVO{

    private int id;
    private String  name;
    private String press;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public EmailBookVO(Book book){
        this.setId(book.getId());
        this.setName(book.getName());
        this.setPress(book.getPress());
    }
}

