package com.yxtech.sys.dao;

import com.yxtech.sys.domain.Book;
import com.yxtech.sys.dto.AdvBookDto;
import com.yxtech.sys.vo.count.BookCountVo;
import com.yxtech.sys.vo.count.BookListVo;
import com.yxtech.sys.vo.count.QrCountVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface BookMapper extends Mapper<Book> {

    /**
     * 批量插入数据
     * @param bookList
     * @return
     * @author yanfei
     * @date 2015.11.9
     */
    public int insertBooks (List<Book> bookList);


    public List<Book> selectReportBooks(Map map);

    public int countReportBooks(Map map);

    public List<BookCountVo> selectScanBooks(Map map);

    public int countScanBooks(Map map);

    //导出图书扫描
    public List<BookCountVo> exportBookRank(Map map);

    //导出图书信息扫描
    public List<BookListVo> exportBookList(Map map);

    List<BookCountVo> selectViewRank(Map<String, Object> map);

    int countViewRank(Map<String, Object> map);

    List<QrCountVo> getQrViewRank(@Param("bookId") int bookId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    List<BookCountVo> exportViewRank(Map<String, Object> map);

    List<AdvBookDto> getAdvBooksByIds(@Param("advBookIds") List<Object> advBookIds);
    //刮刮卡二维码列表
    List<Book> getList(Map<String,Object> map);
    //导出刮刮卡列表
    List<Book> exportList(Map<String,Object> map);

    int getBookCountByMap(Map<String, Object> map);
}