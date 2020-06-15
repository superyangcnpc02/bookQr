package com.yxtech.common;

import com.yxtech.sys.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.List;

/**
 * Created by lyj on 2015/10/16.
 */
public class CurrentUser {

    public static User getUser() {
        Subject subject = SecurityUtils.getSubject();
        return  (User)subject.getSession().getAttribute("user");
    }
    public static Integer getOrgId() {
        Subject subject = SecurityUtils.getSubject();
        User u= (User)subject.getSession().getAttribute("user");
        return u.getOrgId();
    }

    public static List<Integer> getPermissions() {
        Subject subject = SecurityUtils.getSubject();
        User u= (User)subject.getSession().getAttribute("user");
        return null;
    }

}
