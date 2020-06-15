package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.PermMapper;
import com.yxtech.sys.domain.Perm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2015/10/27.
 */
@Service
public class PermService extends BaseService<Perm>{
    private PermMapper permMapper;

    @Resource(name = "permMapper")
    public void setPermMapper(PermMapper permMapper) {
        setMapper(permMapper);
        this.permMapper = permMapper;
    }
}
