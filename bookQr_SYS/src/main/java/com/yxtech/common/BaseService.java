package com.yxtech.common;

import com.github.pagehelper.PageInfo;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultPage;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by Chenxh on 2015/9/22.
 */
public abstract class BaseService<T> {
    protected Logger log = LoggerFactory.getLogger(BaseService.class);

    private Mapper<T> mapper;
    public Mapper<T> getMapper() {
        return mapper;
    }
    public void setMapper(Mapper<T> mapper) {
        this.mapper = mapper;
    }

    /**
     * 保存一个实体，null的属性也会保存，不会使用数据库默认值。t.getId可以获取新增数据的ID
     * @param t 泛型对象
     */
    public void insert(T t) {
        if (mapper.insert(t) != 1) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"新增"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE,"新增"));
        }

    }

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值。t.getId可以获取新增数据的ID
     * @param t
     * @return
     */
    public void insertSelective(T t) {
        if (mapper.insertSelective(t) != 1) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"新增"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE,"新增"));
        }
    }


    /**
     * 根据Example条件删除数据（一条或多条）
     * @param object
     * @return
     */
    public int deleteByExample(Object object) {
        return mapper.deleteByExample(object);
    }

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号（一条或多条）
     * @param t
     * @return
     */
    public int delete(T t) {
        return mapper.delete(t);
    }

    /**
     * 根据主键字段进行删除，方法参数必须包含完整的主键属性
     * @param object
     * @return
     */
    public void deleteByPrimaryKey(Object object) {
        if (mapper.deleteByPrimaryKey(object) != 1) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"删除"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE,"删除"));
        }
    }


    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param t
     * @return
     */
    public void updateByPrimaryKey(T t) {
        if (mapper.updateByPrimaryKey(t) != 1) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"更新"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE,"更新"));
        }
    }

    /**
     * 根据主键更新实体全部字段，null值不会被更新
     * @param t
     * @return
     */
    public void updateByPrimaryKeySelective(T t) {
        if (mapper.updateByPrimaryKeySelective(t) != 1) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"更新"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE,"更新"));
        }
    }

    /**
     * 根据Example条件更新实体t包含的全部属性，null值会被更新(一条或多条)
     * @param t
     * @param object
     * @return
     */
    public int updateByExample(T t, Object object) {
        return mapper.updateByExample(t, object);
    }

    /**
     * 根据Example条件更新实体t包含的全部属性，null值不会被更新(一条或多条)
     * @param t
     * @param object
     * @return
     */
    public int updateByExampleSelective(T t, Object object) {
        return mapper.updateByExampleSelective(t, object);
    }


    /**
     * 根据Example条件进行查询
     * 这个查询支持通过Example类指定查询列，通过selectProperties方法指定查询列
     * @param object
     * @return
     */
    public List<T> selectByExample(Object object) {
        return mapper.selectByExample(object);
    }

    /**
     * 根据ID查询一条数据
     * @param object
     * @return
     */
    public T selectByPrimaryKey(Object object) {
        T t = mapper.selectByPrimaryKey(object);
        if (t == null) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"查询"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE, "查询"));
        }

        return t;
    }

    /**
     * 根据example条件和RowBounds进行分页查询
     * @param example
     * @param rowBounds
     * @return
     */
    public List<T> selectByExampleAndRowBounds(Example example, RowBounds rowBounds) {
        return mapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    /**
     * 根据条件分页查询
     * @param example
     * @param rowBounds
     * @return
     */
    public PageInfo selectByExampleAndRowBounds2PageInfo(Example example, RowBounds rowBounds) {
        List<T> list = mapper.selectByExampleAndRowBounds(example, rowBounds);
        PageInfo page = new PageInfo(list);
        return page;
    }

    /**
     * 针对以前返回page对象封装
     * @param example
     * @param rowBounds
     * @return
     */
    public JsonResultPage selectByExampleAndRowBounds2JsonResultPage(Example example, RowBounds rowBounds) {
        List<T> list = mapper.selectByExampleAndRowBounds(example, rowBounds);
        PageInfo page = new PageInfo(list);
        return new JsonResultPage(page);
    }

    /**
     * 根据实体属性和RowBounds进行分页查询
     * @param t
     * @param rowBounds
     * @return
     */
    public List<T> selectByRowBounds(T t, RowBounds rowBounds) {
        return mapper.selectByRowBounds(t, rowBounds);
    }

    /**
     * 根据实体中的属性查询总数，查询条件使用等号
     * @param t
     * @return
     */
    public int selectCount(T t) {
        return mapper.selectCount(t);
    }

    /**
     * 根据实体属性和RowBounds进行分页查询
     * @param object
     * @return
     */
    public int selectByRowBounds(Object object) {
        return mapper.selectCountByExample(object);
    }

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     * @param t
     * @return
     */
    public T selectOne(T t) {
        T ret = mapper.selectOne(t);
        if (ret == null) {
            log.warn(MessageFormat.format(ConsHint.FAILURE,"查询"));
            throw new ServiceException(MessageFormat.format(ConsHint.FAILURE, "查询"));
        }

        return ret;
    }

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     * @param t
     * @return
     */
    public List<T> select(T t) {
        return mapper.select(t);
    }
}
