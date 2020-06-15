package com.yxtech.sys.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yxtech.common.BaseService;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.datapiess.DataPermission;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.MailBookCountMapper;
import com.yxtech.sys.dao.MailMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.Mail;
import com.yxtech.sys.domain.MailBookCount;
import com.yxtech.sys.vo.EmailBookVO;
import com.yxtech.sys.vo.bookQr.MailVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by hesufang on 2015/10/16.
 */

@Service
public class MailService extends BaseService<Mail> {
    @Autowired
    private MailBookCountMapper mailBookCountMapper;

    @Autowired
    private BookMapper bookMapper;

    private MailMapper mailMapper;

    public MailMapper getMailMapper() {
        return mailMapper;
    }


    @Resource(name = "mailMapper")
    public void setMailMapper(MailMapper mailMapper) {
        setMapper(mailMapper);
        this.mailMapper = mailMapper;
    }

    @Autowired
    private BookService bookService;




    /**
     * 根据图书id获取邮箱列表
     * @param bookId  图书id
     * @param pressId  分社id
     * @param pageNum  页码
     * @param pageSize  每页条数
     * @param keyword  关键字
     * @return
     */
    public JsonResultPage getMailsByBookID(int bookId ,int pressId,int pageNum,int pageSize,String keyword){
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        List<Object> onwerGETList = (List) session.getAttribute("selectPerm");
        Map<String, Object> map = new HashMap<>();
        map.put("pressId", pressId);
        map.put("bookId", bookId);
        map.put("keyword", keyword);

        int role = CurrentUser.getUser().getRole();//0：管理员；1：编辑员；2：总编  3:审核员
        if(role == 0){
            onwerGETList=null;
        }
        map.put("onwers",onwerGETList);

        //分页
        PageHelper.startPage(pageNum, pageSize);
        List<MailVo> mails = this.mailMapper.getMailList(map);
        for(MailVo vo:mails){
            int mailId = vo.getId();
            List<EmailBookVO> bookList = new ArrayList<>();
            List<Integer> bookIds = this.mailMapper.getBooksByMailId(mailId);
            if(bookIds!=null &&bookIds.size()!=0){
                for(int id:bookIds){
                    Book book = bookMapper.selectByPrimaryKey(id);
                    if(book!=null){
                        EmailBookVO bookVO = new EmailBookVO(book);
                        bookList.add(bookVO);
                    }
                }
            }
            vo.setBooks(bookList);
        }
        PageInfo page = new PageInfo(mails);
        return new JsonResultPage(page);
    }


    /**
     * 删除邮箱
     * @param mailIds
     */
    @Transactional
    public void deleteMaills(List mailIds,Integer bookId){

        //删除邮箱图书关联表
        Example example1 = new Example(MailBookCount.class);
        example1.createCriteria().andIn("mailId", mailIds);
        this.mailBookCountMapper.deleteByExample(example1);
        if (bookId == 0){
            //删除邮箱
            Example example = new Example(Mail.class);
            example.createCriteria().andIn("id",mailIds);
            this.mailMapper.deleteByExample(example);
        }
    }

    /**
     * 根据图书id 删除邮箱  （删图书时级联删除该图书邮箱）
     * @param bookId   图书id
     */
    @Transactional
    public void deleteMaillsByBookId(Integer  bookId){

        //删除邮箱图书关联表
        Example example1 = new Example(MailBookCount.class);
        example1.createCriteria().andEqualTo("bookId", bookId);
        this.mailBookCountMapper.deleteByExample(example1);
    }

    /**
     * 获取邮箱列表 用来导出表格
     * @param bookId
     * @return
     */
    public List<MailVo> getMailListByBookID(int bookId){

        // 1、查询该人能看到的所有图书
        Example bookExample = new Example(Book.class);
        Example.Criteria criteria = bookExample.createCriteria();
        criteria.andEqualTo("type", 0);
        // 1、查询该人能看到的所有图书

        if (CurrentUser.getUser().getRole() != 0){
            DataPermission.hasPermission(Book.class, Method.QUERY, this, criteria);
        }

        List<Book> books = bookService.selectByExample(bookExample);

        if (books == null || books.size() ==0){
            return new ArrayList<>();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("bookId", bookId);
        map.put("books",books);

        return this.mailMapper.getMailsByBookID(map);

    }



    /**
     * 新增邮箱  记录次数
     * @param bookQr
     * @param email   要发送的邮箱
     * @param type  1.发送邮箱次数    2.下载课件次数
     */
    @Transactional
    public void addMailAndCount(BookQr bookQr, String email, int type){

        //检查该email----bookId  记录是否存在
        MailBookCount mailBookCount =   mailBookCountMapper.selectCountIdByEmaliAndBookId(email,bookQr.getBookId());


        if (mailBookCount!= null){
            //有记录   t_mail_book_count 表中该记录发送次数+1
            if (type == 1){
                mailBookCount.setNum(mailBookCount.getNum()+1);
            }else {
                mailBookCount.setDownNum(mailBookCount.getDownNum()+1);
            }

            Mail mail = new Mail();
            // 添加记录 t_mail   t_mail_book_count
            mail.setTime(new Date());
            Example example = new Example(Mail.class);
            example.createCriteria().andEqualTo("email",email);
            mailMapper.updateByExampleSelective(mail,example);

            mailBookCountMapper.updateByPrimaryKeySelective(mailBookCount);

        }else{
            //无记录
            // 查询有没有邮箱记录 t_mail
            Example example = new Example(Mail.class);
            example.createCriteria().andEqualTo("email", email);
            example.selectProperties("id");
            List<Mail> mails = this.mailMapper.selectByExample(example);
            Mail mail = new Mail();


            if (mails == null || mails.size()==0){
                // 添加记录 t_mail   t_mail_book_count
                mail.setEmail(email);
                mail.setTime(new Date());
                mailMapper.insertSelective(mail);
            }else {
                mail = mails.get(0);
                // 修改发送时间
                mail.setTime(new Date());
                Example exampleUpdate = new Example(Mail.class);
                exampleUpdate.createCriteria().andEqualTo("email",email);
                mailMapper.updateByExampleSelective(mail,exampleUpdate);
            }

            MailBookCount mailBookCount1 = new MailBookCount();
            if (type == 1){
                mailBookCount1.setNum(1);
                mailBookCount1.setDownNum(0);
            }else {
                mailBookCount1.setNum(0);
                mailBookCount1.setDownNum(1);
            }
            mailBookCount1.setBookId(bookQr.getBookId());
            mailBookCount1.setMailId(mail.getId());
            mailBookCountMapper.insert(mailBookCount1);
        }

    }




}
