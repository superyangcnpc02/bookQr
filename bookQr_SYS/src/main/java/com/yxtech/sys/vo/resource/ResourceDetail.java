package com.yxtech.sys.vo.resource;

import com.yxtech.sys.domain.FileRes;

/**
 * @author cuihao
 * @create 2016-09-18-17:48
 */

public class ResourceDetail extends FileRes {

    private String text;

    private Integer onwer;

    private Integer num;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOnwer() {
        return onwer;
    }

    public void setOnwer(Integer onwer) {
        this.onwer = onwer;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public ResourceDetail(FileRes fileRes){
        this.setName(fileRes.getName());
        this.setPath(fileRes.getPath());
        this.setSize(fileRes.getSize());
        this.setSuffix(fileRes.getSuffix());
        this.setUuid(fileRes.getUuid());
    }
}
