package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.OrgMapper;
import com.yxtech.sys.domain.Org;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lyj on 2015/10/24.
 */
@Service
public class OrgService extends BaseService<Org> {
    private OrgMapper orgMapper;

    @Resource(name = "orgMapper")
    public void setOrgMapper(OrgMapper orgMapper) {
        setMapper(orgMapper);
        this.orgMapper = orgMapper;
    }
}
