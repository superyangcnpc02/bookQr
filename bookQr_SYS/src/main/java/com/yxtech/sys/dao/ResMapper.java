package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Res;
import com.yxtech.sys.vo.count.ResCountVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface ResMapper extends Mapper<Res> {
    public List<Object> getSecrecyByqrId(Integer qrId);

    public List<ResCountVo> exportDownRank(Map<String, Object> map);

    public Long downLoadRankCount(Map<String, Object> map);

    public List<ResCountVo> downLoadRank(Map<String, Object> map);

    public List<Integer> getResNumGreaterThanOne();
}