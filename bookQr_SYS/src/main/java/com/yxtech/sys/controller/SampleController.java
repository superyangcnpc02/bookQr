package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.ClientAttr;
import com.yxtech.sys.service.SampleService;
import com.yxtech.sys.vo.client.ClientVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

/**
 * 样书
 * Created by zml on 2016/9/10.
 */

@RestController
@Scope("prototype")
@RequestMapping(value = "/sample")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    /**
     * 样书申请审核           1:待审核  2:通过  3:不通过
     * @param clientAttr
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    public JsonResult editClientAttr(@RequestBody ClientAttr clientAttr){
        Assert.isTrue(Constant.ZERO < clientAttr.getId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "主键"));
        Assert.isTrue(Constant.ZERO < clientAttr.getStatus(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "状态"));
        return sampleService.editClientAttr(clientAttr);
    }

    /**
     * 样书申请列表
     * @param pageNo
     * @param pageSize
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage getClientAttrList(@RequestParam(value = "bookId")int bookId,
                                            @RequestParam(value = "status")int status,
                                            @RequestParam(value = "page")int pageNo,
                                            @RequestParam(value = "pageSize")int pageSize){
        Assert.isTrue(Constant.ZERO < pageNo, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.isTrue(Constant.ZERO < pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return sampleService.getClientAttrList(bookId, status, pageNo, pageSize);
    }

    /**
     * 样书申请
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public JsonResult addClientAttr(@RequestBody ClientVo clientVo){
        Assert.isTrue(Constant.ZERO < clientVo.getQrcodeId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.notNull(clientVo.getEmail(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));
        return sampleService.addClientAttr(clientVo);
    }

    /**
     * 删除样书
     * @param id
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public JsonResult deleteClientAttr(@RequestParam(value = "id")int id){
        Assert.isTrue(Constant.ZERO < id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "主键"));
        return sampleService.deleteClientAttr(id);
    }




}
