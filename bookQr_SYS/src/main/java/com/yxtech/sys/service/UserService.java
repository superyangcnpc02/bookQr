package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.PressMapper;
import com.yxtech.sys.dao.UserMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.UserVo;
import com.yxtech.sys.vo.user.UserActiveVo;
import com.yxtech.utils.file.PathUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chenxh on 2015/9/22.
 */

@Service
public class UserService extends BaseService<User> {

    @Autowired
    private PressMapper pressMapper;

    private UserMapper userMapper;

    public UserMapper getUserMapper() {
        return userMapper;
    }

    @Resource(name = "userMapper")
    public void setUserMapper(UserMapper userMapper) {
        setMapper(userMapper);
        this.userMapper = userMapper;
    }

    @Autowired
    private FileResService fileResService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UseOrgService useOrgService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserCenterService userCenterService;

    /**
     * 新增用户
     * @param user  用户对象
     */
    @Transactional
    public void addUser(User user){

        //判断用户邮箱是否已存在
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("email",user.getEmail());
        if (userMapper.selectCountByExample(example)>0){
            log.warn("邮箱已存在，新增用户失败");
            throw new ServiceException(user.getEmail()+"邮箱已存在，新增用户失败");
        }

        //判断用户邮箱是否已存在
        Example example2 = new Example(User.class);
        if(!StringUtils.isEmpty(user.getPressId())){
            example2.createCriteria().andEqualTo("userName",user.getUserName()).andEqualTo("pressId",user.getPressId());
            if (userMapper.selectCountByExample(example2)>0){
                log.warn("用户名已存在，新增用户失败");
                throw new ServiceException(user.getUserName()+"用户名已存在，新增用户失败");
            }
        }


        //1.新增组织信息  t_org//新增一条自己的
        Org org = new Org();
        org.setName(user.getUserName());
        orgService.insertSelective(org);

        user.setOrgId(org.getId());

        //2.新增用户信息  t_user
        userMapper.insert(user);

        //3.新增数据权限信息   t_use_org
        insertUseOrg(user);

    }


    /**
     * 批量新增用户权限
     * @param user 当前用户实体
     * @author lyj
     * @since 2015-10-30
     */
    public void insertUseOrg(User user) {

        //把当前用户信息放到权限表
        List<UseOrg> useOrgList = new ArrayList<>();
        useOrgList.add(new UseOrg(user.getId(), user.getOrgId()));

        //如果当前用户是总编
        if (user.getRole() == Constant.EDITOR_CHIEF) {
            //查询出所有的编辑员信息
            User tempUser = new User();
            tempUser.setRole(Constant.EDITOR);
            List<User> reUser = super.select(tempUser);

            for (User temp : reUser) {
                //绑定当前用户和下属用户的权限
                useOrgList.add(new UseOrg(user.getId(),temp.getOrgId()));
            }

            //查询出所有审核员信息
            User examUser = new User();
            examUser.setRole(Constant.EXAMINER);
            List<User> userList = super.select(examUser);

            for (User temp : userList) {
                //绑定审核员和当前用户的权限
                useOrgList.add(new UseOrg(temp.getId(),user.getOrgId()));
            }
        } else if (user.getRole() == Constant.EDITOR) {
            //查询出所有总编的信息
            User tempUser = new User();
            tempUser.setRole(Constant.EDITOR_CHIEF);
            List<User> reUser = super.select(tempUser);

            for (User temp : reUser) {
                //绑定总编和当前用户的权限
                useOrgList.add(new UseOrg(temp.getId(),user.getOrgId()));
            }

            //查询出所有审核员信息
            User examUser = new User();
            examUser.setRole(Constant.EXAMINER);
            List<User> userList = super.select(examUser);

            for (User temp : userList) {
                //绑定审核员和当前用户的权限
                useOrgList.add(new UseOrg(temp.getId(),user.getOrgId()));
            }
        }else if(user.getRole() == Constant.EXAMINER){
            //审核员,查询出所有总编信息
            User tempUser = new User();
            tempUser.setRole(Constant.EDITOR_CHIEF);
            List<User> reUser = super.select(tempUser);
            for (User temp : reUser) {
                //绑定当前用户和下属用户的权限
                useOrgList.add(new UseOrg(user.getId(),temp.getOrgId()));
            }

            //查询出所有的编辑员信息
            User tempUser1 = new User();
            tempUser1.setRole(Constant.EDITOR);
            List<User> reUser1 = super.select(tempUser1);

            for (User temp : reUser1) {
                //绑定当前用户和下属用户的权限
                useOrgList.add(new UseOrg(user.getId(),temp.getOrgId()));
            }
        }

        useOrgService.insertBatch(useOrgList);
    }

    /**
     * 修改用户信息
     * @param user  用户实体
     * @return
     */
    public void updateUserByKey(User user) {

        //判断用户名是否已存在
        Example example2 = new Example(User.class);
        if(!StringUtils.isEmpty(user.getPressId())){
            example2.createCriteria().andEqualTo("userName",user.getUserName()).andEqualTo("pressId",user.getPressId()).andNotEqualTo("id",user.getId());
            if (userMapper.selectCountByExample(example2)>0){
                log.warn("用户名已存在，编辑用户失败");
                throw new ServiceException(user.getUserName()+"用户名已存在，编辑用户失败");
            }
        }

        //获取用户原来的角色信息
        User reUser = userMapper.selectByPrimaryKey(user.getId());
        //修改用户信息
        userMapper.updateByPrimaryKeySelective(user);

        //add by liukailong begin
        Map<String,Object> map  = new HashMap<>();
        map.put("id",user.getId());
        map.put("pressId",user.getPressId());
        userMapper.updateUserPressById(map);
        //add by liukailong end

        //如果是用户个人修改信息，不修改用户权限
        if (user.getRole() != null) {
            //如果用户修改了角色
            if (reUser.getRole() != user.getRole()) {
                //删除用户原来的权限
                Example example = new Example(UseOrg.class);
                example.createCriteria().andEqualTo("userId", reUser.getId());
                example.or().andEqualTo("orgId", reUser.getOrgId());
                useOrgService.deleteByExample(example);

                //新增用户数据权限
                user.setOrgId(reUser.getOrgId());
                insertUseOrg(user);
            }
        }
    }


    /**
     * 修改用户密码
     * @param user
     */
    public void updatePassword(UserVo user){

        //根据id 判断原密码 是否正确
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("id",user.getId()).andEqualTo("password",user.getPassword());

        //判断用户密码是否正确
        if (userMapper.selectCountByExample(example)!=1){
              log.warn("原密码错误");
            throw new ServiceException("原密码错误");
        }
        user.setPassword(user.getNewPassword());
        userMapper.updateByPrimaryKeySelective(user);
    }


    /**
     * 删除用户  同时删除头像图片
     * @param id
     * @param request
     * @throws Exception
     * @editor by liukailong 2017-01-22
     */
    public void deleteUser(Integer id,HttpServletRequest request) throws Exception {
        String rootPath = PathUtil.getAppRootPath(request);
        User user=this.userMapper.selectByPrimaryKey(id);

        //验证是否存在
        if(null==user)
            return ;
  
        if(user.getStatus()==2){
            userMapper.deleteByPrimaryKey(id);
            Book book = new Book();
            book.setOnwer(id);
            bookMapper.delete(book);
        }else{
            user.setStatus(3);
            userMapper.updateByPrimaryKeySelective(user);
        }

//        //删除t_organization
//        orgService.deleteByPrimaryKey(user.getOrgId());
//
//        //删除t_use_org
//        Example example = new Example(UseOrg.class);
//        example.createCriteria().andEqualTo("userId", id);
//        useOrgService.deleteByExample(example);
//
//
//        //删除用户
//        userMapper.deleteByPrimaryKey(id);
//
//        if (null==user.getPhoto()|| "".equals(user.getPhoto()))
//            return;
//        //查询出对应的二维码图片存储信息
//        FileRes fileRes =new FileRes();
//        fileRes.setUuid(user.getPhoto());
//        fileRes =this.fileResService.selectOne(fileRes);
//        File file = null;
//        if (null != fileRes){
//            file = new File(rootPath + fileRes.getPath());
//        }
//
//        //验证图片是否存在，存在则删除
//        if(file.exists()) {
//            boolean flag=file.delete();
//        }
//        //删除文件系统数据
//        this.fileResService.deleteByPrimaryKey(fileRes.getId());


    }

    /**
     * 恢复删除的用户
     * @param id
     * @param request
     * @throws Exception
     * @editor by liukailong 2017-01-22
     */
    public void recoverUser(Integer id,HttpServletRequest request) throws Exception {
        User user=this.userMapper.selectByPrimaryKey(id);

        //验证是否存在
        if(null==user)
            return ;

        if(user.getStatus()==3){
            user.setStatus(1);
            userMapper.updateByPrimaryKeySelective(user);
        }else{
            throw new ServiceException("用户不是删除状态,无法恢复!");
        }

    }

    /**
     * 激活用户
     * @param vo  用户id和用户email
     * @return
     * @editor by cuihao 2017-03-13
     */
    public void activeUser(UserActiveVo vo, HttpServletRequest request) throws Exception {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email",vo.getEmail());
        List<User> list = userMapper.selectByExample(example);

        if(list!=null && list.size()!=0){
            throw new ServiceException("该邮箱的用户已经存在!");
        }

        User user=this.userMapper.selectByPrimaryKey(vo.getId());
        //验证是否存在
        if(null==user)
            throw new ServiceException("用户不存在!");
        user.setStatus(1);
        user.setEmail(vo.getEmail());
        user.setPhone(vo.getPhone());
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 普通激活,激活后进入待审核队列
     * @param vo  用户id和用户email
     * @return
     * @editor by cuihao 2017-03-13
     */
    public void activeOrdinary(UserActiveVo vo, HttpServletRequest request) throws Exception {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email",vo.getEmail());
        List<User> list = userMapper.selectByExample(example);

        if(list!=null && list.size()!=0){
            throw new ServiceException("该邮箱的用户已经存在!");
        }

        User user=this.userMapper.selectByPrimaryKey(vo.getId());
        //验证是否存在
        if(null==user)
            throw new ServiceException("用户不存在!");
        user.setStatus(4);
        user.setEmail(vo.getEmail());
        user.setPhone(vo.getPhone());
        userMapper.updateByPrimaryKeySelective(user);
    }

    public Integer getUserIdByUserName(String editor, int pressId) {
        return userMapper.getUserIdByUserName(editor, pressId);
    }

    public String getUserNameByUserId(int editorId){
        return userMapper.selectByPrimaryKey(editorId).getUserName();
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public JsonResult registe(User user) {
        user.setStatus(4);
        //1.新增组织信息  t_org//新增一条自己的
        Org org = new Org();
        org.setName(user.getUserName());
        orgService.insertSelective(org);
        user.setOrgId(org.getId());
        //2.新增用户信息  t_user
        userMapper.insertSelective(user);
        //3.新增数据权限信息   t_use_org
        this.insertUseOrg(user);
        return new JsonResult(true, "注册成功，请等待审核通过");
    }

    /**
     * 审核注册的用户信息
     * @param user
     * @return
     */
    @Transactional
    public JsonResult examine(User user,HttpServletRequest request) {
        //如果被驳回,则进入未激活状态
        if(user.getStatus()==5){
            user.setStatus(2);
            user.setPhone("");
            user.setEmail("");
        }else{
            //注册到用户中心
            userCenterService.addNativ(user.getId(),request);
        }
        userMapper.updateByPrimaryKeySelective(user);

        return new JsonResult(true, "操作成功！");
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
    public JsonResultPage examineList(String keyword, int pressId, int status, int pageNo, int pageSize) {
        Example example = new Example(User.class);
        example.selectProperties("id","email","phone","userName","loginNum","loginTime","role","photo","pressId","status");
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria orCriteria = example.or();
        if(null!=keyword && keyword.trim().length() > 0) {
            criteria.andLike("email", "%" + keyword + "%");
            orCriteria.andLike("userName","%"+keyword+"%");
        }
        if(status!=0){
            criteria.andEqualTo("status", status);
            orCriteria.andEqualTo("status", status);
        }
        if(pressId!=0){
            criteria.andEqualTo("pressId", pressId);
            orCriteria.andEqualTo("pressId", pressId);
        }
        example.setOrderByClause("id DESC");
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage userPage = null;
        userPage = this.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
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
     * 注册分社下编辑名称验证
     * @param name
     * @param pressId
     * @return
     */
    public JsonResult getPressEditorIfExists(String name, int pressId) {
        int num = userMapper.getPressEditorIfExists(name, pressId);
        return new JsonResult(num == 0 ? true : false, num == 0 ? "该名称可用" : "该分社下已存在该编辑名称，请更换编辑名后重试！");
    }

    /**
     * 检测 email 的唯一性
     * @param email
     * @return
     */
    public JsonResult getEmailIfExists(String email) {
        int num = userMapper.getEmailIfExists(email);
        return new JsonResult(num == 0 ? true : false, num == 0 ? "该邮箱可用" : "该邮箱已存在，请更换邮箱后重试！");
    }

    /**
     * 同步图书数据
     * @param book
     */
    public void updateUserByEditor(Book book) throws ServiceException{
        // 第一步 查询条件是  分社 未删除 角色是编辑 的 图书编辑是否存在，存在的话返回 编辑的 ID 不存在的话新增这个编辑 并 返回编辑的 ID
        if (book != null) {
            if (book.getEditor() != null && book.getPressId() != null) {
                User user = userMapper.getUserByUserName(book.getEditor(), book.getPressId());
                if (user == null) {
                    user = new User();
                    user.setUserName(book.getEditor());
                    user.setPressId(book.getPressId());
                    user.setPassword("123456");
                    user.setRole(1);
                    user.setStatus(2);
                    userMapper.insertSelective(user);
                }
                book.setOnwer(user.getId());
            }else{
                throw new ServiceException("book code为"+book.getCode()+"的书，没有返回编辑和分社");
            }
        }
    }
}
