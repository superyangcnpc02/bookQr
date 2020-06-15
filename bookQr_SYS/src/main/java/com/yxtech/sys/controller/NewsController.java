package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.datapiess.DataPermission;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.News;
import com.yxtech.sys.service.NewsService;
import com.yxtech.sys.vo.NewsStyleVo;
import com.yxtech.sys.vo.bookQr.BookQrStyleVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zml on 2016/9/10.
 */
@RestController
@RequestMapping(value = "/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 宣传列表
     * @param pageNo
     * @param pageSize
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage getNewsList(@RequestParam(value = "page")int pageNo,
                                      @RequestParam(value = "pageSize")int pageSize){
        Assert.isTrue(Constant.ZERO < pageNo, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.isTrue(Constant.ZERO < pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return newsService.getNewsList(pageNo, pageSize);
    }

    /**
     * 新增宣传
     * @param news
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResult addNews(@RequestBody News news, HttpServletRequest request){
        Assert.notNull(news.getTitle(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "标题"));
        Assert.notNull(news.getContent(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "内容"));
        return newsService.addNews(news, request);
    }

    /**
     * 删除宣传
     * @param id
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResult deleteNews(@RequestParam(value = "id")int id, HttpServletRequest request) {
        Assert.isTrue(Constant.ZERO < id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "路径"));
        return newsService.deleteNews(id, request);
    }

    /**
     * 编辑宣传
     * @param news
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public JsonResult editNews(@RequestBody News news){
        Assert.notNull(news.getTitle(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "标题"));
        Assert.notNull(news.getContent(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "内容"));
        return newsService.editNews(news);
    }

    /**
     * 宣传明细
     * @param id
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResultData newsDetail(@RequestParam(value = "id") int id){
        Assert.isTrue(Constant.ZERO < id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "主键"));
        return newsService.newsDetail(id);
    }

    /**
     * 导出宣传二维码
     * @param id
     * @param request
     * @param response
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportQrCode(@RequestParam(value = "id")int id,
                             HttpServletRequest request,
                             HttpServletResponse response){
        newsService.exportQrCode(id, request, response);
    }

    /**
     * 修改二维码样式
     * @param vo 二维码NewsStyleVo
     * @param request  request
     * @return  返回json
     * @throws Exception
     */
    @RequestMapping(value = "/style", method = RequestMethod.PUT)
    public Map<String, Object> addStyle(@RequestBody NewsStyleVo vo, HttpServletRequest request) throws Exception {
        Assert.notNull(vo.getIds(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "宣传二维码id数组"));

        Map<String, Object> map;
        map = this.newsService.editStyle(vo, request);
        return map;
    }




}
