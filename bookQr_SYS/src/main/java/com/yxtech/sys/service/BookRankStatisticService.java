package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.BookRankStatisticMapper;
import com.yxtech.sys.domain.BookRankStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 扫码统计Service
 * @author wzf
 * @since 2019/04/18
 */
@Service
public class BookRankStatisticService extends BaseService<BookRankStatistic> {
    public final static Logger log = LoggerFactory.getLogger(BookRankStatisticService.class);
    @Autowired
    private BookRankStatisticMapper bookRankStatisticMapper;

    public void bookRankStatisticTask(String beginTime,String endTime){
        if(StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(endTime)){
            log.error("time is null");
            return;
        }
        //删除该时间段内数据
        bookRankStatisticMapper.deleteBookRankStatistic(beginTime,endTime);
        beginTime = beginTime + " 00:00:00";
        endTime = endTime + " 23:59:59";
        //新增该时间段内数据
        bookRankStatisticMapper.insertBookRankStatistic(beginTime,endTime);
    }
}

