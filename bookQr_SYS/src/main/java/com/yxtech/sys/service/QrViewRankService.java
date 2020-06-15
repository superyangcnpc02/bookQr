package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.QrViewRankMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.QrViewRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QrViewRankService extends BaseService<QrViewRank>{

    @Autowired
    private QrViewRankMapper qrViewRankMapper;

    @Resource(name = "qrViewRankMapper")
    public void setQrViewRankMapper(QrViewRankMapper qrViewRankMapper) {
        setMapper(qrViewRankMapper);
        this.qrViewRankMapper = qrViewRankMapper;
    }

    @Autowired
    private BookService bookService;
    @Autowired
    private BookQrService bookQrService;


    /**
     * 新增预览统计
     */
    @Transactional
    public JsonResult addViewRank(Integer id,String openId) {
        BookQr bookQr = bookQrService.selectByPrimaryKey(id);
        if (bookQr.getPreviewNum() == null) {
            bookQr.setPreviewNum(1);
        } else {
            bookQr.setPreviewNum(bookQr.getPreviewNum() + 1);
        }
        bookQrService.updateByPrimaryKeySelective(bookQr);

        Book book = bookService.selectByPrimaryKey(bookQr.getBookId());
        book.setPreviewNum(book.getPreviewNum() + 1);
        bookService.updateByPrimaryKeySelective(book);

        //如果数据库有该openid预览该二维码且是今天的数据,则数量+1;否则重新插入一条
        Map map = new HashMap<>();
        map.put("qrId",id);
        map.put("openId",openId);
        List<QrViewRank> qrViewRankList = qrViewRankMapper.getListByMap(map);
        if(qrViewRankList!=null && qrViewRankList.size()!=0){
            QrViewRank viewRank = qrViewRankList.get(0);
            viewRank.setNum(viewRank.getNum()+1);
            qrViewRankMapper.updateByPrimaryKeySelective(viewRank);
        }else{
            QrViewRank qrViewRank = new QrViewRank();
            qrViewRank.setPressId(book.getPressId());
            qrViewRank.setBookId(book.getId());
            qrViewRank.setQrId(id);
            qrViewRank.setOpenid(openId);
            qrViewRank.setCreateTime(new Date());
            qrViewRank.setNum(1);
            qrViewRankMapper.insertSelective(qrViewRank);
        }

        return new JsonResult(true,"增加成功!");
    }












}
