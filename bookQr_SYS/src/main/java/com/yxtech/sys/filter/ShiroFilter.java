package com.yxtech.sys.filter;

import com.yxtech.common.Constant;
import com.yxtech.sys.domain.Perm;
import com.yxtech.sys.domain.UseOrg;
import com.yxtech.sys.domain.User;
import com.yxtech.sys.service.OrgService;
import com.yxtech.sys.service.PermService;
import com.yxtech.sys.service.UseOrgService;
import com.yxtech.sys.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 登录、权限验证
 * Created by lyj on 2015/10/20.
 */
@Service
@Transactional
public class ShiroFilter extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private PermService permService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private UseOrgService useOrgService;
    @Value("#{configProperties['redis.expire']}")
    private int expire;

    /**
     * 权限认证
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String logingName = (String) principalCollection.fromRealm(getName()).iterator().next();

        User user = new User();
        user.setEmail(logingName);
        User reUser = userService.selectOne(user);

        //根据角色ID获取permission
        Example example = new Example(Perm.class);
        example.createCriteria().andEqualTo("roleId", reUser.getRole());
        example.selectProperties("url");
        List<Perm> permList = permService.selectByExample(example);
        List<String> list = new ArrayList<>();
        for (Perm perm : permList) {
            list.add(perm.getUrl());
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        info.addStringPermissions(list);
        info.addRole(reUser.getRole().toString());

        return info;

    }


    /**
     * 登录认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     * @author lyj
     * @since 2015-10-21
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //UserUsernamePasswordToken用来存放提交的登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        //用户登录验证
        User user = new User();
        user.setEmail(token.getUsername());
//        user.setPassword(String.valueOf(token.getPassword()));
        user.setStatus(1);
//        user.setRole(Integer.valueOf(token.getHost()));

        User reUser = userService.selectOne(user);
        //将用户信息存入session
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        //设置超时时间1 hour
        session.setAttribute("user", reUser);
        //过期时间
        session.setTimeout(expire*1000);

        //修改用户名最后登录时间和登录次数
        reUser.setLoginTime(new Date());
        reUser.setLoginNum(reUser.getLoginNum() + 1);
        userService.updateByPrimaryKey(reUser);


            //获取用户权限信息
            Example example = new Example(UseOrg.class);
            example.createCriteria().andEqualTo("userId", reUser.getId());
            List<UseOrg> useOrgList = useOrgService.selectByExample(example);

            //存储用户新增权限
            List<Integer> addList = new ArrayList<>();
            //存储用户删除权限
            List<Integer> deleteList = new ArrayList<>();
            //存储用户编辑权限
            List<Integer> editList = new ArrayList<>();
            //存储用户查询权限
            List<Integer> getList = new ArrayList<>();

            for (UseOrg useOrg : useOrgList) {
                if (useOrg.getMethodId() == Constant.METHOD_ADD) {
                    addList.add(useOrg.getOrgId());
                } else if (useOrg.getMethodId() == Constant.METHOD_DELETE) {
                    deleteList.add(useOrg.getOrgId());
                } else if (useOrg.getMethodId() == Constant.METHOD_UPDATE) {
                    editList.add(useOrg.getOrgId());
                } else if (useOrg.getMethodId() == Constant.METHOD_GET) {
                    getList.add(useOrg.getOrgId());
                }
            }

            //把当前用户的权限放入session中
            session.setAttribute("savePerm", addList);
            session.setAttribute("deletePerm", deleteList);
            session.setAttribute("editPerm", editList);
            session.setAttribute("selectPerm", getList);

        return new SimpleAuthenticationInfo(reUser.getEmail(), Constant.PASSWORD, getName());

    }
}
