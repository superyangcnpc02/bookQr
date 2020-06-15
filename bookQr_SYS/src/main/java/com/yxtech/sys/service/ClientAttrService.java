package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.sys.dao.ClientAttrMapper;
import com.yxtech.sys.domain.ClientAttr;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资源管理业务逻辑类
 * Created by hesufang on 2015/11/3.
 */
@Service
public class ClientAttrService extends BaseService<ClientAttr> {

    private ClientAttrMapper clientAttrMapper;

    public ClientAttrMapper getClientAttrMapper() {
        return clientAttrMapper;
    }


    @Resource(name = "clientAttrMapper")
    public void setClientAttrMapper(ClientAttrMapper clientAttrMapper) {
        setMapper(clientAttrMapper);
        this.clientAttrMapper = clientAttrMapper;
    }


    /**
     * 判断下载密码是否正确存在
     * @param email  邮箱
     * @param bookId  图书
     * @param password  密码
     * @return
     */
    public boolean existPass(String email,Integer bookId,String password)throws Exception{
        Example example = new Example(ClientAttr.class);
        example.createCriteria().andEqualTo("email",email).andEqualTo("bookId",bookId).andEqualTo("password",password);
        List<ClientAttr>  clientAttrList =  this.clientAttrMapper.selectByExample(example);

        if (clientAttrList == null || clientAttrList.size()<1){
            log.warn("email："+email+"----bookId"+bookId+"------password"+password+"密码错误");
            throw new ServiceException("密码错误");
        }
        return  true;
    }



    /**
     * 判断客户 图书对应信息是否存在
     * @param email  邮箱
     * @param bookId  图书
     * @return
     */
    public boolean existEmail(String email,Integer bookId){
        Example example = new Example(ClientAttr.class);
        example.createCriteria().andEqualTo("email",email).andEqualTo("bookId",bookId);
        List<ClientAttr>  clientAttrList =  this.clientAttrMapper.selectByExample(example);

        if (clientAttrList == null || clientAttrList.size()<1){
            return  false; //不存在
        }
        return  true;
    }



}
