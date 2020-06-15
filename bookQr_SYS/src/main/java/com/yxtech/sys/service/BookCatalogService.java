package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultId;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.sys.dao.BookCatalogMapper;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookCatalog;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * create by zml on 2017/10/23 8:58
 */
@Service
public class BookCatalogService extends BaseService<BookCatalog>{

    @Autowired
    private BookCatalogMapper bookCatalogMapper;
    @Autowired
    private BookMapper bookMapper;

    @Resource(name = "bookCatalogMapper")
    public void setBookCatalogMapper(BookCatalogMapper bookCatalogMapper) {
        setMapper(bookCatalogMapper);
        this.bookCatalogMapper = bookCatalogMapper;
    }


    /**
     * 增加 图书分类
     * @param bookCatalog
     * @return
     */
    public JsonResultId addBookCatalog(BookCatalog bookCatalog) {
        bookCatalogMapper.insertSelective(bookCatalog);
        return new JsonResultId(true, "新增成功", bookCatalog.getId());
    }

    /**
     * 删除 图书分类
     * @param id
     * @return
     */
    @Transactional
    public JsonResult deleteBookCatalogById(Integer id) {
        // 递归删除子节点
        List<BookCatalog> resultList = new ArrayList<BookCatalog>();
        resultList = getChildBookCatalog(resultList, id);
        List<Object> delIds = new ArrayList<>();
        delIds.add(id);
        for (BookCatalog bookCatalog : resultList) {
            delIds.add(bookCatalog.getId());
        }

        for(Object delId:delIds){
            // 如果存在图书则不能删除
            Example bookEx = new Example(Book.class);
            bookEx.createCriteria().andEqualTo("categoryId", delId);
            int count = bookMapper.selectCountByExample(bookEx);
            if (count > 0) {
                return new JsonResult(false, "该分类下已绑定图书，删除失败！");
            }
        }

        Example example = new Example(BookCatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", delIds);
        int num = bookCatalogMapper.deleteByExample(example);
        if (num == 0) {
            return new JsonResult(false, "该数据不存在或已被删除！");
        }

        return new JsonResult(true, "删除成功");
    }

    /**
     * 修改 图书分类
     * @param bookCatalog
     * @return
     */
    public JsonResult editBookCatalogById(BookCatalog bookCatalog) {
        int num = bookCatalogMapper.updateByPrimaryKeySelective(bookCatalog);
        if (num != 1) {
            return new JsonResult(false, "该数据不存在或已被删除, 修改失败！");
        }
        return new JsonResult(true, "修改成功");
    }

    /**
     * 列表数据
     * @return
     */
    public JsonResultList getBookCatalogList() {
        List<BookCatalog> resultList = new ArrayList<BookCatalog>();
//        // 获取根节点数据节点列表
//        List<BookCatalog> bookCatalogList = getRootBookCatalogList();
//        for (BookCatalog bookCatalog : bookCatalogList) {
//            // 设置是否是父节点值
//            bookCatalog.setIsParent(isParentNode(bookCatalog));
//            resultList.add(bookCatalog);
//            // 递归查询子节点
//            resultList = getChildBookCatalog(resultList, bookCatalog.getId());
//        }
        resultList = getChildBookCatalog(resultList, 0);
        return new JsonResultList(true, "ok", resultList);
    }

    /**
     * 获取 图书分类 根节点数据列表
     * @return
     */
    public List<BookCatalog> getRootBookCatalogList(){
        Example example = new Example(BookCatalog.class);
        example.setOrderByClause("id asc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", 0l);
        return bookCatalogMapper.selectByExample(example);
    }

    /**
     * 递归查询所有子节点
     * @param resultList
     * @param id
     * @return
     */
    public List<BookCatalog> getChildBookCatalog(List<BookCatalog> resultList, Integer id){
        Example example = new Example(BookCatalog.class);
        example.setOrderByClause("id asc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", id);
        List<BookCatalog> bookCatalogList = bookCatalogMapper.selectByExample(example);
        for (BookCatalog bookCatalog : bookCatalogList) {
            // 设置是否是父节点值
            bookCatalog.setIsParent(isParentNode(bookCatalog));
            resultList.add(bookCatalog);
            // 递归结束条件
            if (bookCatalog.getIsParent()) {
                // 接着查
                getChildBookCatalog(resultList, bookCatalog.getId());
            }
        }
        return resultList;
    }

    /**
     * 根据节点对象查询该节点是否是父节点
     * @param bookCatalog
     * @return
     */
    public Boolean isParentNode(BookCatalog bookCatalog){
        Example example = new Example(BookCatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", bookCatalog.getId());
        int count = bookCatalogMapper.selectCountByExample(example);
        if (count != 0 ) {
            return true;
        }
        return false;
    }

    /**
     * 根据节点主键查询该节点是否是父节点
     * @param id
     * @return
     */
    public Boolean isParentNode(Integer id){
        BookCatalog bookCatalog = new BookCatalog();
        bookCatalog.setId(id);
        return isParentNode(bookCatalog);
    }

    public List<BookCatalog> getBookCatalogInIds(List<Object> ids){
        Example example = new Example(BookCatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        return  bookCatalogMapper.selectByExample(example);
    }

    /**
     * 根据类别名称获取类别对象
     * @param ztfCategory
     * @return
     */
    public BookCatalog getBookCatalogIdByName(String ztfCategory) {
        Example example = new Example(BookCatalog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", ztfCategory);
        List<BookCatalog> bookCatalogList = bookCatalogMapper.selectByExample(example);
        if (bookCatalogList.size() != 0) {
            return bookCatalogList.get(0);
        }
        return null;
    }
}
