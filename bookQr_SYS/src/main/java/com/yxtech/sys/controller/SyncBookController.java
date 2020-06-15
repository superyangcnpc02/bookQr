package com.yxtech.sys.controller;

import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.service.SyncBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * create by zml on 2017/12/14 9:54
 */
@RestController
public class SyncBookController {

    @Autowired
    private SyncBookService syncBookService;

    @RequestMapping(value = "/sync/book", method = RequestMethod.PUT)
    public JsonResult syncBookBaseInfo(){
        JsonResult result = new JsonResult();
        try {
            result = syncBookService.syncBookBaseInfo();
        }catch (Exception e) {
            e.printStackTrace();
            return new JsonResult(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/sync/bookList", method = RequestMethod.PUT)
    public JsonResult syncBookList(){
        return syncBookService.syncBookList();
    }

    /**
     * 获取同步进度条
     * @return
     */
    @RequestMapping(value = "/sync/getPro", method = RequestMethod.GET)
    public double getProgress(){
        return syncBookService.getProgress();
    }

    /**
     * 重置同步进度条
     * @return
     */
    @RequestMapping(value = "/sync/reset", method = RequestMethod.GET)
    public double reset(){
        return syncBookService.reset();
    }


}
