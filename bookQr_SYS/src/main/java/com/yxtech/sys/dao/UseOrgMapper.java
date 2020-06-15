package com.yxtech.sys.dao;

import com.yxtech.sys.domain.UseOrg;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UseOrgMapper extends Mapper<UseOrg> {
    public Integer insertBatch(List<UseOrg> useOrgList);
}