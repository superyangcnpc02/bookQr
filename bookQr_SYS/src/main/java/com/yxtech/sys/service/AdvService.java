package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.AdvMapper;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.BookQrAuthMapper;
import com.yxtech.sys.dao.BookQrMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.dto.AdvBookRelationDto;
import com.yxtech.sys.vo.adv.AdvListVo;
import com.yxtech.sys.vo.adv.AdvVo;
import com.yxtech.sys.dto.AdvBookDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zml on 2017/10/23 8:55
 */
@Service
public class AdvService extends BaseService<Adv>{

    @Autowired
    private AdvMapper advMapper;

    @Autowired
    private BookQrMapper bookQrMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;

    @Autowired
    private AdvBookRelationService advBookRelationService;

    @Resource(name = "advMapper")
    public void setAdvMapper(AdvMapper advMapper) {
        setMapper(advMapper);
        this.advMapper = advMapper;
    }

    /**
     * 增加 广告位
     * @param advVo
     * @return
     */
    @Transactional
    public JsonResultId addAdv(AdvVo advVo) {
        Adv adv = new Adv(advVo);
        advMapper.insertSelective(adv);
        return new JsonResultId(true, "新增成功", adv.getId());
    }

    /**
     * 删除 广告位
     * @param id
     * @return
     */
    public JsonResult deleteAdvById(Integer id) {
        // 删除广告位数据
        int num = advMapper.deleteByPrimaryKey(id);
        if (num != 1) {
            return new JsonResult(false, "该数据不存在或已被删除！");
        }
        // 删除广告位绑定的图书数据
        advBookRelationService.deleteByAdvId(id);
        return new JsonResult(true, "删除成功");
    }

    /**
     * 修改 广告位
     * @param advVo
     * @return
     */
    @Transactional
    public JsonResult editAdvById(AdvVo advVo) {
        Adv adv = new Adv(advVo);
        int num = advMapper.updateByPrimaryKeySelective(adv);
        if (num != 1) {
            return new JsonResult(false, "该数据不存在或已被删除, 修改失败！");
        }
        return new JsonResult(true, "修改成功");
    }

    /**
     * 列表 广告位 0108
     * @return
     */
    public JsonResultPage getAdvList(String keyword, int pageNo, int pageSize) {
        Example example = new Example(Adv.class);
        example.setOrderByClause("createTime desc");
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andLike("name", "%" + keyword + "%");
        }
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage jsonResultPage = selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<Adv> advList = jsonResultPage.getItems();
        if (advList.size() == 0) {
            return jsonResultPage;
        }
        List<AdvListVo> advListVoList = new ArrayList<>();
        for (Adv adv : advList) {
            AdvListVo advListVo = new AdvListVo(adv);
            // 获取关系的 advBookIds
            List<Object> advBookIds = advBookRelationService.getIdsByAdvId(adv.getId());
            if (advBookIds.size() != 0) {
                // 获取广告位图书对象
                List<AdvBookDto> advBooks = bookMapper.getAdvBooksByIds(advBookIds);
                advListVo.setBooks(advBooks);
            }
            advListVoList.add(advListVo);
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), pageNo, pageSize, advListVoList));
    }

    /**
     * 根据二维码 id 获取广告位列表
     * @param qrId
     * @return
     */
    public JsonResultList getAdvListByQrId(Integer qrId) {
        // 根据二维码 id 获取 bookId
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(qrId);
        if (bookQr != null) {
            return getAdvListByBookId(bookQr.getBookId());
        }
        return new JsonResultList(false, "错误的二维码ID");
    }

    /**
     * 根据 advIds 获取广告位列表
     * @param ids
     * @return
     */
    public List<Adv> getAdvListByIds(List<Object> ids){
        Example example = new Example(Adv.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        return advMapper.selectByExample(example);
    }

    /**
     * 根据 权限二维码 id 获取 广告位列表
     * @param qrId
     * @return
     */
    public JsonResultList getAdvListByAuthQrId(Integer qrId) {
        BookQrAuth bookQrAuth = bookQrAuthMapper.selectByPrimaryKey(qrId);
        if (bookQrAuth != null) {
            return getAdvListByBookId(bookQrAuth.getBookId());
        }
        return new JsonResultList(false, "错误的二维码ID");
    }

    /**
     * 根据图书ID 获取广告位ID
     * @param bookId
     * @return
     */
    public JsonResultList getAdvListByBookId(Integer bookId){
        List<Object> advIds = advBookRelationService.getAdvIdsByBookId(bookId);
        if (advIds .size() == 0) {
            return new JsonResultList( true, "查询成功，但没有广告位数据！", new ArrayList());
        }
        // 获取广告位列表 根据 advIds
        List<Adv> advList = getAdvListByIds(advIds);
        return new JsonResultList(advList);
    }

    /**
     * 绑定关系 维护关系
     * @param advBookRelationDto
     * @return
     */
    public JsonResult addAdvBookRelation(AdvBookRelationDto advBookRelationDto) {
        advBookRelationService.addAdvBookRelation(advBookRelationDto);
        return new JsonResult(true, "操作成功");
    }
}
