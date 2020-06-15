package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.datapiess.DataPermission;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.BookCatalogMapper;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.PressMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.BookVO;
import com.yxtech.utils.file.PathUtil;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书业务类
 *
 * @author yanfei
 * @since 2015-10-14
 */
@Service
public class PublicationService extends BaseService<Book> {
    public final static Logger log = LoggerFactory.getLogger(PublicationService.class);
    private BookMapper bookMapper;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private MailService mailService;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private ResService resService;
    @Autowired
    private AuditorMailService auditorMailService;
    @Autowired
    private BookCatalogMapper bookCatalogMapper;

    @Autowired
    private PressMapper pressMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Resource(name = "bookMapper")
    public void setBookMapper(BookMapper bookMapper) {
        setMapper(bookMapper);
        this.bookMapper = bookMapper;
    }

    /**
     * 审核邮箱
     */
    public static final String EMAIL = "email";
    /**
     * 课件下载提示信息
     */
    public static final String TIP = "tip";

    /**
     * 新建图书
     *
     * @param bookVO
     * @param request
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @Transactional
    public int saveBook(BookVO bookVO, HttpServletRequest request) throws Exception {
        //新增
        bookVO.setQuoteid(0);
        BookCatalog bookCatalog = bookCatalogMapper.selectByPrimaryKey(bookVO.getCategoryId());
        if (bookCatalog != null){
            bookVO.setCategory(bookCatalog.getName());
        }
        insertSelective(bookVO);
        this.bookQrService.creatBookPath(bookVO.getId(), request);

        AuditorMail mail = new AuditorMail();
        mail.setBookId(bookVO.getId());
        mail.setAuditorEmail(bookVO.getEmail());
        mail.setTip(bookVO.getTip());

        auditorMailService.insertSelective(mail);
        return   bookVO.getId();
    }


    /**
     * 获得图书列表
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @author yanfei
     * @date 2015.10.17
     */
    public JsonResultPage getBookList(String keyword, int page, int pageSize, Integer userId, Integer orderTime ,String isbns,Integer pressId, Integer parentCategoryId, Integer categoryId) {
        List<BookVO> bookVOList = new ArrayList<>();
        Page<BookVO> bookVOPage = null;
        RowBounds rowBounds = null;
        JsonResultPage bookPage = null;
        // 这里重新写，是因为拼接查询条件限制
        Example bookExample = new Example(Book.class);
        Example.Criteria criteria = bookExample.createCriteria();
        Example.Criteria criteria2 = bookExample.createCriteria();
        Example.Criteria criteria3 = bookExample.createCriteria();
        // 1、模糊查询图书
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andLike("name", "%" + keyword + "%");
            criteria2.andLike("author", "%" + keyword + "%");
            criteria3.andLike("editor", "%" + keyword + "%");
            bookExample.or(criteria2);
            bookExample.or(criteria3);
        }
        // 非正式图书
        criteria.andEqualTo("formal", Constant.ONE);
        criteria2.andEqualTo("formal", Constant.ONE);
        criteria3.andEqualTo("formal", Constant.ONE);

        if(userId==0){
            if(CurrentUser.getUser().getRole() != 0){
                DataPermission.hasPermissionForOr(Book.class, Method.QUERY, this, criteria, criteria2, criteria3);
            }
        }else{
            criteria.andEqualTo("onwer", userId);
            criteria2.andEqualTo("onwer", userId);
            criteria3.andEqualTo("onwer", userId);
        }

        if(isbns!=null &&!"".equals(isbns)){
            criteria.andLike("isbn", "%" + isbns + "%");
            criteria2.andLike("isbn", "%" + isbns + "%");
            criteria3.andLike("isbn", "%" + isbns + "%");
        }

        if(pressId!=0){
            criteria.andEqualTo("pressId", pressId);
            criteria2.andEqualTo("pressId", pressId);
            criteria3.andEqualTo("pressId", pressId);
        }
        if(parentCategoryId!=0){
            criteria.andEqualTo("categorySuperId", parentCategoryId);
            criteria2.andEqualTo("categorySuperId", parentCategoryId);
            criteria3.andEqualTo("categorySuperId", parentCategoryId);
        }
        if(categoryId!=0){
            criteria.andEqualTo("categoryId", categoryId);
            criteria2.andEqualTo("categoryId", categoryId);
            criteria3.andEqualTo("categoryId", categoryId);
        }

        // 	orderTime值为0时按时间倒序排序，值为1时按时间正序排序
        if(orderTime == 0){
            bookExample.setOrderByClause("id DESC");
        }else {
            bookExample.setOrderByClause("id");
        }
        rowBounds = new RowBounds(page, pageSize);
        bookPage = selectByExampleAndRowBounds2JsonResultPage(bookExample, rowBounds);

        if (bookPage != null) {
            List<Book> bookList = (List<Book>) bookPage.getItems();
            if(bookList == null || bookList.size()==0){
                return new JsonResultPage(new Page());
            }

            //转换vo
            BookVO bookVO = null;
            for(Book book : bookList){
                bookVO = new BookVO(book);

                Press press = pressMapper.selectByPrimaryKey(book.getPressId());
                if (press != null){
                    bookVO.setPress(press.getName());
                }

                bookVO.setEditorId(book.getOnwer());
                bookVOList.add(bookVO);
            }
        }

        bookVOPage = new Page<>(bookPage.getTotal(), bookPage.getNum(), bookPage.getSize(), bookVOList);
        return new JsonResultPage(bookVOPage);
    }


    /**
     * 查询图书中哪些有“审核邮箱”和“课件下载提示信息”
     *
     * @return
     * @author yanfei
     * @date 2015.11.3
     */
    public AuditorMail getEmail(Integer bookId) {
        AuditorMail mail = new AuditorMail();
        mail.setId(bookId);
        Example example = new Example(AuditorMail.class);
        example.createCriteria().andEqualTo("bookId",bookId);
        List<AuditorMail> mails =   auditorMailService.selectByExample(example);
       if (mails!= null && mails.size()>0){
          return mails.get(0);
       }
        if (mails == null && bookId !=0){
            Example example1 = new Example(AuditorMail.class);
            example1.createCriteria().andEqualTo("bookId",0);
            List<AuditorMail> mails1 =   auditorMailService.selectByExample(example1);
            if (mails1!= null && mails1.size()>0){
                return mails.get(0);
            }
        }

        return null;
    }


    /**
     * 更新图书
     * @param bookVO
     */
    public void updateBook(BookVO bookVO, HttpServletRequest request){
        Book book = mapper.map(bookVO, Book.class);

        User user = userService.selectByPrimaryKey(bookVO.getEditorId());
        book.setEditor(user.getUserName());
        book.setOnwer(bookVO.getEditorId());
        int bookId = book.getId();
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        if(CurrentUser.getUser().getRole() != 0) {
//            DataPermission.hasPermission(Book.class, Method.UPDATE, this, criteria, bookId);
        }

        Book oldBook = selectByPrimaryKey(book);
        String oldAuthor = oldBook.getAuthor();// 老作者
        String oldBookName = oldBook.getName();// 老图书名称


        updateByPrimaryKeySelective(book);
        // 2、如果已经生成二维码，更新二维码名称
        Example qrExample = new Example(BookQr.class);
        qrExample.createCriteria().andEqualTo("bookId", bookId).andEqualTo("qrType", Constant.ONE);
        List<BookQr> bookQrList = bookQrService.selectByExample(qrExample);
        if (bookQrList != null && bookQrList.size() > 0){
            BookQr bookQr = new BookQr();
            bookQr.setId(bookQrList.get(Constant.ZERO).getId());
            bookQr.setName(book.getName());
            bookQrService.updateByPrimaryKeySelective(bookQr);
        }
        // 3、变更图书名称或作者后，更新生成的文件名称
        String newAuthor = book.getAuthor();// 新作者
        String newBookName = book.getName();// 新图书名称
        if(!(newAuthor.equals(oldAuthor)) || !(newBookName.equals(oldBookName))){
            File newFile = new File(PathUtil.getAppRootPath(request) + bookQrService.QR_ROOT_PATH
                    + File.separator + newBookName + "-" + newAuthor+ "-" + book.getId());
            File oldFile = new File(PathUtil.getAppRootPath(request) + bookQrService.QR_ROOT_PATH
                    + File.separator + oldBookName + "-" + oldAuthor+ "-" + book.getId());
            oldFile.renameTo(newFile);// 重命名
        }


        //找该书下的所有二维码  修改路径。。。。。
        BookQr bookQr = new BookQr();
        bookQr.setBookId(bookId);

        List<BookQr> bookQrs = bookQrService.select(bookQr);
        if (bookQrs == null || bookQrs.size() == 0){
            return;
        }

        fileResService.updateFilePath(request,bookQrs,File.separator + newBookName + "-" + newAuthor+ "-"+book.getId(), File.separator + oldBookName + "-" + oldAuthor+ "-"+book.getId(),newBookName,oldBookName);

        Example example1 = new Example(AuditorMail.class);
        example1.createCriteria().andEqualTo("bookId",bookId);
        List<AuditorMail> mails = auditorMailService.selectByExample(example1);
        AuditorMail mail = new AuditorMail();
        mail.setBookId(bookVO.getId());
        mail.setAuditorEmail(bookVO.getEmail());
        mail.setTip(bookVO.getTip());
        if (mails == null || mails.size() ==0){

            auditorMailService.insert(mail);
        }else {
            mail.setId(mails.get(0).getId());
            auditorMailService.updateByPrimaryKey(mail);
        }

    }

    /**
     * 检测Isbn是否已被创建
     * @param id
     * @param isbn
     * @return
     */
    public boolean hasIsbn(int id,String isbn) {
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        if(id!=0){
            criteria.andNotEqualTo("id",id);
        }
        criteria.andEqualTo("isbn",isbn);
        criteria.andEqualTo("type",0);


        List<Book> bookList = bookMapper.selectByExample(example);

        if(bookList==null || bookList.size()==0){
            return false;
        }else{
            return true;
        }
    }

}
