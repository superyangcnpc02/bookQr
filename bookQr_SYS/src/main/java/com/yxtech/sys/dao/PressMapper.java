package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Press;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface PressMapper extends Mapper<Press> {
    Integer getPressIdByName(@Param("pressName") String pressName);
}