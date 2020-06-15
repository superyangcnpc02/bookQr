package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.UseOrgMapper;
import com.yxtech.sys.domain.UseOrg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyj on 2015/10/24.
 */
@Service
public class UseOrgService extends BaseService<UseOrg> {
    private UseOrgMapper useOrgMapper;

    @Resource(name = "useOrgMapper")
    public void setUseOrgMapper(UseOrgMapper useOrgMapper) {
        setMapper(useOrgMapper);
        this.useOrgMapper = useOrgMapper;
    }


    /**
     * 批量插入
     * @param useOrgList
     * @author lyj
     * @since 2015-10-30
     */
    public void insertBatch(List<UseOrg> useOrgList) {
        List<UseOrg> tempUseOrg = new ArrayList<>();
        for (UseOrg useOrg : useOrgList) {
            for (int i = 1; i <= 4; i++) {
                tempUseOrg.add(new UseOrg(useOrg.getUserId(), useOrg.getOrgId(), i));
            }
        }

        this.useOrgMapper.insertBatch(tempUseOrg);
    }
}
