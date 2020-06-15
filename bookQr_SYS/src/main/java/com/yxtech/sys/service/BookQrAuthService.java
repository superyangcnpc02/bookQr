package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.BookQrAuthMapper;
import com.yxtech.sys.domain.BookQrAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zml on 2017/8/1.
 */

@Service
public class BookQrAuthService extends BaseService<BookQrAuth>{
    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;

    @Resource(name = "bookQrAuthMapper")
    public void setBookQrAuthMapper(BookQrAuthMapper bookQrAuthMapper) {
        setMapper(bookQrAuthMapper);
        this.bookQrAuthMapper = bookQrAuthMapper;
    }

    /**
     * 根据 bookId 查询权限二维码的主键 ID
     * 查不到返回 0
     * @param bookId
     * @return
     */
    public int getIdByBookId(int bookId){
        BookQrAuth condition = new BookQrAuth();
        condition.setBookId(bookId);
        BookQrAuth bookQrAuth = bookQrAuthMapper.selectOne(condition);
        if (bookQrAuth != null){
            return bookQrAuth.getId();
        }
        return 0;
    }


}
