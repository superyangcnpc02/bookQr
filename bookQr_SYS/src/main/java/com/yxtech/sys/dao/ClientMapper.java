package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Client;
import com.yxtech.sys.vo.client.ClientExportVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ClientMapper extends Mapper<Client> {
}