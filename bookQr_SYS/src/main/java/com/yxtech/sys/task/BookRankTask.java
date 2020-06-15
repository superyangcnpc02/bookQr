package com.yxtech.sys.task;

import com.yxtech.sys.service.BookRankStatisticService;
import com.yxtech.utils.file.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *  扫码统计定时任务
 *  @author wzf
 *  @date 2019/04/18
 */
@Component
@Configuration
@EnableScheduling
public class BookRankTask {

    protected Logger log = LoggerFactory.getLogger(BookRankTask.class);

    //定时任务开关
    @Value("#{configProperties['task.enable']}")
    private boolean taskEnable;

    private static final String beginTime = "00";

    @Autowired
    private BookRankStatisticService bookRankStatisticService;
    /**
     *  定时任务，每隔2小时整点执行1次
     */
    @Scheduled(cron = "0 0 0/2 * * ?")
    //@Scheduled(cron = "30 0/1 * * * ?")
    public void bookRankStatistic() {
        if(!taskEnable){
            return;
        }
        log.info("bookRankStatistic begin");
        Date date = new Date();
        String yyyyMMdd = DateUtil.parseDateToStr(date,DateUtil.DATE_FORMAT_YYYY_MM_DD);
        String hour = DateUtil.parseDateToStr(date,DateUtil.DATE_TIME_FORMAT_HH);
        //小时为00时，统计前一天数据
        if(hour.equals(beginTime)){
            yyyyMMdd = DateUtil.getDayBefore(date.toLocaleString());
        }
        //生成数据
        bookRankStatisticService.bookRankStatisticTask(yyyyMMdd,yyyyMMdd);
        log.info("bookRankStatistic end");
    }
}
