package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.sys.dao.AdvBookRelationMapper;
import com.yxtech.sys.domain.AdvBookRelation;
import com.yxtech.sys.dto.AdvBookRelationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zml on 2018/1/8 14:34
 */
@Service
public class AdvBookRelationService extends BaseService<AdvBookRelation>{

    @Autowired
    private AdvBookRelationMapper advBookRelationMapper;

    @Resource(name = "advBookRelationMapper")
    public void setAdvBookRelationMapper(AdvBookRelationMapper advBookRelationMapper) {
        setMapper(advBookRelationMapper);
        this.advBookRelationMapper = advBookRelationMapper;
    }

    /**
     * 根据 advId 获取所有的 bookCatalogIds
     * @param id
     * @return
     */
    public List<Object> getIdsByAdvId(Integer id) {
        List<Object> bookIds = new ArrayList<>();
        Example example = new Example(AdvBookRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("advId", id);
        List<AdvBookRelation> advBookRelationList = advBookRelationMapper.selectByExample(example);
        for (AdvBookRelation advBookRelation : advBookRelationList) {
            bookIds.add(advBookRelation.getBookId());
        }
        return bookIds;
    }

    /**
     * 根据广告位，删除所有与广告位有关联的信息
     * @param id
     */
    @Transactional
    public void deleteByAdvId(Integer id) {
        Example example = new Example(AdvBookRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("advId", id);
        advBookRelationMapper.deleteByExample(example);
    }

    /**
     * 根据 bookId 获取 advIds
     * @param id
     * @return
     */
    public List<Object> getAdvIdsByBookId(Integer id) {
        List<Object> advIds = new ArrayList<>();
        Example example = new Example(AdvBookRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bookId", id);
        List<AdvBookRelation> advBookRelationList = advBookRelationMapper.selectByExample(example);
        for (AdvBookRelation advBookRelation : advBookRelationList) {
            advIds.add(advBookRelation.getAdvId());
        }
        return advIds;
    }

    /**
     * 编辑关系
     * @param advBookRelationDto
     */
    @Transactional
    public void addAdvBookRelation(AdvBookRelationDto advBookRelationDto) {
        // 先查询出来现在的关系
        Example example = new Example(AdvBookRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("advId", advBookRelationDto.getAdvId());
        // 获取当前的 bookCatalogIds
        List<AdvBookRelation> advBookRelationList = advBookRelationMapper.selectByExample(example);
        List<Integer> dataIds = new ArrayList<>();
        for (AdvBookRelation advBookRelation : advBookRelationList) {
            dataIds.add(advBookRelation.getBookId());
        }
        // 需要解除关系的 bookCatalogIds
        List<Object> delBookIds = new ArrayList<>();
        for (Integer bookId : dataIds) {
            if (!advBookRelationDto.getBookIds().contains(bookId)) {
                delBookIds.add(bookId);
            }
        }

        // 需要新增关系的 bookCatalogIds
        List<Integer> addBookIds = new ArrayList<>();
        for (Integer bookId : advBookRelationDto.getBookIds()) {
            if (!dataIds.contains(bookId)) {
                addBookIds.add(bookId);
            }
        }

        if (addBookIds.size() != 0) {
            advBookRelationMapper.insertBatchAdbBook(advBookRelationDto.getAdvId(), addBookIds);
        }
        if (delBookIds.size() != 0){
            deleteInBookIds(advBookRelationDto.getAdvId(), delBookIds);
        }
    }

    /**
     * 根据多个图书分类ID 删除数据
     * @param delBookIds
     */
    @Transactional
    public void deleteInBookIds(Integer advId, List<Object> delBookIds){
        Example example = new Example(AdvBookRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("advId", advId);
        criteria.andIn("bookId", delBookIds);
        advBookRelationMapper.deleteByExample(example);
    }

}
