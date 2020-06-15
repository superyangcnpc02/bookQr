package com.yxtech.sys.controller;

import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.QrUserInfoMapper;
import com.yxtech.sys.domain.User;
import com.yxtech.sys.service.UserCenterService;
import com.yxtech.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cuihao
 * @create 2017-12-12-14:00
 */

@RestController("UserCenterController")
@Scope("prototype")
@RequestMapping(value = "/userCenter")
public class UserCenterController {
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private QrUserInfoMapper qrUserInfoMapper;
    @Autowired
    private UserService userService;

    /**
     * 导入微信用户
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/weixin",method = RequestMethod.GET)
    public JsonResult postWeixin(HttpServletRequest request) throws Exception{
        List<String> list = qrUserInfoMapper.getOpenIds();
        for(int i=0;i<list.size();i++){
            System.out.println("------------"+i);
            String openid = list.get(i);
            userCenterService.userCenter(1,openid,request);
        }

        return new JsonResult(true,"导入微信用户成功");
    }

    /**
     * 导入系统用户
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/sys",method = RequestMethod.GET)
    public JsonResult postSys(HttpServletRequest request) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",1);
        List<User> list = userService.selectByExample(example);

        for(int i=0;i<list.size();i++){
            System.out.println("------------"+i);
            User user = list.get(i);
            userCenterService.addNativ(user.getId(),request);
        }

        return new JsonResult(true,"导入系统用户成功");
    }

}
