package com.yxtech.sys.controller;

import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultId;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.sys.domain.BookCatalog;
import com.yxtech.sys.service.BookCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * create by zml on 2017/10/23 8:57
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/bookCategory")
public class BookCatalogController {

    @Autowired
    private BookCatalogService bookCatalogService;

    /**
     * 增加 图书分类
     * @param bookCatalog
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultId addBookCatalog(@RequestBody BookCatalog bookCatalog) {
        Assert.isTrue(bookCatalog.getLevelId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "节点层级数"));
        Assert.notNull(bookCatalog.getName(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "节点名称"));
        Assert.isTrue(bookCatalog.getParentId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "父节点ID"));
        return bookCatalogService.addBookCatalog(bookCatalog);
    }

    /**
     * 删除 图书分类
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResult deleteBookCatalogById(@RequestParam("id") Integer id) {
        Assert.isTrue(id != null && id.longValue() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "节点ID"));
        return bookCatalogService.deleteBookCatalogById(id);
    }

    /**
     * 修改 图书分类
     * @param bookCatalog
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public JsonResult editBookCatalogById(@RequestBody BookCatalog bookCatalog) {
        Assert.isTrue(bookCatalog.getId() != null && bookCatalog.getId().longValue() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "节点ID"));
        Assert.notNull(bookCatalog.getName(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "节点名称"));
        return bookCatalogService.editBookCatalogById(bookCatalog);
    }

    /**
     * 列表 图书分类
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultList getBookCatalogList() {
        return bookCatalogService.getBookCatalogList();
    }

}
