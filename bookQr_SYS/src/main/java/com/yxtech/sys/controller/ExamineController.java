package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.User;
import com.yxtech.sys.service.ClientAttributeService;
import com.yxtech.sys.vo.ExamineSendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by Chenxh on 2015/9/22.
 */

@RestController
@Scope("prototype")
@RequestMapping(value = "/examine")
public class ExamineController {
    @Autowired
    private ClientAttributeService attributeService;
    /**
     * 审核通过
     * @param examineSendVo
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/send",method = RequestMethod.PUT)
    public JsonResult send(@RequestBody ExamineSendVo examineSendVo) throws Exception{
        Assert.notNull(examineSendVo.getIds(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "id字符串"));

        User user = CurrentUser.getUser();
//        if(user.getRole() != 3){//0：管理员；1：编辑员；2：总编  3:审核员
//            return new JsonResult(true,"必须是审核员才能审核!");
//        }

        boolean status = attributeService.send(examineSendVo.getIds(),examineSendVo.getFlag(),examineSendVo.getReason());

        if(status){
            return new JsonResult(true,"审核成功!");
        }else{
            return new JsonResult(true,"审核失败!");
        }
    }

    /**
     * 邮箱审核
     * @param id
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/emailSend",method = RequestMethod.PUT)
    public JsonResult emailSend(String id) throws Exception{
        Assert.notNull(id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "id字符串"));

        ArrayList<Object> ids = new ArrayList<>();
        ids.add(id);
        boolean status = attributeService.send(ids, Constant.ONE,"");

        if(status){
            return new JsonResult(true,"审核成功!");
        }else{
            return new JsonResult(true,"审核失败!");
        }
    }


    /**
     * 审核列表
     * @param status  0:全部 1:待审核 2：审核通过（发送邮件）3：不通过
     * @param pageNo  页码
     * @param pageSize  条数
     * @param bookId  书籍id
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JsonResultPage getList(int status ,int bookId,
                                      @RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Assert.isTrue(status>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "status状态码"));
        Assert.isTrue(bookId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书籍id"));

        User user = CurrentUser.getUser();

        return attributeService.getList(status,bookId,user.getId(),pageNo,pageSize);
    }

    @RequestMapping(value = "/exportList",method = RequestMethod.GET)
    public void exportList(@RequestParam(value = "bookId") int bookId,
                           @RequestParam(value = "status") int status,
                           HttpServletRequest request,
                           HttpServletResponse response)throws Exception{

        Assert.notNull(status, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "status状态码"));
        Assert.isTrue(bookId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书籍id"));

        User user=CurrentUser.getUser();
        Integer userId=user.getId();
        attributeService.exportList(userId,bookId,status, request, response);
    }


}
