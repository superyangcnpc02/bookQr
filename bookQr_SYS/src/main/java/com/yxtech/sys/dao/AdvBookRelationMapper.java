package com.yxtech.sys.dao;

import com.yxtech.sys.domain.AdvBookRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
@Repository
public interface AdvBookRelationMapper extends Mapper<AdvBookRelation> {
    void insertBatchAdbBook(@Param("advId") Integer advId, @Param("addBookIds") List<Integer> addBookIds);
}