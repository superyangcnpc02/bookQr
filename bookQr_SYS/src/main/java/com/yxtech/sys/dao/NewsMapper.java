package com.yxtech.sys.dao;

import com.yxtech.sys.domain.News;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface NewsMapper extends Mapper<News> {
}