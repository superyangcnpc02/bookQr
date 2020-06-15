package com.yxtech.sys.dao;

import com.yxtech.sys.domain.QrViewRank;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;
import java.util.Map;

@Repository
public interface QrViewRankMapper extends Mapper<QrViewRank> {
    List<QrViewRank> getListByMap(Map map);
}