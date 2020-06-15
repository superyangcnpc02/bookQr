package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Report;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ReportMapper extends Mapper<Report> {
}