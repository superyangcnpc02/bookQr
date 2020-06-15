package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.LBookResourceMapper;
import com.yxtech.sys.domain.LBookResource;
import com.yxtech.sys.dto.BookResourceDto;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 智学云平台绑定图书和资源关系日志Service
 * @author wzf
 * @since 2019/04/30
 */
@Service
@EnableAsync
public class LBookResourceService extends BaseService<LBookResource> {
    public final static Logger log = LoggerFactory.getLogger(LBookResourceService.class);
    @Autowired
    private LBookResourceMapper bookResourceMapper;
    @Autowired
    private UserCenterService userCenterService;

    /**
     *  新增或修改
     * @param bookResource
     * @return
     */
    public boolean insertOrUpdate(LBookResource bookResource){
        if(bookResource.getId() == null){
            //新增
            int i = bookResourceMapper.insertSelective(bookResource);
            return i>0?true:false;
        }else {
            //修改
            LBookResource lBookResource = bookResourceMapper.selectByPrimaryKey(bookResource.getId());
            if(lBookResource == null){
                log.error("ID:" + bookResource.getId() + ",未查询到数据");
                return false;
            }
            if(bookResource != null && bookResource.getErrorCode() != 0){
                bookResource.setErrorNum(lBookResource.getErrorNum() + 1);
            }else {
                bookResource.setStatus(2);
            }

            int i = bookResourceMapper.updateByPrimaryKeySelective(bookResource);
            return i>0?true:false;
        }
    }

    public List<LBookResource> query(LBookResource bookResource, RowBounds rowBounds){
        Example example = new Example(LBookResource.class);
        Example.Criteria criteria = example.createCriteria();
        if(bookResource.getBookId() != null && bookResource.getBookId() != 0){
            criteria.andEqualTo("bookId",bookResource.getBookId());
        }
        if(bookResource.getQrType() != null && bookResource.getQrType() != 0){
            criteria.andEqualTo("qrType",bookResource.getQrType());
        }
        if(bookResource.getResId() != null && bookResource.getResId() != 0){
            criteria.andEqualTo("resId",bookResource.getResId());
        }
        if(bookResource.getErrorType() != null && bookResource.getErrorType() != 0){
            criteria.andEqualTo("errorType",bookResource.getErrorType());
        }
        if(bookResource.getStatus() != null && bookResource.getStatus() != 0){
            criteria.andEqualTo("status",bookResource.getStatus());
        }
        List<LBookResource> list = bookResourceMapper.selectByExampleAndRowBounds(example,rowBounds);
        return list;
    }

    /**
     *  根据资源ID将任务修改为成功
     *  防止先失败，后成功，造成脏数据
     * @param resId
     * @return
     */
    public boolean updateStatusByResId(int resId){
        LBookResource lBookResource = new LBookResource();
        lBookResource.setStatus(2);//成功
        Example example = new Example(LBookResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("resId",resId);
        bookResourceMapper.updateByExampleSelective(lBookResource,example);
        return true;
    }

    public List<BookResourceDto> queryAllBookResource(Integer bookId){
        //查询所有的图书关系
        List<BookResourceDto> list = bookResourceMapper.queryAllBookResource(bookId);
        return list;
    }

    /**
     *  异步初始化数据
     * @param dto
     * @param index
     */
    @Async
    public void initData(BookResourceDto dto,int index){
        log.info("--------------" + index);
        userCenterService.bindBookRes(dto.getBookId(),dto.getQrType(),dto.getResId(),null);
    }
}

