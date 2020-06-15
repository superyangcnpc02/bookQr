package com.yxtech.sys.vo.bookQr;

import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */
public class BookQREditVO {

    private Integer id ;
    private String name;
    private String note;
    private List<Object> resource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Object> getResource() {
        return resource;
    }

    public void setResource(List<Object> resource) {
        this.resource = resource;
    }
}
