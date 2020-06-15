package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.BookQrMapper;
import com.yxtech.sys.dao.ClientAttrMapper;
import com.yxtech.sys.dao.ClientMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.Client;
import com.yxtech.sys.domain.ClientAttr;
import com.yxtech.sys.vo.client.ClientVo;
import com.yxtech.sys.vo.clientAttr.ClientAttrVo;
import com.yxtech.utils.mail.MailSender;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zml on 2016/9/10.
 */
@Service
public class SampleService extends BaseService<ClientAttr> {

    @Autowired
    private ClientAttrMapper clientAttrMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookQrMapper bookQrMapper;

    @Resource(name = "clientAttrMapper")
    public void setClientAttrMapper(ClientAttrMapper clientAttrMapper) {
        setMapper(clientAttrMapper);
        this.clientAttrMapper = clientAttrMapper;
    }

    /**
     * 样书申请审核
     * @param clientAttr
     * @return
     * @author zml
     * @date 2016/9/9
     */
    public JsonResult editClientAttr(ClientAttr clientAttr) {
        clientAttrMapper.updateByPrimaryKeySelective(clientAttr);
        ClientAttr clientAttr1 = clientAttrMapper.selectByPrimaryKey(clientAttr.getId());
        // 根据客户ID查询客户详细
        Client client = clientMapper.selectByPrimaryKey(clientAttr1.getClientId());
        // 根据bookId查询bookName
        Book book = bookMapper.selectByPrimaryKey(clientAttr1.getBookId());
        String subject = "文泉云盘移动阅读平台";
        String context = "尊敬的"+client.getName()+",您申请的<<"+book.getName()+">>的样书";
        if(clientAttr.getStatus() == Constant.TWO){
            context = context + "已审核通过,邮寄地址为:" + client.getAddress() + ",请注意查收！";
        }else if(clientAttr.getStatus() == 3){
            context = context + "未通过审核!";
        }
        String[] toMail = new String[]{clientAttr1.getEmail()};
        try {
            MailSender.sendMail(context, subject, toMail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonResult(true, ConsHint.EDIT_SUCCESS);
    }

    /**
     * 样书申请列表
     * @param pageNo
     * @param pageSize
     * @return
     * @author zml
     * @date 2016/9/13
     */
    public JsonResultPage getClientAttrList(int bookId, int status, int pageNo, int pageSize) {
        Example example = new Example(ClientAttr.class);
        example.setOrderByClause("createTime desc");
        Example.Criteria criteria = example.createCriteria();
        if (bookId != 0){
            criteria.andEqualTo("bookId", bookId);
        }
        if (status != 0){
            criteria.andEqualTo("status", status);
        }
        criteria.andEqualTo("type", 2);
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage jsonResultPage = selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<ClientAttr> clientAttrList = jsonResultPage.getItems();
        if (clientAttrList.size() == Constant.ZERO){
            return jsonResultPage;
        }
        List<ClientAttrVo> cavList = new ArrayList<>();
        for (ClientAttr ca : clientAttrList){
            // 根据bookId查询bookName
            Book book = bookMapper.selectByPrimaryKey(ca.getBookId());
            // 根据客户ID查询客户详细
            Client client = clientMapper.selectByPrimaryKey(ca.getClientId());
            ClientAttrVo clientAttrVo = new ClientAttrVo(ca);
            if (book != null){
                clientAttrVo.setBookName(book.getName());
            }
            if (client != null){
                clientAttrVo.setName(client.getName());
                clientAttrVo.setJob(client.getJob());
                clientAttrVo.setMajor(client.getMajor());
                clientAttrVo.setPhone(client.getPhone());
                clientAttrVo.setSchool(client.getSchool());
                clientAttrVo.setCreateTime(ca.getCreateTime());
            }
            cavList.add(clientAttrVo);
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), pageNo, pageSize, cavList));
    }

    /**
     * 样书申请
     * @param clientVo
     * @return
     * @author zml
     * @date 2016/9/13
     */
    public JsonResult addClientAttr(ClientVo clientVo) {
        Example example = new Example(Client.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", clientVo.getOpenId());
        List<Client> clientList = clientMapper.selectByExample(example);
        if (clientList.size() == 0){
            return new JsonResult(false, "样书申请失败");
        }
        Client client = clientList.get(0);

        BookQr bookQr = bookQrMapper.selectByPrimaryKey(clientVo.getQrcodeId());
        if (bookQr == null ||bookQr.getBookId() == null){
            return new JsonResult(true, "申请失败！该样书不存在或已被删除！");
        }

        //添加数据库记录(有就修改，没有新增)
        ClientAttr clientAttr = new ClientAttr();
        clientAttr.setClientId(client.getId());
        clientAttr.setBookId(bookQr.getBookId());
        clientAttr.setType(Constant.TWO);

        List<ClientAttr> attrs = clientAttrMapper.select(clientAttr);
        if(attrs!=null && attrs.size()!=0){
            //置为待审核
            clientAttr.setEmail(clientVo.getEmail());
            clientAttr.setStatus(Constant.ONE);
            clientAttr.setCreateTime(new Date());
            clientAttr.setTarget(client.getObjective());
            Example example_clientAttr = new Example(ClientAttr.class);
            example_clientAttr.createCriteria().andEqualTo("clientId", clientAttr.getClientId()).andEqualTo("bookId", clientAttr.getBookId());
            clientAttrMapper.updateByExampleSelective(clientAttr, example_clientAttr);
        }else{
            clientAttr.setEmail(clientVo.getEmail());
            clientAttr.setStatus(Constant.ONE);
            clientAttr.setCreateTime(new Date());
            clientAttr.setTarget(client.getObjective());
            clientAttrMapper.insertSelective(clientAttr);
        }

        return new JsonResult(true, "样书申请成功");
    }

    /**
     * 删除申请样书
     * @param id
     * @return
     * @author zml
     * @date 2016/9/13
     */
    public JsonResult deleteClientAttr(int id) {
        clientAttrMapper.deleteByPrimaryKey(id);
        return new JsonResult(true, ConsHint.DELETE_SUCCESS);
    }
}
