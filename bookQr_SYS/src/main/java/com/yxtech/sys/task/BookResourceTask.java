package com.yxtech.sys.task;

import com.yxtech.sys.domain.LBookResource;
import com.yxtech.sys.service.LBookResourceService;
import com.yxtech.sys.service.UserCenterService;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  智学云平台绑定图书和资源关系定时任务
 *  @author wzf
 *  @date 2019/04/30
 */
@Component
@Configuration
@EnableScheduling
public class BookResourceTask {

    protected Logger log = LoggerFactory.getLogger(BookResourceTask.class);

    //定时任务开关
    @Value("#{configProperties['task.enable']}")
    private boolean taskEnable;

    @Autowired
    private LBookResourceService bookResourceService;

    @Autowired
    private UserCenterService userCenterService;
    /**
     *  定时任务，每隔1小时10分执行1次
     *  向向智学云平台重新发送图书和资源关系
     */
    @Scheduled(cron = "0 10 0/1 * * ?")
    //@Scheduled(cron = "30 0/1 * * * ?")
    public void bookResourceTask() {
        if(!taskEnable){
            return;
        }
        log.info("bookResourceTask begin");
        LBookResource bookResource = new LBookResource();
        bookResource.setStatus(1);//执行失败的
        int limit = 10;
        //每次处理10条数据，执行10次
        for (int i = 0; i<10 ; i++){
            RowBounds rowBounds = new RowBounds(0 * limit,limit);
            List<LBookResource> list = bookResourceService.query(bookResource,rowBounds);
            if(list == null || list.size() ==0){
                break;
            }
            for(LBookResource lBookResource : list){
                if(lBookResource.getErrorType() != null){
                    if(lBookResource.getErrorType() == 1){//新增
                        userCenterService.bindBookRes(lBookResource.getBookId(),lBookResource.getQrType(),lBookResource.getResId(),lBookResource.getId());
                    }else if(lBookResource.getErrorType() == 2){//删除
                        userCenterService.deleteBookRes(lBookResource.getResId(),lBookResource.getId());
                    }
                }
            }
        }
        log.info("bookResourceTask end");
    }
}
