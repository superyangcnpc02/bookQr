package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Perm;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface PermMapper extends Mapper<Perm> {
}