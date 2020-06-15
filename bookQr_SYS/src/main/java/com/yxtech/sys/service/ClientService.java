package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.sys.dao.ClientAttrMapper;
import com.yxtech.sys.dao.ClientMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.client.ClientBookVO;
import com.yxtech.sys.vo.client.ClientDetailVO;
import com.yxtech.sys.vo.user.UpdateClientInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 客户管理业务逻辑类
 * Created by hesufang on 2015/11/3.
 */
@Service
public class ClientService extends BaseService<Client> {

    private ClientMapper clientMapper;

    @Autowired
    private UserCenterService userCenterService;

    public ClientMapper getClientMapper() {
        return clientMapper;
    }


    @Resource(name = "clientMapper")
    public void setClientMapper(ClientMapper clientMapper) {
        setMapper(clientMapper);
        this.clientMapper = clientMapper;
    }

    @Autowired
    private  ClientBookService clientBookService;
    @Autowired
    private BeanMapper mapper;

    @Autowired
    private  QrUserInfoService qrUserInfoService;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private ClientAttrMapper clientAttrMapper;

    /**
     * 通过email查看客户信息是否存在
     * @param email  邮箱
     * @author hesufang
     * @date 2015.11.5
     * @return
     */
    public Client existClient(String email){
        Example example = new Example(Client.class);
        example.createCriteria().andEqualTo("email",email);
        List<Client>  clients =  this.clientMapper.selectByExample(example);

        if (clients == null || clients.size()<1){
           return null; //信息不存在
        }
        return  clients.get(0); //信息存在返回该客户信息
    }

    /**
     * 通过openId查看客户信息是否存在
     * @param openId  微信唯一标识
     * @author liukailong
     * @date 2016.10.9
     * @return
     */
    public Client existClientByOpenId(String openId){
        Example example = new Example(Client.class);
        example.createCriteria().andEqualTo("openId",openId);
        List<Client>  clients =  this.clientMapper.selectByExample(example);

        if (clients == null || clients.size()<1){
            return null; //信息不存在
        }
        return  clients.get(0); //信息存在返回该客户信息
    }


    /**
     * 获取客户申请信息
     * @param email  客户邮箱
     * @return
     * @throws Exception
     * @author hesufang （zml改增加用户地址）
     * @date 2015.11.6
     */
    public ClientDetailVO getClientDetail(String email,String openId)throws  Exception{

        Client client = null;
         if(!StringUtils.isEmpty(email)){
             client =  existClient(email);
         }else{
             client =  existClientByOpenId(openId);
         }

        if (client == null){
            return null;
          //  throw  new ServiceException("信息不存在");
        }
        ClientDetailVO detailVO = mapper.map(client, ClientDetailVO.class);

        Example example = new Example(ClientBook.class);
        example.createCriteria().andEqualTo("clientId",client.getId());
        List<ClientBook> bookList = clientBookService.selectByExample(example);
        if (bookList != null && bookList.size()>0){

            detailVO.setBooks( mapper.mapList(bookList,ClientBookVO.class));

        }
        return  detailVO;
    }

    /**
     * 更新客户信息
     * @param client
     * @return
     * @autor zml
     */
    public JsonResult updateInfo(Client client) {
        Example example = new Example(Client.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", client.getOpenId());
        List<Client> clientList = clientMapper.selectByExample(example);
        if (clientList.size() == 0){
            clientMapper.insertSelective(client);
        }else {
            Example ex = new Example(Client.class);
            ex.createCriteria().andEqualTo("email",client.getEmail());
            clientMapper.updateByExampleSelective(client, ex);
        }
        //更新智学云教师信息
        userCenterService.addTeacherInfo(client);

        return new JsonResult(true, ConsHint.EDIT_SUCCESS);
    }

    /**
     * @param openId
     * @return
     */
    public JsonResultData clientDetail(String openId) {
        Client client = null;
        Example exampleQr = new Example(QrUserInfo.class);
        Example.Criteria criteriaQr = exampleQr.createCriteria();
        criteriaQr.andEqualTo("openid", openId);
        List<QrUserInfo> qrList = qrUserInfoService.selectByExample(exampleQr);
        if(qrList != null && qrList.size()>0){
            Example example = new Example(Client.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("openId", openId);
            List<Client> clientList = clientMapper.selectByExample(example);
            if(clientList != null && clientList.size()>0){
                client = clientList.get(0);
                return new JsonResultData(true, "微信用户已注册", client);
            }else{
                return new JsonResultData(true, "微信用户未注册", client);
            }
        }else{
            return new JsonResultData(false, "非微信用户", client);
        }
    }

    /**
     * desc: 根据手机号查询信息
     * date: 2019/7/26 0026
     * @author cuihao
     * @param phone
     * @return
     */
    public JsonResult getClientInfo(String phone){
        Example example = new Example(Client.class);
        example.createCriteria().andEqualTo("phone",phone);
        List<Client>  clients =  this.clientMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(clients)){
            return new JsonResultData<>(false,"根据手机号无法查询到客户信息!");
        }
        Client client = clients.get(0);
        return new JsonResultData<>(client);
    }

    /**
     * 更新客户信息
     *
     * @param vo
     * @return
     * @autor zml
     */
    public JsonResult updateClientInfo(UpdateClientInfoVo vo) {
        int qrcodeId = vo.getQrcodeId();
        Client client = new Client();
        client.setId(vo.getId());
        client.setEmail(vo.getEmail());
        client.setSchool(vo.getSchool());
        client.setPhone(vo.getPhone());

        clientMapper.updateByPrimaryKeySelective(client);

        BookQr bookQr = bookQrService.selectByPrimaryKey(qrcodeId);

        Integer clientId = client.getId();
        Integer bookId = bookQr.getBookId();
        Example qrSub = new Example(ClientAttr.class);
        qrSub.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("qrId", qrcodeId).andEqualTo("type", Constant.TWO);
        List<ClientAttr> clientAttSubList = clientAttrMapper.selectByExample(qrSub);
        if (clientAttSubList == null || clientAttSubList.size() == 0) {
            ClientAttr clientAttr = new ClientAttr();
            clientAttr.setClientId(clientId);
            clientAttr.setEmail(client.getEmail());
            clientAttr.setBookId(bookId);
            clientAttr.setQrId(Integer.valueOf(qrcodeId));
            clientAttr.setStatus(2);
            clientAttr.setType(2);
            clientAttr.setCreateTime(new Date());
            clientAttrMapper.insertSelective(clientAttr);
        }

        client = clientMapper.selectByPrimaryKey(vo.getId());

        //添加方法，调/users/change/profile（用户属性修改）
        userCenterService.changeProfile(client.getOpenId(),vo.getEmail(),vo.getSchool());

        return new JsonResult(true, ConsHint.EDIT_SUCCESS);
    }
}
