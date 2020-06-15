package com.yxtech.sys.vo.adv;

import com.yxtech.sys.domain.Adv;
import com.yxtech.sys.dto.AdvBookDto;

import java.util.List;

/**
 * create by zml on 2017/10/25 9:51
 */
public class AdvListVo extends Adv {

    private List<AdvBookDto> books;

    public AdvListVo() {}

    public AdvListVo(Adv adv) {
        this.setId(adv.getId());
        this.setName(adv.getName());
        this.setUrl(adv.getUrl());
        this.setImgUrl(adv.getImgUrl());
        this.setCreateTime(adv.getCreateTime());
    }

    public List<AdvBookDto> getBooks() {
        return books;
    }

    public void setBooks(List<AdvBookDto> books) {
        this.books = books;
    }
}
