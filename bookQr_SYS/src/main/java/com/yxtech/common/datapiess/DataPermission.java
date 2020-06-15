package com.yxtech.common.datapiess;

import com.yxtech.common.BaseService;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.utils.i18n.I18n;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * Created by Chenxh on 2015/10/21.
 */


public class DataPermission {
    /**
     * 验证是否拥有数据权限
     *
     * @param clazz       对象
     * @param method      验证的方法  CRUD
     * @param baseService service
     * @param criteria    example条件，如果是query增加数据权限的条件
     * @param ids         验证的数据ID列表
     */
    public static void hasPermission(Class<?> clazz, Method method, BaseService baseService, Example.Criteria criteria, Integer... ids) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();

        List<Object> onwerAddList = (List)session.getAttribute("savePerm");
        List<Object> onwerDeleteList = (List) session.getAttribute("deletePerm");
        List<Object> onwerUpdateList = (List) session.getAttribute("editPerm");
        List<Object> onwerGETList = (List) session.getAttribute("selectPerm");


        switch (method) {
            case UPDATE:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerUpdateList);
                    List<Class<?>> updateList = baseService.selectByExample(example);
                    if (null == updateList || updateList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case DELETE:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerDeleteList);
                    List<Class<?>> deleteList = baseService.selectByExample(example);
                    if (null == deleteList || deleteList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case GET:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerGETList);
                    List<Class<?>> getList = baseService.selectByExample(example);
                    if (null == getList || getList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case QUERY:
                criteria.andIn("onwer", onwerGETList);
                break;

        }
    }

    /**
     * 验证是否拥有数据权限
     *
     * @param clazz       对象
     * @param method      验证的方法  CRUD
     * @param baseService service
     * @param criteria    example条件，如果是query增加数据权限的条件
     * @param criteria2    example的OR条件
     * @param ids         验证的数据ID列表
     */
    public static void hasPermissionForOr(Class<?> clazz, Method method, BaseService baseService, Example.Criteria criteria, Example.Criteria criteria2, Example.Criteria criteria3, Integer... ids) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();

        List<Object> onwerAddList = (List)session.getAttribute("savePerm");
        List<Object> onwerDeleteList = (List) session.getAttribute("deletePerm");
        List<Object> onwerUpdateList = (List) session.getAttribute("editPerm");
        List<Object> onwerGETList = (List) session.getAttribute("selectPerm");


        switch (method) {
            case UPDATE:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerUpdateList);
                    List<Class<?>> updateList = baseService.selectByExample(example);
                    if (null == updateList || updateList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case DELETE:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerDeleteList);
                    List<Class<?>> deleteList = baseService.selectByExample(example);
                    if (null == deleteList || deleteList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case GET:
                for (Integer id : ids) {
                    Example example = new Example(clazz);
                    example.createCriteria().andEqualTo("id", id).andIn("onwer", onwerGETList);
                    List<Class<?>> getList = baseService.selectByExample(example);
                    if (null == getList || getList.size() == 0) {
                        throw new ServiceException(I18n.getMessage("dataperm.error"));
                    }
                }
                break;
            case QUERY:
                criteria.andIn("onwer", onwerGETList);
                criteria2.andIn("onwer", onwerGETList);
                criteria3.andIn("onwer", onwerGETList);
                break;

        }
    }

}

