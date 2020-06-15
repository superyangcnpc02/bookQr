package com.yxtech.sys.dao;

import com.yxtech.sys.domain.MailBookCount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface MailBookCountMapper extends Mapper<MailBookCount> {
    public MailBookCount selectCountIdByEmaliAndBookId(@Param("email") String email, @Param("bookId") Integer bookId);
    public List<Object> getmailIdsByBookId(Integer BookId);
    public List<Object> getmailIdsByCountIds(List<Integer> countIds );
}