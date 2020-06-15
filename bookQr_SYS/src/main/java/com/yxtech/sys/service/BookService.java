package com.yxtech.sys.service;

import com.google.common.collect.Maps;
import com.yxtech.common.BaseService;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.datapiess.DataPermission;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.BookReuseVo;
import com.yxtech.sys.vo.BookVO;
import com.yxtech.sys.vo.adv.AdvBookListVo;
import com.yxtech.sys.vo.book.BookMenuVO;
import com.yxtech.sys.vo.book.BookPowerVO;
import com.yxtech.sys.vo.count.BookCountVo;
import com.yxtech.sys.vo.count.BookListVo;
import com.yxtech.sys.vo.count.QrCountVo;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.excel.ImportExcelUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.i18n.I18n;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 图书业务类
 *
 * @author yanfei
 * @since 2015-10-14
 */
@Service
public class BookService extends BaseService<Book> {
    public final static Logger log = LoggerFactory.getLogger(BookService.class);
    @Autowired
    private BookQrMapper bookQrMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private ClientAttrMapper clientAttrMapper;
    @Autowired
    private MailService mailService;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private ResService resService;
    @Autowired
    private AuditorMailService auditorMailService;
    @Autowired
    private BookQrService bookQrService;

    @Autowired
    private PressMapper pressMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;

    @Autowired
    private BookCatalogMapper bookCatalogMapper;

    @Autowired
    private BookRankStatisticMapper bookRankStatisticMapper;

    @Autowired
    private ViewRankStatisticMapper viewRankStatisticMapper;

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
        // 标示“新增记录”
//        bookVO.setType(Constant.ADD_TYPE);
        bookVO.setCreateTime(new Date());
        bookVO.setOnwer(bookVO.getEditorId());
        Integer categoryId=bookVO.getCategoryId();
        BookCatalog bookCatalog=bookCatalogMapper.selectByPrimaryKey(categoryId);
        bookVO.setCategory(bookCatalog.getName());
        /**
         * 同步锁机制，防止多个编辑同时创建一本书
         */
        synchronized(this) {
            Book book = new Book();
            book.setCode(bookVO.getCode());
            int count = this.selectCount(book);
            if(count>1){
                return 0;
            }
            insertSelective(bookVO);
        }

        this.bookQrService.creatBookPath(bookVO.getId(), request);

        //对应修改图书基础信息
        updateBaseBook(bookVO);


        AuditorMail mail = new AuditorMail();
        mail.setBookId(bookVO.getId());
        mail.setAuditorEmail(bookVO.getEmail());
        mail.setTip(bookVO.getTip());

        auditorMailService.insertSelective(mail);
        return   bookVO.getId();
    }


    private  void  updateBaseBook(BookVO bookVO){
        Book baseBook = selectByPrimaryKey(bookVO.getQuoteid());
        baseBook.setOnwer(bookVO.getEditorId());
        baseBook.setAuthor(bookVO.getAuthor());
        baseBook.setCode(bookVO.getCode());
        baseBook.setCategory(bookVO.getCategory());
        baseBook.setCategoryId(bookVO.getCategoryId());
        baseBook.setCategorySuperId(bookVO.getCategorySuperId());
        baseBook.setCover(bookVO.getCover());
        baseBook.setIsbn(bookVO.getIsbn());
        baseBook.setName(bookVO.getName());
        baseBook.setPress(bookVO.getPress());
        baseBook.setPressId(bookVO.getPressId());
        baseBook.setRemark(bookVO.getRemark());
        baseBook.setLogo(bookVO.getLogo());
        baseBook.setWidth(bookVO.getWidth());
        baseBook.setAuditorEmail(bookVO.getAuditorEmail());
        baseBook.setAuditorId(bookVO.getAuditorId());
        baseBook.setBookaskUrl(bookVO.getBookaskUrl());
        baseBook.setStatus(bookVO.getStatus());
        baseBook.setViewNum(bookVO.getViewNum());
        baseBook.setTip(bookVO.getTip());
        baseBook.setEditor(bookVO.getEditor());
        baseBook.setQuoteid(2);

        updateByPrimaryKeySelective(baseBook);
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
    public JsonResultPage getBookList(String keyword, int create, int page, int pageSize, int dataType, Integer userId, Integer orderTime ,String isbns,Integer pressId,Integer parentCategoryId,Integer categoryId) {
        List<BookVO> bookVOList = new ArrayList<>();
        Page<BookVO> bookVOPage = null;
        RowBounds rowBounds = null;
        JsonResultPage bookPage = null;

        // 权限验证
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

        //正式图书
        criteria.andEqualTo("formal", Constant.ZERO);
        criteria2.andEqualTo("formal", Constant.ZERO);
        criteria3.andEqualTo("formal", Constant.ZERO);

        switch (dataType) {
            case Constant.ADD_TYPE:
                // 新增数据类型
                criteria.andEqualTo("type", Constant.ADD_TYPE);
                criteria2.andEqualTo("type", Constant.ADD_TYPE);
                criteria3.andEqualTo("type", Constant.ADD_TYPE);
                break;
            default:
                // 导入数据类型
                criteria.andEqualTo("type", Constant.IMPORT_TYPE);
                criteria2.andEqualTo("type", Constant.IMPORT_TYPE);
                criteria3.andEqualTo("type", Constant.IMPORT_TYPE);

                if (create == 1){
                    //已经创建
                    criteria.andEqualTo("quoteid",Constant.TWO);
                    criteria2.andEqualTo("quoteid",Constant.TWO);
                    criteria3.andEqualTo("quoteid",Constant.TWO);
                }else if(create == 2) {
                    //尚未创建
                    criteria.andEqualTo("quoteid",Constant.ONE);
                    criteria2.andEqualTo("quoteid",Constant.ONE);
                    criteria3.andEqualTo("quoteid",Constant.ONE);
                }
        }

        if(userId==0){
            /*if(CurrentUser.getUser().getRole() != 0){
                DataPermission.hasPermissionForOr(Book.class, Method.QUERY, this, criteria, criteria2, criteria3);
            }*/
        }else{

            if(CurrentUser.getUser().getRole() == 4){
                // 营销人员 查询该营销人员所在的分社，查询分社下的所有已创建的图书
                pressId = CurrentUser.getUser().getPressId();
            }else {
                criteria.andEqualTo("onwer", userId);
                criteria2.andEqualTo("onwer", userId);
                criteria3.andEqualTo("onwer", userId);
            }
        }

        //验证isbns
        if(isbns!=null &&!"".equals(isbns)){

            String[] codes = isbns.split(",");
            List<Object> codeList = new ArrayList<>();
            for (String code : codes) {
                if (!StringUtils.isEmpty(code)){
                    codeList.add(code);
                }
            }
            if (codeList.size() == 1){
                criteria.andLike("code", "%" + codeList.get(0) + "%");
                criteria2.andLike("code", "%" + codeList.get(0) + "%");
                criteria3.andLike("code", "%" + codeList.get(0) + "%");
            }else if (codeList.size() != 0){
                criteria.andIn("code", codeList);
                criteria2.andIn("code", codeList);
                criteria3.andIn("code", codeList);
            }

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
//            bookVOList = mapper.mapList(bookList, BookVO.class);
            BookVO bookVO = null;
            for(Book book : bookList){
                bookVO = new BookVO(book);

                Press press = pressMapper.selectByPrimaryKey(book.getPressId());
                if (press != null){
                    bookVO.setPress(press.getName());
                }

                BookCatalog bookCatalog=bookCatalogMapper.selectByPrimaryKey(book.getCategoryId());
                if (bookCatalog!=null){
                    bookVO.setCategory(bookCatalog.getName());
                }

                bookVO.setEditorId(book.getOnwer());

                //如果是导入的图书  需要查看是否被引用过
                if (dataType==1){
                    Example example_add = new Example(Book.class);
                    Example.Criteria criteria_add = example_add.createCriteria();
                    criteria_add.andEqualTo("quoteid",bookVO.getId());
                    List<Book> bookList_add = this.selectByExample(example_add);
                    if(bookList_add!=null && bookList_add.size()!=0) {
                        bookVO.setCreateFlag(true);
                    }else {
                        bookVO.setCreateFlag(false);
                    }
                }
                bookVOList.add(bookVO);
            }
        }

        bookVOPage = new Page<>(bookPage.getTotal(), bookPage.getNum(), bookPage.getSize(), bookVOList);
        return new JsonResultPage(bookVOPage);
    }

    /**
     * 扫描打印图书信息
     */
    public void exportBookList(String keyword,Integer create,int pressId,String code,Integer parentCategoryId,Integer categoryId,HttpServletRequest request,HttpServletResponse response)throws Exception{
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("pressId",pressId);
        map.put("keyword",keyword);
        map.put("create",create);
        map.put("parentCategoryId",parentCategoryId);
        map.put("categoryId",categoryId);
        //验证isbns
        if(code!=null &&!"".equals(code)){
            String[] codes = code.split(",");
            List<Object> codeList = new ArrayList<>();
            for (String c : codes) {
                if (!StringUtils.isEmpty(c)){
                    codeList.add(c);
                }
            }
            if (codeList.size() == 1){
                map.put("code", codeList.get(0));
            }else {
                map.put("codes", codeList);
            }
        } else {
            map.put("code","");
            map.put("codes","");
        }
        List<BookListVo> bookListVos = bookMapper.exportBookList(map);

        Iterator<BookListVo> it = bookListVos.iterator();
        while(it.hasNext()){
            BookListVo vo = it.next();
            if(("3").equals(vo.getQrType())){
                Integer resId = vo.getResId();
                if(resId == null){
                    it.remove();
                }
            }
        }


        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("bookListVos", bookListVos);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + (bookListVos.size() != 0 ? "freemarker/bookListScan.xls" : "freemarker/bookListScanNoData.xls");
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("图书基本信息.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("图书基本信息.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, bookListVos, "bookListVos", Constant.EXCEL_MAX_INDEX);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    /**
     * 删除图书，级联删除邮箱、二维码
     * @param bookId
     * @param request
     * @author yanfei
     * @date 2015.11.3
     */
    @Transactional
    public void deleteBook(int bookId, HttpServletRequest request) throws Exception {
        // 删除数据条件
        Set<String> uuid = new HashSet<String>(); // 所有文件数据
        // 图书封面，默认logo
        Book book = selectByPrimaryKey(bookId);
       if(book.getLogo() != null && book.getLogo().length() > 0 ){
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid", book.getLogo());
            List<FileRes> fileResList = this.fileResService.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0){
                for (FileRes fr : fileResList) {
                    uuid.add(fr.getUuid());
                }
            }

        }
        //更改基础图书basebook中quoteid为1
        if(book.getQuoteid()!=0&&book.getQuoteid()!=1&&book.getQuoteid()!=2){
            Book baseBook = selectByPrimaryKey(book.getQuoteid());
            baseBook.setQuoteid(1);
            updateByPrimaryKeySelective(baseBook);
        }
        /* if(book.getCover() != null && book.getCover().length() > 0 ){
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid", book.getCover());
            List<FileRes> fileResList = this.fileResService.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0){
                for (FileRes fr : fileResList) {
                    uuid.add(fr.getUuid());
                }
            }
        }*/
        // 二维码数据 保留bookQrId
        List<Integer> bookQrId = new ArrayList<>();
        List<Integer> resIdList = new ArrayList<>();
        Example example = new Example(BookQr.class);
        example.selectProperties("id","url");
        example.createCriteria().andEqualTo("bookId", bookId);
        List<BookQr> bookQrList = bookQrService.selectByExample(example);
        if (bookQrList != null && bookQrList.size() > 0) {
            for (BookQr qr : bookQrList) {
                uuid.add(qr.getUrl());
                bookQrId.add(qr.getId());// 保留bookQrId
            }
        }
        List<Object> qrIds = new ArrayList<>();
        if(bookQrId != null && bookQrId.size() > 0){
            for (int i = 0;i<bookQrList.size();i++){
                qrIds.add(bookQrId.get(i));
            }
            Example resExample = new Example(Res.class);
            resExample.selectProperties("id","fileUuid");
            resExample.createCriteria().andIn("qrId", qrIds);
            List<Res> resList = resService.selectByExample(resExample);
            if (resList != null && resList.size() > 0){
                for (Res res : resList) {
                    resIdList.add(res.getId());
                    uuid.add(res.getFileUuid());
                }
            }
        }
        // 删除邮箱表数据
        mailService.deleteMaillsByBookId(bookId);
        for (int i = 0;i<bookQrList.size();i++){
            // 删除Qr表数据
            bookQrService.deleteByPrimaryKey(bookQrList.get(i));
        }
        for (int i = 0;i<resIdList.size();i++){
            // 删除Resource表数据
            resService.deleteByPrimaryKey(resIdList.get(i));
            /**
             * 向出版社删除图书和资源关系
             */
            userCenterService.deleteBookRes(resIdList.get(i));
        }
        //删除举报列表
        if(qrIds.size()>0){
            Example exampleReport = new Example(Report.class);
            Example.Criteria criteria = exampleReport.createCriteria();
            criteria.andIn("qrcodeId",qrIds);
            reportService.deleteByExample(exampleReport);
        }
        // 删除fileRes表数据及关联文件
        fileResService.deleteForDataAndFileBySetUUID(uuid,request);
        // 删除图书文件夹
        File file = new File(PathUtil.getAppRootPath(request) + bookQrService.QR_ROOT_PATH
                + bookQrService.getBookNamePath(bookId));
        if (file.exists()){
            file.delete();
        }
        //删除图书申请信息
        ClientAttr clientAttr = new ClientAttr();
        clientAttr.setBookId(bookId);
        clientAttrMapper.delete(clientAttr);
        // 删除掉图书数据
        deleteByPrimaryKey(bookId);
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
     * 读取信息
     * @param in
     * @return
     * @author yanfei
     * @date 2015.11.3
     */
    public List<Book> readFromExcel(InputStream in) {
        //定义标题-属性的映射
        LinkedHashMap<String, String> fieldMap = Maps.newLinkedHashMap();
        fieldMap.put("产品编号", "code");
        fieldMap.put("ISBN编号", "isbn");
        fieldMap.put("图书名称", "name");
        fieldMap.put("图书分类", "category");
        fieldMap.put("作者", "author");
        fieldMap.put("图书简介", "remark");
        fieldMap.put("编辑", "editor");
        fieldMap.put("分社", "press");
        log.debug("读取Excel文档内信息");
        return ImportExcelUtil.excelToList(in, Book.class, fieldMap);
    }

    /**
     * 批量插入数据
     * @param bookList
     * @return
     * @author yanfei
     * @date 2015.11.9
     */
    public int insertBooks(List<Book> bookList){
        return bookMapper.insertBooks(bookList);
    }

    /**
     * 更新图书
     * @param bookVO
     */
    public void updateBook(BookVO bookVO, HttpServletRequest request){
        BookCatalog bookCatalog = bookCatalogMapper.selectByPrimaryKey(bookVO.getCategoryId());
        bookVO.setCategory(bookCatalog.getName());
        Book book = mapper.map(bookVO, Book.class);
        User user = userService.selectByPrimaryKey(bookVO.getEditorId());
        book.setEditor(user.getUserName());
        book.setOnwer(bookVO.getEditorId());
        int bookId = book.getId();
        if(CurrentUser.getUser().getRole() != 0) {
//            DataPermission.hasPermission(Book.class, Method.UPDATE, this, criteria, bookId);
        }

        Book oldBook = selectByPrimaryKey(book);
        String oldAuthor = oldBook.getAuthor();// 老作者
        String oldBookName = oldBook.getName();// 老图书名称

        // 1、更新新增信息
        if(bookVO.getType() == 1 ){
            //如果类型是导入图书,他如果被引用，就去修改对应的新增书籍
            Example example_add = new Example(Book.class);
            Example.Criteria criteria_add = example_add.createCriteria();
            criteria_add.andEqualTo("quoteid",bookVO.getId());
            List<Book> bookList_add = this.selectByExample(example_add);
            if(bookList_add!=null && bookList_add.size()!=0){
                Book book_add = bookList_add.get(0);
                //修改isbn,作者,简介
                book_add.setLogo(bookVO.getLogo());
                book_add.setCover(bookVO.getCover());
                book_add.setIsbn(bookVO.getIsbn());
                book_add.setAuthor(bookVO.getAuthor());
                book_add.setRemark(bookVO.getRemark());
                book_add.setCategoryId(bookVO.getCategoryId());
                book_add.setCategory(bookCatalog.getName());
                book_add.setCategorySuperId(bookVO.getCategorySuperId());
                updateByPrimaryKeySelective(book_add);
            }
        }else {
            //2.更新图书基础信息
            updateBaseBook(bookVO);
        }
        updateByPrimaryKeySelective(book);
        // 2、如果已经生成二维码，更新二维码名称
//        Example qrExample = new Example(BookQr.class);
//        qrExample.createCriteria().andEqualTo("bookId", bookId).andEqualTo("qrType", Constant.ONE);
//        List<BookQr> bookQrList = bookQrService.selectByExample(qrExample);
//        if (bookQrList != null && bookQrList.size() > 0){
//            BookQr bookQr = new BookQr();
//            bookQr.setId(bookQrList.get(Constant.ZERO).getId());
//            bookQr.setName(book.getName());
//            bookQrService.updateByPrimaryKeySelective(bookQr);
//        }
        // 3、变更图书名称或作者后，更新生成的文件名称\
        /*
        String newAuthor = book.getAuthor();// 新作者
        String newBookName = book.getName();// 新图书名称
        if(!(newAuthor.equals(oldAuthor)) || !(newBookName.equals(oldBookName))){
            File newFile = new File(PathUtil.getAppRootPath(request) + bookQrService.QR_ROOT_PATH
                    + File.separator + newBookName + "-" + newAuthor+ "-" + book.getId());
            File oldFile = new File(PathUtil.getAppRootPath(request) + bookQrService.QR_ROOT_PATH
                    + File.separator + oldBookName + "-" + oldAuthor+ "-" + book.getId());
            oldFile.renameTo(newFile);// 重命名
            //删除pdf文件
            Arrays.stream(newFile.listFiles()).filter(f ->f.getName().contains(".pdf") ).forEach(f->f.delete());
        }
        //找该书下的所有二维码  修改路径。。。。。
        BookQr bookQr = new BookQr();
        bookQr.setBookId(bookId);
        List<BookQr> bookQrs = bookQrService.select(bookQr);
        if (bookQrs == null || bookQrs.size() == 0){
            return;
        }
        fileResService.updateFilePath(request,bookQrs,File.separator + newBookName + "-" + newAuthor+ "-"+book.getId(), File.separator + oldBookName + "-" + oldAuthor+ "-"+book.getId(),newBookName,oldBookName);

        */
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
     * 审核设置
     * @param email
     * @param tip
     * @author yanfei
     * @date 2015.11.16
     */
    @Transactional
    public void editEmail(String email, String tip,Integer bookId){

        AuditorMail auditorMail = new AuditorMail();
        auditorMail.setBookId(bookId);
        int count = auditorMailService.selectCount(auditorMail);
        if (count > Constant.ZERO){
            // 更新
            auditorMail = auditorMailService.selectOne(auditorMail);
            auditorMail.setAuditorEmail(email);
            auditorMail.setTip(tip);
            auditorMailService.updateByPrimaryKeySelective(auditorMail);
        }else{
            // 新增
            auditorMail.setAuditorEmail(email);
            auditorMail.setTip(tip);
            auditorMailService.insertSelective(auditorMail);
        }
    }


    /**
     * 获得图书预览排行
     *
     * @param flag 1正式 2非正式
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @author zml
     * @date 2016.9.10
     */

    public JsonResultPage viewRank(String beginTime, String endTime, Integer flag, String keyword, int pageNo, int pageSize, int bookId, int pressId,int queryType) throws Exception{
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("formal", formal);
        map.put("keyword", keyword);
        map.put("pageNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        map.put("bookId",bookId);
        map.put("pressId",pressId);

        if(queryType != 0){
            List<BookCountVo> bookCountVoList = viewRankStatisticMapper.queryByPageViewRankStatistic(map);
            for (BookCountVo bookCountVo : bookCountVoList) {
                bookCountVo.setItems(bookMapper.getQrViewRank(bookCountVo.getBookId(), beginTime, endTime));
            }
            int total = viewRankStatisticMapper.queryViewRankStatisticCount(map);
            return new JsonResultPage(new Page(total,pageNo,pageSize,bookCountVoList));
        }else {
            List<BookCountVo> bookCountVoList = bookMapper.selectViewRank(map);
            for (BookCountVo bookCountVo : bookCountVoList) {
                bookCountVo.setItems(bookMapper.getQrViewRank(bookCountVo.getBookId(), beginTime, endTime));
            }
            int total = bookMapper.countViewRank(map);
            return new JsonResultPage(new Page(total,pageNo,pageSize,bookCountVoList));
        }

    }

    /**
     * 导出预览统计
     *
     * @param pressId
     * @param request
     * @param response
     * @autor zml
     */

    public void exportViewRank(String beginTime, String endTime, Integer flag, String keyword, int bookId, int pressId, HttpServletRequest request, HttpServletResponse response) throws Exception{
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("formal", formal);
        map.put("keyword", keyword);
        map.put("bookId",bookId);
        map.put("pressId",pressId);
        List<BookCountVo> bookVOList = bookMapper.exportViewRank(map);
        for (BookCountVo bookCountVo : bookVOList) {
            bookCountVo.setFlag(bookCountVo.getFormal() == 0 ? "正式出版物" : "非正式出版物");
            List<QrCountVo> qrCountVoList = bookMapper.getQrViewRank(bookCountVo.getBookId(), beginTime, endTime);
            bookCountVo.setItems(qrCountVoList);
            for (int i = 0; i < qrCountVoList.size(); i++) {
                if (i == 0) {
                    bookCountVo.setItemsStr(qrCountVoList.get(i).toString());
                }else {
                    bookCountVo.setItemsStr(bookCountVo.getItemsStr() + "\n" + qrCountVoList.get(i).toString());
                }

            }
        }
        //列表为空
        if (bookVOList == null || bookVOList.size() == 0) {
            throw new ServiceException("无数据");
        }
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("bookVOList", bookVOList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/bookViewTemplate.xls";
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("预览统计.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("预览统计.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, bookVOList, "bookVOList", Constant.EXCEL_MAX_INDEX);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 扫描排行
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @author liukailong
     * @date 2016.9.10
     */

    public JsonResultPage bookRank(String beginTime, String endTime, Integer flag, int pageNo, int pageSize, int pressId,int queryType) throws Exception {
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;

        int offset = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("formal", formal);
        map.put("offset",offset);
        map.put("pageSize",pageSize);
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("pressId",pressId);

        if(queryType != 0){
            List<BookCountVo> list = bookRankStatisticMapper.queryByPageBookRankStatistic(map);
            int total = bookRankStatisticMapper.queryBookRankStatisticCount(map);

            Page page = new Page(total,pageNo,pageSize,list);
            return new JsonResultPage(page);
        }else {
            List<BookCountVo> list = bookMapper.selectScanBooks(map);
            int total = bookMapper.countScanBooks(map);

            Page page = new Page(total,pageNo,pageSize,list);
            return new JsonResultPage(page);
        }
    }

    /**
     * 导出扫描排行
     *
     * @param pressId
     * @param request
     * @param response
     * @return
     * @author liukailong
     * @date 2016.9.10
     */

    public void exportBookRank(String beginTime, String endTime, int pressId, Integer flag,HttpServletRequest request, HttpServletResponse response) throws Exception {
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("pressId",pressId);
        map.put("formal",formal);

        List<BookCountVo> list = bookMapper.exportBookRank(map);

        if(list==null || list.size()==0)
            return ;

        List<BookCountVo> bookVOList = new ArrayList<>();
        for (BookCountVo countVo : list) {
            Book book = bookMapper.selectByPrimaryKey(countVo.getBookId());
            BookCountVo vo = new BookCountVo();
            vo.setBookId(book.getId());
            vo.setBookName(book.getName());
            vo.setViewNum(countVo.getViewNum());
            vo.setPressId(book.getPressId());
            vo.setPressName(book.getPress());
            vo.setEditor(book.getEditor());
            vo.setCode(book.getCode());
            vo.setEditorId(book.getOnwer());
            bookVOList.add(vo);
        }

        //列表为空
        if (bookVOList == null || bookVOList.size() == 0) {
            throw new ServiceException("无数据");
        }
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("bookVOList", bookVOList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/bookScanTemplate.xls";
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("扫描统计.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("扫描统计.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, bookVOList, "bookVOList", Constant.EXCEL_MAX_INDEX);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * 拿到已被引用的的导入图书信息
     * @return
     */
    public ArrayList<Object> getQuoteIds(){
        ArrayList<Object> list = new ArrayList<>();

        Example example = new Example(Book.class);
        example.selectProperties("quoteid");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",0);
        List<Book> bookList = this.selectByExample(example);
        if(bookList == null || bookList.size()==0){
            return null;
        }

        for(Book book:bookList){
            list.add(book.getQuoteid());
        }

        return list;
    }

    /**
     * // 检测传过来的code与引用的code是否一致
     * @param quoteId
     * @param code
     * @return
     */
    public boolean existsIsbn(Integer quoteId, String code) {
        Book book = bookMapper.selectByPrimaryKey(quoteId);
        if (!StringUtils.isEmpty(book)){
            if (String.valueOf(book.getCode()).equals(code)){
                return true;
            }
        }
        return false;
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

    /**
     * 授权
     * @param editorId
     * @param ids
     * @return
     */
    public JsonResult bookRight(int editorId, String ids, int pressId) {
        // 根据编辑 id 查询编辑的名字
        String editorName = userService.getUserNameByUserId(editorId);
        String[] idd = ids.split(",");
        for (String id : idd) {
            if (!StringUtils.isEmpty(id)){
                Book book = bookMapper.selectByPrimaryKey(Integer.valueOf(id));
                book.setPressId(pressId);
                book.setPress(pressMapper.selectByPrimaryKey(pressId).getName());
                book.setOnwer(editorId);
                book.setEditor(editorName);
                bookMapper.updateByPrimaryKeySelective(book);

                int type = book.getType();//0 新增;1 导入
                if(type == 0){
                    if(book.getFormal()==0){
                        //正式出版物,去修改其基础图书
                        Book subBook = bookMapper.selectByPrimaryKey(book.getQuoteid());
                        subBook.setPressId(pressId);
                        subBook.setPress(pressMapper.selectByPrimaryKey(pressId).getName());
                        subBook.setOnwer(editorId);
                        subBook.setEditor(editorName);
                        bookMapper.updateByPrimaryKeySelective(subBook);
                    }
                }else if(type == 1){
                    Example example = new Example(Book.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("quoteid",book.getId());
                    List<Book> bookList = bookMapper.selectByExample(example);
                    if(bookList != null && bookList.size()!=0){
                        Book subBook = bookList.get(0);
                        subBook.setPressId(pressId);
                        subBook.setPress(pressMapper.selectByPrimaryKey(pressId).getName());
                        subBook.setOnwer(editorId);
                        subBook.setEditor(editorName);
                        bookMapper.updateByPrimaryKeySelective(subBook);
                    }

                }
            }
        }
        return new JsonResult(true, "操作成功！");
    }

    /**
     * 出版部图书列表
     * @param editor        编辑 ID
     * @param isbns         字符串，分隔，模糊查询
     * @param keyword       书名，编辑，作者搜索
     * @param pressId       分社 ID
     * @param type          1 正式图书   2 非正式图书
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getPowerBookList(Integer editor, String isbns, String keyword, Integer pressId, int type,int categoryId,int categorySuperId, int pageNo, int pageSize) {
        // 查询 Book 表
        Example example = new Example(Book.class);
        example.setOrderByClause("id DESC");
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria criteria2 = example.createCriteria();
        Example.Criteria criteria3 = example.createCriteria();
        // 1、模糊查询图书
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andLike("name", "%" + keyword + "%");
            criteria2.andLike("author", "%" + keyword + "%");
            criteria3.andLike("editor", "%" + keyword + "%");
            example.or(criteria2);
            example.or(criteria3);
        }
        //验证isbns
        if(isbns!=null &&!"".equals(isbns)){
            String[] codes = isbns.split(",");
            List<Object> codeList = new ArrayList<>();
            for (String code : codes) {
                if (!StringUtils.isEmpty(code)){
                    codeList.add(code);
                }
            }
            if (codeList.size() == 1){
                criteria.andLike("code", "%" + codeList.get(0) + "%");
                criteria2.andLike("code", "%" + codeList.get(0) + "%");
                criteria3.andLike("code", "%" + codeList.get(0) + "%");
            }else if(codeList.size()!=0){
                criteria.andIn("code", codeList);
                criteria2.andIn("code", codeList);
                criteria3.andIn("code", codeList);
            }
        }
        if(pressId != 0){
            criteria.andEqualTo("pressId", pressId);
            criteria2.andEqualTo("pressId", pressId);
            criteria3.andEqualTo("pressId", pressId);
        }
        if (editor != 0){
            criteria.andEqualTo("onwer", editor);
            criteria2.andEqualTo("onwer", editor);
            criteria3.andEqualTo("onwer", editor);
        }
        criteria.andEqualTo("formal", type);
        criteria2.andEqualTo("formal", type);
        criteria3.andEqualTo("formal", type);
        criteria.andEqualTo("isPublication", 2);
        criteria2.andEqualTo("isPublication", 2);
        criteria3.andEqualTo("isPublication", 2);
        criteria.andEqualTo("type", 0);
        criteria2.andEqualTo("type", 0);
        criteria3.andEqualTo("type", 0);
        if (categoryId!=0) {
            criteria.andEqualTo("categoryId", categoryId);
            criteria2.andEqualTo("categoryId", categoryId);
            criteria3.andEqualTo("categoryId", categoryId);
        }
        if (categorySuperId!=0){
            criteria.andEqualTo("categorySuperId",categorySuperId);
            criteria2.andEqualTo("categorySuperId",categorySuperId);
            criteria3.andEqualTo("categorySuperId",categorySuperId);
        }
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage jsonResultPage = this.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<Book> bookList = jsonResultPage.getItems();
        if (bookList.size() == 0) {
            return jsonResultPage;
        }
        List<BookPowerVO> resultVoList = new ArrayList<>();
        for (Book book : bookList) {
            resultVoList.add(new BookPowerVO(book));
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), pageNo, pageSize, resultVoList));
    }

    /**
     * 图书下拉列表 仅保留图书下有资源的图书
     * @param pressId
     * @param userId
     * @return
     */
    public JsonResultList getNewExamineDownList(Integer pressId, String userId) {
        // 查询该分社下的图书
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 0);
        if(pressId != 0){
            criteria.andEqualTo("pressId",pressId);
        }
        if(userId != null && !"".equals(userId)){//传空拿全部
            criteria.andEqualTo("auditorId",userId);
        }
        List<Book> bookList = new ArrayList<>();
        List<Book> bkList = bookMapper.selectByExample(example) ;
        // 查询该图书下的二维码 是否存在 不存在直接去掉该书
        for (int i = 0; i < bkList.size(); i++) {
            if (!bookQrService.getBookQrResIsEmpty(bkList.get(i).getId())) {
                bookList.add(bkList.get(i));
            }
        }
        return new JsonResultList(mapper.mapList(bookList, BookMenuVO.class));
    }

    /**
     * 图书设置仅限制微信扫描
     * @param flag 开关 1：是 2否
     * @param bookId 图书id
     * @return
     */
    public JsonResult setWetChat( Integer flag, Integer bookId ){
        Book book = new Book();
        book.setId(bookId);
        book.setOnlyWebchat(flag);

        bookMapper.updateByPrimaryKeySelective(book);

        return new JsonResult(true,"设置成功!");
}

    /**
     * 图书复用校验code
     * @param code 新的图书code
     * @return
     * @author cuihao
     * @date 2017.10.21
     */
    public List<Book> validateCode(String code) throws Exception{
        String[] arr_code = code.split("-");

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",1);
        criteria.andEqualTo("quoteid",Constant.ONE);
        criteria.andEqualTo("formal",0);
        criteria.andLike("code", arr_code[0] + "%");
        criteria.andNotEqualTo("code",code);
        List<Book> bookList = bookMapper.selectByExample(example);

        return bookList;
    }

    /**
     * 由三位code字符串查询未创建的基础图书
     * @param code
     * @return
     */
    public List<Book> getUnCreatedBookByCode(String code){
        ArrayList<Object> arrayList = this.getQuoteIds();

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",Constant.ONE);
        criteria.andLike("code",  code + "%");
        if(arrayList!=null && arrayList.size()!=0){
            criteria.andNotIn("id", arrayList);
        }

        // 如果是编辑员  只能查到分给自己的；
        if(CurrentUser.getUser().getRole() == 1){
            criteria.andEqualTo("onwer",CurrentUser.getUser().getId());
        }

        example.setOrderByClause("code");
        List<Book> bookList = this.selectByExample(example);

        return bookList;
    }

    /**
     * 图书复用
     * @param baseBookId 新建图书引用的基础图书id(复制图书下面资源信息)
     * @param bookId 模板图书id(复制其图书自身信息)
     * @return
     * @author cuihao
     * @date 2017.10.21
     */
    @Transactional
    public JsonResult reuseBook(Integer baseBookId,Integer bookId,HttpServletRequest request){
        Integer newBookId = 0;
        Integer newQuoteId = baseBookId;
        try {
            Book baseBook = bookMapper.selectByPrimaryKey(baseBookId); //基础图书
            Book createdBook = (Book) baseBook.Clone();//创建图书
            Book modelBook = bookMapper.selectByPrimaryKey(bookId); //模板图书
            if(baseBook == null || modelBook ==null)
                return new JsonResult(false, "数据错误!");
            //createdBook
            createdBook.setId(null);
            createdBook.setCreateTime(new Date());
            createdBook.setType(0);
            createdBook.setQuoteid(baseBookId);
            createdBook.setOnlyWebchat(modelBook.getOnlyWebchat());
            bookMapper.insertSelective(createdBook);
            newBookId = createdBook.getId();

            //修改baseBook的quoteid为2,代表已创建
            baseBook.setQuoteid(2);
            bookMapper.updateByPrimaryKeySelective(baseBook);
            //AuditorMail
            AuditorMail auditorMail =  this.getEmail(bookId);
            AuditorMail mail = new AuditorMail();
            mail.setBookId(newBookId);
            mail.setAuditorEmail(auditorMail.getAuditorEmail());
            mail.setTip(auditorMail.getTip());
            auditorMailService.insertSelective(mail);

            //生成权限二维码(只在t_qr_auth表中插入一条记录,不需要生成二维码图片)
            BookQrAuth bookQrAuth = new BookQrAuth();
            bookQrAuth.setBookId(newBookId);
            bookQrAuth.setName(createdBook.getName());
            bookQrAuth.setOnwer(createdBook.getOnwer());
            bookQrAuthMapper.insertSelective(bookQrAuth);
            //BookQr,Res,FileRes
            BookQr bookQrEP = new BookQr();
            bookQrEP.setBookId(bookId);
            List<BookQr> bookQrList = bookQrService.select(bookQrEP);
            for(BookQr bookQr:bookQrList){
                int bookQrId = bookQr.getId();
                //复用BookQr
                Map<String, Object> map = bookQrService.reuseBookQR(newBookId,bookQr, request);
                int  newbookQrId = (Integer) map.get("id");

                Res resEP = new Res();
                resEP.setQrId(bookQrId);
                List<Res> resList = resService.select(resEP);
                for(Res res:resList){
                    String fileUuid = res.getFileUuid();
                    String newFileUuid = UUID.randomUUID().toString();
                    //新增Res
                    res.setId(null);
                    res.setQrId(newbookQrId);
                    res.setFileUuid(newFileUuid);
                    resService.insertSelective(res);
                    //新增FileRes
                    FileRes fileResEP = new FileRes();
                    fileResEP.setUuid(fileUuid);
                    List<FileRes> fileResList = fileResService.select(fileResEP);
                    if(fileResList!=null &&fileResList.size()!=0){
                        FileRes fileRes = fileResList.get(0);
                        fileRes.setId(null);
                        fileRes.setUuid(newFileUuid);
                        fileResService.insertSelective(fileRes);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        return new JsonResultData(true, I18n.getMessage("操作成功!"),new BookReuseVo(newBookId,newQuoteId));
    }

    // 同步图书数据

    @Autowired
    private PressService pressService;

    /**
     * 图书同步数据
     * @param code
     * @return
     * @throws Exception
     */
    public List<Book> getBookListByCode(String code) throws Exception {
        List<Book> bookList = new ArrayList<>();
        if (!StringUtils.isEmpty(code)) {
            Example example = new Example(Book.class);
            example.setOrderByClause("code asc");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("code", code);
            bookList = bookMapper.selectByExample(example);
        }
        return bookList;
    }

    public void updateSyncBookById(Book book) throws Exception {
        // 处理分社
//        pressService.updatePressByName(book);
        // 处理编辑
//        userService.updateUserByEditor(book);
        // 如果更新完之后，必须补全这个数据的 分社ID pressId 还有这个图书的编辑，没有查询到的话都新增数据
        bookMapper.updateByPrimaryKeySelective(book);
    }

    /**
     * 新增数据的话
     * @param book
     */
    public void insertBookBaseInfo(Book book) throws ServiceException{
        // 处理分社
        pressService.updatePressByName(book);
        // 处理编辑
        userService.updateUserByEditor(book);
        // 新增图书，补全分社和编辑信息
        bookMapper.insertSelective(book);
    }

    /**
     * 获取广告位所需要的图书信息
     * @param author
     * @param categoryId
     * @param formal
     * @param keyword
     * @param likeType
     * @param parentCategoryId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getAdvBookList(String author, Integer categoryId, Integer formal, String keyword, Integer likeType, Integer parentCategoryId, Integer pageNo, Integer pageSize) {
        Example example = new Example(Book.class);
        example.setOrderByClause("createTime desc");
        Example.Criteria criteria = example.createCriteria();
        // 0：正式出版物 1：非正式出版物
        criteria.andEqualTo("formal", formal);
        // type 类型 已经创建的图书
        criteria.andEqualTo("type", 0);
        if (!StringUtils.isEmpty(author)) {
            criteria.andLike("author", "%" + author + "%");
        }
        if (categoryId != 0) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (!StringUtils.isEmpty(keyword)) {
            // 包含不包含 1 包含 2 不包含
            if (likeType == 1) {
                criteria.andLike("name", "%" + keyword + "%");
            }else {
                criteria.andNotLike("name", "%" + keyword + "%");
            }
        }
        if (parentCategoryId != 0) {
            criteria.andEqualTo("categorySuperId", parentCategoryId);
        }
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage jsonResultPage = this.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<Book> bookList = jsonResultPage.getItems();
        if (bookList.size() == 0) {
            return jsonResultPage;
        }
        List<AdvBookListVo> advBookListVoList = new ArrayList<>();
        // 转换VO
        for (Book book : bookList) {
            advBookListVoList.add(new AdvBookListVo(book.getAuthor(), book.getCategory(), book.getCategoryId(), book.getCode(), book.getEditor(), book.getOnwer(), book.getFormal(), book.getId(), book.getIsbn(), book.getName(), book.getPress(), book.getPressId(), book.getRemark()));
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), pageNo, pageSize, advBookListVoList));
    }

    /**
     * 获取广告位所需要的图书信息
     * @param author
     * @param categoryId
     * @param formal
     * @param keyword
     * @param likeType
     * @param parentCategoryId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultList bookListAllSelected(String author, Integer categoryId, Integer formal, String keyword, Integer likeType, Integer parentCategoryId, Integer pageNo, Integer pageSize) {
        Example example = new Example(Book.class);
        example.selectProperties("id","name");
        example.setOrderByClause("id desc");
        Example.Criteria criteria = example.createCriteria();
        // 0：正式出版物 1：非正式出版物
        criteria.andEqualTo("formal", formal);
        // type 类型 已经创建的图书
        criteria.andEqualTo("type", 0);
        if (!StringUtils.isEmpty(author)) {
            criteria.andLike("author", "%" + author + "%");
        }
        if (categoryId != 0) {
            criteria.andEqualTo("categoryId", categoryId);
        }
        if (!StringUtils.isEmpty(keyword)) {
            // 包含不包含 1 包含 2 不包含
            if (likeType == 1) {
                criteria.andLike("name", "%" + keyword + "%");
            }else {
                criteria.andNotLike("name", "%" + keyword + "%");
            }
        }
        if (parentCategoryId != 0) {
            criteria.andEqualTo("categorySuperId", parentCategoryId);
        }

        List<Book> bookList = this.selectByExample(example);
        if (bookList.size() == 0) {
            return new JsonResultList(new ArrayList());
        }

        return new JsonResultList(bookList);
    }
}

