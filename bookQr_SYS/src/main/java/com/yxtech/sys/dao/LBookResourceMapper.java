package com.yxtech.sys.dao;

import com.yxtech.sys.domain.LBookResource;
import com.yxtech.sys.dto.BookResourceDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface LBookResourceMapper extends Mapper<LBookResource> {

    public List<BookResourceDto> queryAllBookResource(@Param("bookId") Integer bookId);
}