package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Operation;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface OperationMapper extends Mapper<Operation> {
}