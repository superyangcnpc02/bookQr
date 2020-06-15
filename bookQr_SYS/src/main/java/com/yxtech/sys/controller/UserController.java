package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.dao.PressMapper;
import com.yxtech.sys.domain.Press;
import com.yxtech.sys.domain.User;
import com.yxtech.sys.service.UserCenterService;
import com.yxtech.sys.service.UserService;
import com.yxtech.sys.vo.UserVo;
import com.yxtech.sys.vo.user.UserActiveVo;
import com.yxtech.sys.vo.user.UserCenterInfo;
import com.yxtech.utils.mail.ValidateUtil;
import com.yxtech.utils.password.MD5Util;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Chenxh on 2015/9/22.
 */

@RestController("UserController")
@Scope("prototype")
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PressMapper pressMapper;
    @Autowired
    private UserCenterService userCenterService;
    /**
     * 新增用户
     * @param user
     * @return
     * @author hesufang
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @RequiresPermissions("/user/add")
    @Transactional
    public JsonResult addUser(@Valid  @RequestBody UserVo user,HttpServletRequest request) {
        Assert.isTrue(ValidateUtil.isValidEmail(user.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户邮箱格式不正确!"));

        user.setLoginTime(new Date());
        user.setLoginNum(0);
        user.setPassword(Constant.PASSWORD);
        user.setStatus(1);
        userService.addUser(user);

        //注册到用户中心
        userCenterService.addNativ(user.getId(),request);

        return new JsonResult(true,"新增用户成功");
    }


    /**
     * 根据id 删除用户
     * @param id  用户id
     * @return
     * @author hesufang
     * @editor by liukailong 2017-01-22
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    @RequiresPermissions("/user/delete")
    public JsonResult deleteUser(@RequestParam("id") Integer id , HttpServletRequest request)throws Exception {
        Assert.isTrue(id > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户ID"));

        this.userService.deleteUser(id,request);

        return new JsonResult(true,"删除用户成功");
    }

    /**
     * 恢复用户
     * @param id  用户id
     * @return
     * @author hesufang
     * @editor by liukailong 2018-07-02
     */
    @RequestMapping(value = "/recover",method = RequestMethod.GET)
    public JsonResult recoverUser(@RequestParam("id") Integer id , HttpServletRequest request)throws Exception {
        Assert.isTrue(id > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户ID"));

        this.userService.recoverUser(id,request);

        return new JsonResult(true,"恢复用户成功");
    }

    /**
     * 激活用户(只能管理员调用,激活后进入正常队列)
     * @param vo  用户id和用户email
     * @return
     * @editor by cuihao 2017-03-13
     */
    @RequestMapping(value = "/active",method = RequestMethod.PUT)
    @Transactional
    public JsonResult activeUser(@RequestBody UserActiveVo vo , HttpServletRequest request)throws Exception {
        Assert.isTrue(vo.getId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户ID"));
        Assert.isTrue(!StringUtils.isEmpty(vo.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户邮箱"));

        this.userService.activeUser(vo,request);

        //注册到用户中心
        userCenterService.addNativ(vo.getId(),request);

        return new JsonResult(true,"激活用户成功");
    }

    /**
     * 普通激活,激活后进入待审核队列
     * @param vo  用户id和用户email
     * @return
     * @editor by cuihao 2017-03-13
     */
    @RequestMapping(value = "/activeOrdinary",method = RequestMethod.PUT)
    public JsonResult activeOrdinary(@RequestBody UserActiveVo vo , HttpServletRequest request)throws Exception {
        Assert.isTrue(vo.getId() > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户ID"));
        Assert.isTrue(!StringUtils.isEmpty(vo.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户邮箱"));
        Assert.isTrue(ValidateUtil.isValidEmail(vo.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户邮箱格式不正确!"));


        this.userService.activeOrdinary(vo,request);

        return new JsonResult(true,"激活用户成功");
    }



    /**
     * 根据id重置密码 置为123456
     *
     * @param id id
     * @return JsonResult
     * @author hesufang
     * @since 2015-10-15.
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @RequiresPermissions("/user/resetPassword")
    @Transactional
    public JsonResult resetPassword(@RequestParam("id") Integer id,HttpServletRequest request) {

        Assert.isTrue(id > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "用户ID"));

        User user = new User();
        user.setId(id);
        user.setPassword(Constant.PASSWORD);

        userService.updateByPrimaryKeySelective(user);

        //更新用户中心信息
        userCenterService.updateNativ(id,request);

        return new JsonResult(true,  "重置密码成功");
    }

    /**
     * 修改用户信息
     * @param user  用户实体
     * @return
     * @author hesufang
     */
    @RequestMapping(value = "/edit",method = RequestMethod.PUT)
    @Transactional
//    @RequiresPermissions("/user/edit")
    public JsonResult editUser(@RequestBody UserVo user,HttpServletRequest request) {
        userService.updateUserByKey(user);

        //更新用户中心信息
        userCenterService.updateNativ(user.getId(),request);

        return new JsonResult(true,"修改用户成功");
    }





    /**
     * 修改密码

     * @return JsonResult
     * @author hesufang
     * @since 2015-10-15.
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    @Transactional
//    @RequiresPermissions("/user/password")
    public JsonResult updatePassword(@RequestBody UserVo user,HttpServletRequest request) {

        user.setId(CurrentUser.getUser().getId());

        userService.updatePassword(user);

        //更新用户中心信息
        userCenterService.updateNativ(user.getId(),request);

        return new JsonResult(true,  "修改密码成功");
    }



    /**
     * 获取用户列表
     * @param pageNo  页码
     * @param pageSize  条数
     * @param roleId 角色Id 0：管理员；1：编辑员；2：总编  3:审核员
     * @param keyword  关键字
     * @return
     * @author hesufang (zml改)
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
//    @RequiresPermissions("/user/list")
    public JsonResultPage getUserList(@RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                      @RequestParam(value = "roleId", defaultValue = "-1") int roleId,
                                      @RequestParam(value = "status", defaultValue = "1") int status,
                                      @RequestParam(value = "pressId",defaultValue = "0") int pressId,
                                      @RequestParam(value = "keyword") String keyword) {

        Example example = new Example(User.class);
        example.selectProperties("id","email","phone","userName","loginNum","loginTime","role","photo","pressId","status");
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria orCriteria = example.or();
        if(null!=keyword && keyword.trim().length() > 0) {
            criteria.andLike("email", "%" + keyword + "%");
            orCriteria.andLike("userName","%"+keyword+"%");
        }
        if (roleId != -1){
            criteria.andEqualTo("role", roleId);
            orCriteria.andEqualTo("role", roleId);
        }
        if(status!=0){
            criteria.andEqualTo("status", status);
            orCriteria.andEqualTo("status", status);
        }else{
            criteria.andLessThanOrEqualTo("status",4);
            orCriteria.andLessThanOrEqualTo("status",4);
        }
        if(pressId!=0){
            criteria.andEqualTo("pressId", pressId);
            orCriteria.andEqualTo("pressId", pressId);
        }
        example.setOrderByClause("id DESC");
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage userPage = null;
        userPage = userService.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        if(userPage != null){
            List<User> userList = (List<User>) userPage.getItems();
            for(User user : userList){
                Press press = pressMapper.selectByPrimaryKey(user.getPressId());
                if (!StringUtils.isEmpty(press)) {
                    user.setPress(press.getName());
                }
            }
        }
        return userPage;
    }


    /**
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JsonResultData userLogin(@RequestBody User user, HttpServletRequest request) {
        //1.去社用户中心校验
        UserCenterInfo info = userCenterService.checkIsExist(user.getEmail(), user.getPassword());
        int code = info.getCode();
        if(code !=0 ){
            return new JsonResultData(false, "登录失败！用户名或密码错误");
        }else{
            String userId = info.getUserId();
            User userEp = new User();
            userEp.setEmail(user.getEmail());
            List<User> userList = userService.select(userEp);
            if(!CollectionUtils.isEmpty(userList)){
                User u = userList.get(0);
                u.setUserId(userId);
                userService.updateByPrimaryKeySelective(u);
            }
        }
        //2.本地校验,只是形式上
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), Constant.PASSWORD);
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            token.clear();
            return new JsonResultData(false, "用户账户未激活或已被删除!");
        }
        User reUser = (User) request.getSession().getAttribute("user");
        String pressName="";
        if(reUser.getPressId()!=null && reUser.getPressId()!=0){
            Press press = pressMapper.selectByPrimaryKey(reUser.getPressId());
            pressName = press.getName();
        }

        return new JsonResultData(new User(reUser.getId(),reUser.getRole(), reUser.getPhoto(), reUser.getUserName(),reUser.getPhone(),reUser.getPressId(),pressName));
    }

    /**
     * 退出登录
     * @return
     * @author lyj
     * @since 2015-10-21
     */
    @RequestMapping(value = "/loginOut",method = RequestMethod.POST)
    public JsonResult loginOut() {
        Subject subject = SecurityUtils.getSubject();

        if (subject.isAuthenticated()) {
            subject.logout();
        }
        return new JsonResult(true, "退出成功");
    }


    /**
     * 权限异常处理
     * @param response
     * @return
     */
    @RequestMapping(value = "/403")
    public JsonResult sessionTimeOut(HttpServletResponse response) {
        response.setStatus(403);
        return new JsonResult(false,"权限异常，请重新登录");
    }

    /**
     * 编辑用户下拉框
     * @param pressId
     * @author liukailong
     * @return
     */
    @RequestMapping(value = "/editorList", method = RequestMethod.GET)
    public JsonResultList editorList(@RequestParam(name = "pressId") String pressId) {
        Example ex = new Example(User.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("role","1");
        criteria.andEqualTo("status","1");
        if(!StringUtils.isEmpty(pressId)){
            criteria.andEqualTo("pressId",pressId);
        }
        List<User> list = this.userService.selectByExample(ex);
        return new JsonResultList(true, MessageFormat.format(ConsHint.SUCCESS, "编辑用户下拉框"), list);
    }

    /**
     * 注册(已废弃)
     * @param user
     * @return
     */
    @RequestMapping(value = "/registe", method = RequestMethod.POST)
    public JsonResult registe(@RequestBody User user){
        return userService.registe(user);
    }

    /**
     * 审核注册的用户信息
     * @param user
     * @return
     */
    @RequestMapping(value = "/examine", method = RequestMethod.PUT)
    public JsonResult examine(@RequestBody User user,HttpServletRequest request){
        return userService.examine(user,request);
    }

    /**
     * 注册信息审核列表
     * @param keyword
     * @param pressId
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/examineList", method = RequestMethod.GET)
    public JsonResultPage examineList(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      @RequestParam(value = "pressId") int pressId,
                                      @RequestParam(value = "status") int status,
                                      @RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return userService.examineList(keyword, pressId, status, pageNo, pageSize);
    }

    /**
     * 该分社下是否存在该用户名
     * @return
     */
    @RequestMapping(value = "/registeName", method = RequestMethod.GET)
    public JsonResult getPressEditorIfExists(@RequestParam(value = "name") String name, @RequestParam(value = "pressId") int pressId){
        Assert.isTrue(Constant.ZERO < pressId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分社主键"));
        Assert.notNull(name, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "编辑名"));
        return userService.getPressEditorIfExists(name, pressId);
    }

    /**
     * 该邮箱是否存在
     * @return
     */
    @RequestMapping(value = "/registeEmail", method = RequestMethod.GET)
    public JsonResult getEmailIfExists(@RequestParam(value = "email") String email){
        Assert.notNull(email, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱不能为空"));
        return userService.getEmailIfExists(email);
    }

}
