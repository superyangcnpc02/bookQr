package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.ViewRankStatisticMapper;
import com.yxtech.sys.domain.ViewRankStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 预览统计Service
 * @author wzf
 * @since 2019/04/19
 */
@Service
public class ViewRankStatisticService extends BaseService<ViewRankStatistic> {
    public final static Logger log = LoggerFactory.getLogger(ViewRankStatisticService.class);
    @Autowired
    private ViewRankStatisticMapper viewRankStatisticMapper;

    public void viewRankStatisticTask(String beginTime,String endTime){
        if(StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(endTime)){
            log.error("time is null");
            return;
        }
        //删除该时间段内数据
        viewRankStatisticMapper.deleteViewRankStatistic(beginTime,endTime);
        beginTime = beginTime + " 00:00:00";
        endTime = endTime + " 23:59:59";
        //新增该时间段内数据
        viewRankStatisticMapper.insertViewRankStatistic(beginTime,endTime);
    }
}

