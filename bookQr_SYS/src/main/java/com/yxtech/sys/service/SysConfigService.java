package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.SysConfigMapper;
import com.yxtech.sys.domain.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * create by zml on 2017/12/14 16:10
 */
@Service
public class SysConfigService extends BaseService<SysConfig> {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Resource(name = "sysConfigMapper")
    public void setSysConfigMapper(SysConfigMapper sysConfigMapper) {
        setMapper(sysConfigMapper);
        this.sysConfigMapper = sysConfigMapper;
    }

    public void updateSysConfigBySyncTime(Date syncTime){
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(1);
        sysConfig.setSyncTime(syncTime);
        sysConfigMapper.updateByPrimaryKeySelective(sysConfig);
    }





}
