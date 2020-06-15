package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.AuditorMailMapper;
import com.yxtech.sys.domain.AuditorMail;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 审核配置
 * Created by yanfei on 2015/11/16.
 */
@Service("auditorMailService")
public class AuditorMailService extends BaseService<AuditorMail> {
    private AuditorMailMapper auditorMailMapper;

    public AuditorMailMapper getAuditorMailMapper() {
        return auditorMailMapper;
    }

    @Resource(name = "auditorMailMapper")
    public void setAuditorMailMapper(AuditorMailMapper auditorMailMapper) {
        setMapper(auditorMailMapper);
        this.auditorMailMapper = auditorMailMapper;
    }
}
