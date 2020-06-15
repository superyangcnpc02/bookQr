package com.yxtech.sys.dao;

import com.yxtech.sys.domain.ViewRankStatistic;
import com.yxtech.sys.vo.count.BookCountVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 *  预览统计Mapper
 * @author wzf
 * @date 2019/04/19
 */
@Repository
public interface ViewRankStatisticMapper extends Mapper<ViewRankStatistic> {

    /**
     * 删除一段时间内的数据
     * @param beginTime
     * @param endTime
     * @return
     */
    public int deleteViewRankStatistic(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     *  新增一段时间内的数据
     * @param beginTime
     * @param endTime
     * @return
     */
    public int insertViewRankStatistic(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    public List<BookCountVo> queryByPageViewRankStatistic(Map map);

    public int queryViewRankStatisticCount(Map map);


}