package com.yxtech.sys.dao;

import com.yxtech.sys.domain.BookCatalog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface BookCatalogMapper extends Mapper<BookCatalog> {
}