package com.yxtech.sys.vo.bookQr;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2015/10/14.
 */
public class BookQRFile {

    private String name;//附件名称

    private String size;//附件大小

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BookQRFile(){}

    public String toString(){
        return "[name="+name+",size="+size+"]";
    }
}
