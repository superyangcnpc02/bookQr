package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.Press;
import com.yxtech.sys.service.PressService;
import com.yxtech.utils.file.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by liukailong on 2016/09/10
 * 出版社Controller
 */
@RestController
@RequestMapping(value = "/press")
public class PressController {

    @Autowired
    private PressService pressService;

    /**
     * 新增出版社
     * @param press
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultData addResource(@RequestBody Press press){
        Assert.notNull(press.getName(),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社名称"));
        Assert.notNull(press.getRemark(),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社简介"));

        press.setCreateTime(new Date());
        press.setCreateId(CurrentUser.getUser().getId());//TODO
//        press.setCreateId(1);
        return pressService.addPress(press);
    }

    /**
     * 列表
     * @param pageSize
     * @param page
     * @param keyword
     * @return
     * @author liukailong
     * @date 2016-09-10
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage list(@RequestParam("pageSize") int pageSize,
                               @RequestParam("page") int page,
                               @RequestParam(value="keyword",required = false) String keyword) {
        Assert.notNull(page, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.notNull(pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return pressService.getList(page, pageSize, StringUtil.escape4Like(keyword));
    }

    /**
     * 编辑
     * @param press
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public JsonResult editResource(@RequestBody Press press){
        Assert.notNull(press.getId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社ID"));
        Assert.notNull(press.getName(),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社名称"));
        Assert.notNull(press.getRemark(),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社简介"));
        return pressService.editPress(press);
    }

    /**
     * 删除
     * @param id
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResult remove(@RequestParam("id") int id){
        Assert.isTrue(id > Constant.ZERO, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "材料ID"));
        return pressService.deletePress(id);
    }
}
