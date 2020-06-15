package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultId;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.Adv;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.dto.AdvBookRelationDto;
import com.yxtech.sys.service.AdvService;
import com.yxtech.sys.service.BookService;
import com.yxtech.sys.vo.adv.AdvVo;
import com.yxtech.utils.file.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * create by zml on 2017/10/23 8:54
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/adv")
public class AdvController {

    @Autowired
    private AdvService advService;
    @Autowired
    private BookService bookService;

    /**
     * 增加 广告位
     * @param advVo
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultId addAdv(@RequestBody AdvVo advVo) {
        Assert.notNull(advVo.getUrl(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图片路径"));
        Assert.notNull(advVo.getImgUrl(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图片路径"));
        Assert.notNull(advVo.getName(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "广告名称"));
        return advService.addAdv(advVo);
    }

    /**
     * 删除 广告位
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResult deleteAdvById(@RequestParam("id") Integer id) {
        Assert.isTrue(id != null && id.longValue() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "广告位ID"));
        return advService.deleteAdvById(id);
    }

    /**
     * 修改 广告位
     * @param advVo
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public JsonResult editAdvById(@RequestBody AdvVo advVo) {
        Assert.isTrue(advVo.getId() != null && advVo.getId().longValue() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "广告位ID"));
        Assert.notNull(advVo.getImgUrl(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图片路径"));
        Assert.notNull(advVo.getName(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "广告名称"));
        return advService.editAdvById(advVo);
    }

    /**
     * 列表 广告位
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage getAdvList(@RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                     @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return advService.getAdvList(StringUtil.escape4Like(keyword), pageNo, pageSize);
    }

    /**
     * 根据二维码 id 获取所有广告位列表
     * @param qrId
     * @return
     */
    @RequestMapping(value = "/getListByQrId", method = RequestMethod.GET)
    public JsonResultList getAdvListByQrId(@RequestParam("qrId") Integer qrId){
        Assert.isTrue(qrId != null && qrId > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        return advService.getAdvListByQrId(qrId);
    }

    /**
     * 根据权限二维码 id 获取所有广告位列表
     * @param qrId
     * @return
     */
    @RequestMapping(value = "/getListByQrAuthId", method = RequestMethod.GET)
    public JsonResultList getAdvListByAuthQrId(@RequestParam("qrAuthId") Integer qrId){
        Assert.isTrue(qrId != null && qrId > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        return advService.getAdvListByAuthQrId(qrId);
    }

    /**
     * 绑定关系
     * @return
     */
    @RequestMapping(value = "/setLink", method = RequestMethod.PUT)
    public JsonResult addAdvBookRelation(@RequestBody AdvBookRelationDto advBookRelationDto){
        Assert.isTrue(advBookRelationDto.getAdvId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "广告位ID"));
        return advService.addAdvBookRelation(advBookRelationDto);
    }

    /**
     * 获取广告位所需要用到的图书列表
     * @param author
     * @param categoryId
     * @param formal
     * @param keyword
     * @param likeType
     * @param parentCategoryId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/bookList",  method = RequestMethod.GET)
    public JsonResultPage getAdvBookList(@RequestParam(value = "author", defaultValue = "", required = false) String author,
                                         @RequestParam(value = "categoryId", defaultValue = "0", required = false) Integer categoryId,
                                         @RequestParam(value = "formal", defaultValue = "0", required = false) Integer formal,
                                         @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                         @RequestParam(value = "likeType", defaultValue = "1", required = false) Integer likeType,
                                         @RequestParam(value = "parentCategoryId", defaultValue = "0", required = false) Integer parentCategoryId,
                                         @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        return bookService.getAdvBookList(author, categoryId, formal, StringUtil.escape4Like(keyword), likeType, parentCategoryId, pageNo, pageSize);
    }


    /**
     * 获取广告位所需要用到的图书列表
     * @param author
     * @param categoryId
     * @param formal
     * @param keyword
     * @param likeType
     * @param parentCategoryId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/bookListAllSelected",  method = RequestMethod.GET)
    public JsonResultList bookListAllSelected(@RequestParam(value = "author", defaultValue = "", required = false) String author,
                                         @RequestParam(value = "categoryId", defaultValue = "0", required = false) Integer categoryId,
                                         @RequestParam(value = "formal", defaultValue = "0", required = false) Integer formal,
                                         @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                                         @RequestParam(value = "likeType", defaultValue = "1", required = false) Integer likeType,
                                         @RequestParam(value = "parentCategoryId", defaultValue = "0", required = false) Integer parentCategoryId,
                                         @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
                                         @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        return bookService.bookListAllSelected(author, categoryId, formal, StringUtil.escape4Like(keyword), likeType, parentCategoryId, pageNo, pageSize);
    }

}
