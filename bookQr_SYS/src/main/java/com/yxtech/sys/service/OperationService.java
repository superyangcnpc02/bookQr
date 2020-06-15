package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.utils.file.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by lyj on 2015/10/24.
 */
@Service
public class OperationService extends BaseService<Operation> {
    private OperationMapper operationMapper;

    @Resource(name = "operationMapper")
    public void setOrgMapper(OperationMapper operationMapper) {
        setMapper(operationMapper);
        this.operationMapper = operationMapper;
    }

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookQrMapper bookQrMapper;
    @Autowired
    private ResMapper resMapper;
    @Autowired
    private FileResMapper fileResMapper;

    /**
     *
     * @param idList 对应下面的类别id
     * @param type 1图书2二维码3资源
     */
    public void record(List<Integer>  idList , int type , HttpServletRequest request){
        for(int id : idList){
            if(type == Constant.BOOK){
                Book book = bookMapper.selectByPrimaryKey(id);
                book.setTip("");
                String bookStr = JSON.toJSONString(book);
                String ip = WebUtil.getIpAddr(request);
                //保存到数据库
                Operation op = new Operation();
                op.setEmail(CurrentUser.getUser().getEmail());
                op.setType(Constant.BOOK);
                op.setObjId(id);
                op.setContent(bookStr);
                op.setIp(ip);
                op.setCreateTime(new Date());
                this.insertSelective(op);
            }else if(type == Constant.BOOKQR){
                //二维码明细
                BookQr bookQr = bookQrMapper.selectByPrimaryKey(id);
                String bookQrStr = JSON.toJSONString(bookQr);
                //图书明细
                Book book = bookMapper.selectByPrimaryKey(bookQr.getBookId());
                book.setTip("");
                String bookStr = JSON.toJSONString(book);
                String ip = WebUtil.getIpAddr(request);
                //保存到数据库
                Operation op = new Operation();
                op.setEmail(CurrentUser.getUser().getEmail());
                op.setType(Constant.BOOKQR);
                op.setObjId(id);
                op.setContent(bookStr+"<__________>"+bookQrStr);
                op.setIp(ip);
                op.setCreateTime(new Date());
                this.insertSelective(op);
            }else if(type == Constant.RES){
                //资源明细
                Res res = resMapper.selectByPrimaryKey(id);
                String resStr = JSON.toJSONString(res);
                //文件明细
                FileRes fileResEP = new FileRes();
                fileResEP.setUuid(res.getFileUuid());
                FileRes fileRes = fileResMapper.selectOne(fileResEP);
                String fileResStr = "";
                if(fileRes!=null){
                    fileResStr = JSON.toJSONString(fileRes);
                }
                //二维码明细
                BookQr bookQr = bookQrMapper.selectByPrimaryKey(res.getQrId());
                String bookQrStr = JSON.toJSONString(bookQr);
                //图书明细
                Book book = bookMapper.selectByPrimaryKey(bookQr.getBookId());
                book.setTip("");
                String bookStr = JSON.toJSONString(book);
                String ip = WebUtil.getIpAddr(request);
                //保存到数据库
                Operation op = new Operation();
                op.setEmail(CurrentUser.getUser().getEmail());
                op.setType(Constant.RES);
                op.setObjId(id);
                op.setContent(bookStr+"<__________>"+bookQrStr+"<__________>"+resStr+"<__________>"+fileResStr);
                op.setIp(ip);
                op.setCreateTime(new Date());
                this.insertSelective(op);
            }

        }

    }
}
