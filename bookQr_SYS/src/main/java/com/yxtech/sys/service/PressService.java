package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.PressMapper;
import com.yxtech.sys.dao.UserMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.Press;
import com.yxtech.sys.domain.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liukailong on 2016/9/10.
 */

@Service
public class PressService extends BaseService<Press> {

    @Autowired
    protected PressMapper pressMapper;

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected BookService bookService;

    @Resource(name = "pressMapper")
    public void setPressMapper(PressMapper pressMapper) {
        setMapper(pressMapper);
        this.pressMapper = pressMapper;
    }

    /**
     * 新增出版社
     * @param press
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @Transactional
    public JsonResultData addPress(Press press) {
        Press pressTemp = new Press();
        pressTemp.setName(press.getName());
        int i = this.selectCount(pressTemp);
        if(i>0){
            return new JsonResultData(false,"此分社在数据库中已存在!");
        }

        this.insertSelective(press);
        return new JsonResultData(press,"新增成功!");
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param keyword
     * @return
     * @author liukailong
     * @date 2016-09-10
     */
    @Transactional
    public JsonResultPage getList(int page, int pageSize, String keyword) {
        Example example = new Example(Press.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(keyword)){
            criteria.andLike("name","%" + keyword + "%");
        }
        example.setOrderByClause("id desc");
        RowBounds rowBounds = new RowBounds(page, pageSize);
        JsonResultPage jsonResultPage = selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<Press> resourceList = jsonResultPage.getItems();

        if(resourceList.size()==0){
            return jsonResultPage;
        }
        Page pageReturn = new Page(jsonResultPage.getTotal(), page, pageSize, resourceList);
        return new JsonResultPage(pageReturn);
    }

    /**
     * 修改出版社
     * @param press
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @Transactional
    public JsonResult editPress(Press press) {
        Example example = new Example(Press.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", press.getId());
        updateByExampleSelective(press,example);
        return new JsonResult(true, "更新成功!");
    }

    /**
     * 删除出版社
     * @param id
     * @return
     * @author liukailong
     * @date 2016/09/10
     */
    @Transactional
    public JsonResult deletePress(int id) {
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("pressId", id);
        List<Book> bookList = bookService.selectByExample(example);

        Example example2 = new Example(User.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("pressId", id);
        criteria2.andNotEqualTo("status", 3);
        List<User> userList = userMapper.selectByExample(example2);

        if(userList != null && userList.size()>0){
            return new JsonResult(false, "该分社下有用户,不能删除!");
        }else if(bookList != null && bookList.size()>0){
            return new JsonResult(false, "该分社下有图书,不能删除!");
        }else{
            //删除
            this.deleteByPrimaryKey(id);
            return new JsonResult(true, "删除成功!");
        }
    }

    public void updatePressByName(Book book) throws ServiceException {
        if (book != null) {
            if (!StringUtils.isEmpty(book.getPress())) {
                System.out.println("图书的分社名称: " + book.getPress());
                Integer pressId = pressMapper.getPressIdByName(book.getPress());
                System.out.println("图书的分社ID: " + pressId);
                if (pressId == null || pressId == 0) {
                    // 新增分社信息
                    Press press = new Press();
                    press.setName(book.getPress());
                    press.setCreateId(1);
                    press.setRemark("同步图书时新增了新的分社！");
                    pressMapper.insertSelective(press);
                    pressId = press.getId();
                }
                book.setPressId(pressId);
            }else {
                if (book.getEditor() == null) {
                    throw new ServiceException("同步数据的编辑为空！ code : " + book.getCode());
                }
                if (book.getPressId() == null) {
                    throw new ServiceException("同步数据的分社为空！ code : " + book.getCode());
                }
            }
        }
    }
}
