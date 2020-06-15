package com.yxtech.sys.controller;

import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.service.ScratchService;
import com.yxtech.utils.file.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

/**
 * Created by lsn on 2018/1/9.
 */
@RestController
@RequestMapping(value = "/scratch")
public class ScratchController {
    @Autowired
    private ScratchService scratchService;


    /**
     * 刮刮卡二维码列表
     * @param editor
     * @param isbns
     * @param keyword
     * @param orderTime
     * @param pressId
     * @param type
     * @param pageNo
     * @param pageSize
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JsonResultPage list(@RequestParam(value = "isbns", defaultValue = "", required = false) String isbns,
                               @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                               @RequestParam(value = "pressId", defaultValue = "0", required = false) Integer pressId,
                               @RequestParam(value = "editor", defaultValue = "0", required = false) Integer editor,
                               @RequestParam(value = "type", defaultValue = "0", required = false) int type,
                               @RequestParam(value = "orderTime",defaultValue = "0",required = false) String orderTime,
                               @RequestParam(value = "categoryId",defaultValue = "0",required = false) int categoryId,
                               @RequestParam(value = "categorysuperId",defaultValue = "0",required = false) int categorysuperId,
                               @RequestParam("pageNo") int pageNo,
                               @RequestParam("pageSize") int pageSize){
        Assert.notNull(pageNo, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.notNull(pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
       return scratchService.getList(editor,isbns, StringUtil.escape4Like(keyword),orderTime,pressId,type,categoryId,categorysuperId,pageNo,pageSize);

    }

    /**
     * 导出刮刮卡二维码生成记录
     * @param editor
     * @param isbns
     * @param keyword
     * @param pressId
     * @param type
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportList",method = RequestMethod.GET)
    public void exportList(@RequestParam(value = "type", defaultValue = "0", required = false) int type,
                           @RequestParam(value = "pressId", defaultValue = "0", required = false) Integer pressId,
                           @RequestParam(value = "editor", defaultValue = "0", required = false) Integer editor,
                           @RequestParam(value = "isbns", defaultValue = "", required = false) String isbns,
                           @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword,
                           @RequestParam(value = "categoryId",defaultValue = "0",required = false) int categoryId,
                           @RequestParam(value = "categorysuperId",defaultValue = "0" +
                                   "",required = false) int categorysuperId,
                           HttpServletRequest request,
                           HttpServletResponse response)throws Exception{
        scratchService.exportList(editor,isbns,StringUtil.escape4Like(keyword),pressId,type,categoryId,categorysuperId,request,response);
    }
}
