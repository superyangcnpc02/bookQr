package com.yxtech.sys.dao;

import com.yxtech.sys.domain.BookQr;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BookQrMapper extends Mapper<BookQr> {
    /**
     * 根据bookid查询
     */
    public List<BookQr> getListByBookId(int bookId);

    public List<BookQr> getResNumThanTwo();
}