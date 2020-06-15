package com.yxtech.sys.dao;

import com.yxtech.sys.domain.BookRankStatistic;
import com.yxtech.sys.vo.count.BookCountVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 *  扫码统计Mapper
 * @author wzf
 * @date 2019/04/18
 */
@Repository
public interface BookRankStatisticMapper extends Mapper<BookRankStatistic> {

    /**
     * 删除一段时间内的数据
     * @param beginTime
     * @param endTime
     * @return
     */
    public int deleteBookRankStatistic(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    /**
     *  新增一段时间内的数据
     * @param beginTime
     * @param endTime
     * @return
     */
    public int insertBookRankStatistic(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    public List<BookCountVo> queryByPageBookRankStatistic(Map map);

    public int queryBookRankStatisticCount(Map map);


}