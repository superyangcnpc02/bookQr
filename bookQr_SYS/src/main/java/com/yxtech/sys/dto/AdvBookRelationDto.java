package com.yxtech.sys.dto;

import java.util.List;

/**
 * create by zml on 2018/1/8 16:40
 */
public class AdvBookRelationDto {
    private Integer advId;
    private List<Integer> bookIds;

    public Integer getAdvId() {
        return advId;
    }

    public void setAdvId(Integer advId) {
        this.advId = advId;
    }

    public List<Integer> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<Integer> bookIds) {
        this.bookIds = bookIds;
    }
}
