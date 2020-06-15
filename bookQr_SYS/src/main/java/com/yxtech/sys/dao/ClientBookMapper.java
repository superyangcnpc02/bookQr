package com.yxtech.sys.dao;

import com.yxtech.sys.domain.ClientBook;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ClientBookMapper extends Mapper<ClientBook> {
    /**
     * 批量插入数据
     * @param clientBookList
     * @return
     * @author yanfei
     * @date 2015.11.9
     */
    public int insertClientBookForList(List<ClientBook> clientBookList);
}