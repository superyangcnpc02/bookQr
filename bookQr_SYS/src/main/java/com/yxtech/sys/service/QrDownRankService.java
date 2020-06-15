package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.QrDownRankMapper;
import com.yxtech.sys.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QrDownRankService extends BaseService<QrDownRank> {

    @Autowired
    private QrDownRankMapper qrDownRankMapper;

    @Resource(name = "qrDownRankMapper")
    public void setQrDownRankMapper(QrDownRankMapper qrDownRankMapper) {
        setMapper(qrDownRankMapper);
        this.qrDownRankMapper = qrDownRankMapper;
    }
    @Autowired
    private ResService resService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookQrService bookQrService;

    /**
     * 单独统计资源下载
     * @param fileId
     * @param openId
     * @throws IOException
     */
    @Transactional
    public JsonResult downNum(int fileId,String openId) {
        //资源下载记录 资源下载数+1
        Res res = resService.selectByPrimaryKey(fileId);
        res.setNum(res.getNum() + 1);
        resService.updateByPrimaryKeySelective(res);

        //如果数据库有该openid下载该资源且是今天的数据,则数量+1;否则重新插入一条
        Map map = new HashMap<>();
        map.put("resId",fileId);
        map.put("openId",openId);
        List<QrDownRank> qrDownRankList = qrDownRankMapper.getListByMap(map);
        if(qrDownRankList!=null && qrDownRankList.size()!=0){
            QrDownRank downRank = qrDownRankList.get(0);
            downRank.setNum(downRank.getNum()+1);
            qrDownRankMapper.updateByPrimaryKeySelective(downRank);
        }else{
            BookQr bookQr = bookQrService.selectByPrimaryKey(res.getQrId());
            Book book = bookService.selectByPrimaryKey(bookQr.getBookId());

            QrDownRank qrDownRank = new QrDownRank();
            qrDownRank.setPressId(book.getPressId());
            qrDownRank.setBookId(book.getId());
            qrDownRank.setQrId(bookQr.getId());
            qrDownRank.setResId(fileId);
            qrDownRank.setOpenid(openId);
            qrDownRank.setCreateTime(new Date());
            qrDownRank.setNum(1);
            qrDownRankMapper.insertSelective(qrDownRank);
        }


        return new JsonResult(true, "操作成功");
    }

    /**
     * 批量文件下载
     * @param qrId
     */
    public void multipleFileDown(int qrId,String openId){
        Res resExample = new Res();
        resExample.setQrId(qrId);

        List<Res> resList = resService.select(resExample);
        if(resList!=null && resList.size()!=0){
            for(Res res:resList){
                //单独资源下载统计
                this.downNum(res.getId(),openId);
            }
        }

    }
}
