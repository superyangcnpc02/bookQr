package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.ClientBookMapper;
import com.yxtech.sys.domain.ClientBook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 编辑过的图书业务逻辑类
 * Created by yanfei on 2015/11/6.
 */
@Service
public class ClientBookService extends BaseService<ClientBook> {

    private ClientBookMapper clientBookMapper;

    @Resource(name = "clientBookMapper")
    public void setClientBookMapper(ClientBookMapper clientBookMapper) {
        setMapper(clientBookMapper);
        this.clientBookMapper = clientBookMapper;
    }

    /**
     * 批量插入数据
     * @param clientBookList
     * @return
     * @author yanfei
     * @date 2015.11.9
     */
    public int insertClientBookForList(List<ClientBook> clientBookList){
        return clientBookMapper.insertClientBookForList(clientBookList);
    }
}
