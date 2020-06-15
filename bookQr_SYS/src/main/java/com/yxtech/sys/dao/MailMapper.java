package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Mail;
import com.yxtech.sys.vo.bookQr.MailVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface MailMapper extends Mapper<Mail> {
    public List<MailVo> getMailsByBookID(Map<String, Object> map);

    public List<MailVo> getMailList(Map<String, Object> map);

    public List<Integer> getBooksByMailId(Integer mailId);
}