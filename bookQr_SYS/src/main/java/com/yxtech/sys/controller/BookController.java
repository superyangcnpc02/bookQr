package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ExcelException;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.datapiess.DataPermission;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.BookCatalogMapper;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.dao.UserMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.BookVO;
import com.yxtech.sys.vo.book.BookEmailVO;
import com.yxtech.sys.vo.book.BookMenuVO;
import com.yxtech.sys.vo.book.IsbnBookVO;
import com.yxtech.sys.vo.user.BookLockVo;
import com.yxtech.sys.vo.user.UserMenuVO;
import com.yxtech.utils.file.NameUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.StringUtil;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.runCode.RunCmdUtil;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图书管理控制器
 * @author yanfei
 * @since 2015-10-14
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/book")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private AuditorMailService auditorMailService;
    @Autowired
    private PressService pressService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OperationService operationService;
    @Autowired
    private BookCatalogService bookCatalogService;
    /*
     * 上传类型白名单
     */
    private List<String> fileTypes = Lists.newArrayList(Constant.FILE_MIME_XLS, Constant.FILE_MIME_XLSX);

    //校验文件名不能有特殊字符
    private Pattern pattern = Pattern.compile("^.*[/|\\\\|:|\\*|\\?|\"|<|>].*$");



    /**
     * 验证权限
     * @param method
     * @author yanfei
     * @date 2015.10.23
     */
    private void validateRole(Method method, Integer id){
//        if(CurrentUser.getUser().getRole() != 0){
//            Example example = new Example(Book.class);
//            Example.Criteria criteria = example.createCriteria();
//            if(id != null){
//                DataPermission.hasPermission(Book.class, method, bookService, criteria, id);
//            }else {
//                DataPermission.hasPermission(Book.class, method, bookService, criteria);
//            }
//        }
    }

    /**
     * 新建图书
     * @param bookVO
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    @RequiresPermissions(value = "/book/add")
    public JsonResultId saveBook(@RequestBody BookVO bookVO, HttpServletRequest request) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuthor()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "作者"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getCode()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "产品码"));
//        Assert.isTrue(!StringUtils.isEmpty(bookVO.getIsbn()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "ISBN"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书名"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getPress()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "出版社"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核邮箱"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getTip()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "提示信息"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getQuoteid()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "引用的id"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuditorId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核人ID"));
//        Assert.isTrue(!StringUtils.isEmpty(bookVO.getBookaskUrl()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书问地址"));

        //检测isbn是否已被创建
//        if(!"".equals(bookVO.getIsbn()) && bookVO.getIsbn()!=null){
//            boolean hasIsbn = bookService.hasIsbn(0,bookVO.getIsbn());
//            if(hasIsbn){
//                return new JsonResultId( false, "新增失败,isbn号已经存在！");
//            }
//        }

        // 检测传过来的code与引用的code是否一致
        boolean bool = bookService.existsIsbn(bookVO.getQuoteid(), bookVO.getCode());
        if (!bool) {
            return new JsonResultId( true, "新增失败,请勿修改已选中的产品编号！");
        }

        Book baseBook  = bookMapper.selectByPrimaryKey(bookVO.getQuoteid());
        if (baseBook!=null && !baseBook.getCode().equals(bookVO.getCode())){
            return new JsonResultId(false,"图书产品编号不能修改");
        }

        Matcher matcher = pattern.matcher(bookVO.getName());
        if (matcher.matches()){
            return new JsonResultId(false,"图书名称含有特殊字符?|\\ /*\"<>:");
        }
        // 校验简介字数是否超长
        String remark = bookVO.getRemark();
        if(!StringUtils.isEmpty(remark)){
            Assert.isTrue(remark.length() <= Constant.LENGTH_1000_MAX, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "简介"));
        }
        bookVO.setType(Constant.ZERO);

        int id = bookService.saveBook(bookVO, request);
        if(id>0){
            return new JsonResultId(id,"新增成功!");
        }else{
            return new JsonResultId(id,"该书已经被创建过!");
        }

    }

    /**
     * 图书列表
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @RequiresPermissions(value = "/book/list")
    public JsonResultPage getBookList(@RequestParam(value = "keyword",defaultValue = "",required = false) String keyword,
                                      @RequestParam(value = "create", defaultValue = "0", required = false) int create,
                                      @RequestParam int page,
                                      @RequestParam int pageSize,
                                      @RequestParam int dataType,
                                      @RequestParam(value = "userId", defaultValue = "0", required = false) Integer userId,
                                      @RequestParam(value = "orderTime", defaultValue = "0", required = false) Integer orderTime,
                                      @RequestParam(value = "isbns",required = false) String isbns,
                                      @RequestParam(value = "pressId", defaultValue = "0") Integer pressId,
                                      @RequestParam(value = "parentCategoryId", defaultValue = "0") Integer parentCategoryId,
                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        Assert.notNull(page, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.notNull(pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
//        Assert.notNull(isbns, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "ISBN"));
        return bookService.getBookList(StringUtil.escape4Like(keyword), create, page, pageSize,dataType, userId, orderTime , StringUtil.escape4Like(isbns),pressId,parentCategoryId,categoryId);
    }

    /**
     * 扫描打印图书信息
     *  @author lsn
     *  @param create 1:已创建 , 2:未创建
     * @date 2017.10.27
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportBookList(@RequestParam(value = "keyword",defaultValue = "",required = false) String keyword,
                                @RequestParam(value = "create") Integer create,
                                @RequestParam(value = "pressId") int pressId,
                               @RequestParam(value = "code",required = false)String code,
                               @RequestParam(value = "parentCategoryId",defaultValue = "0",required = false)Integer parentCategoryId,
                               @RequestParam(value = "categoryId",defaultValue = "0",required = false)Integer categoryId,
                                HttpServletRequest request,
                                HttpServletResponse response)throws Exception{
          bookService.exportBookList(keyword,create,pressId,code,parentCategoryId,categoryId,request,response);
    }

    /**
     * 获取用户下拉框
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @RequestMapping(value = "/editorList", method = RequestMethod.GET)
    @RequiresPermissions(value = "/book/editorList")
    public JsonResultList getUserArray(HttpServletRequest request){
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        example.selectProperties("id", "userName");
        criteria.andEqualTo("role", 1);     // 只查询所有编辑员的用户列表
        criteria.andEqualTo("status", 1);     // 只查询编辑员的状态为正常的
        List<User> userList = userService.selectByExample(example);
        if(userList != null && userList.size() > 0){
            return new JsonResultList(true,I18n.getMessage("query.success"), mapper.mapList(userList, UserMenuVO.class));
        }
        return new JsonResultList(false, I18n.getMessage("query.error"));
    }


    /**
     * 删除图书
     * @param id
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @RequiresPermissions(value = "/book/delete")
    @Transactional
    public JsonResult deleteBook(@RequestParam int id, HttpServletRequest request) throws Exception{
        Assert.notNull(id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        //记录操作行为
        List<Integer> list = new ArrayList<>();
        list.add(id);
        operationService.record(list,Constant.BOOK,request);

        // 权限验证
        validateRole(Method.DELETE, id);

        Book book = bookService.selectByPrimaryKey(id);
        if(book == null){
            return new JsonResult(false, "图书不存在!");
        }

        // 删除图书
        bookService.deleteBook(id, request);

        Example ep = new Example(AuditorMail.class);
        ep.createCriteria().andEqualTo("bookId", id);
        auditorMailService.deleteByExample(ep);

        return new JsonResult(true, I18n.getMessage("delete.book.success"));
    }


    /**
     * 批量删除图书
     * @param ids
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/removeMany", method = RequestMethod.DELETE)
    @RequiresPermissions(value = "/book/removeMany")
    @Transactional
    public JsonResult deleteBooks(@RequestParam(name = "ids") List<Integer> ids, HttpServletRequest request) throws Exception{
        //记录操作行为
        operationService.record(ids,Constant.BOOK,request);

        for (Integer bookId : ids) {
            Assert.notNull(bookId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
            // 权限验证
            validateRole(Method.DELETE, bookId);
            bookService.deleteBook(bookId, request);
            Example deleteExample = new Example(AuditorMail.class);
            deleteExample.createCriteria().andEqualTo("bookId", bookId);
            auditorMailService.deleteByExample(deleteExample);
        }

        return new JsonResult(true, I18n.getMessage("delete.book.success"));
    }

    /**
     * 更新图书
     * @param bookVO
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @RequiresPermissions(value = "/book/edit")
    public JsonResult updateBook(@RequestBody BookVO bookVO, HttpServletRequest request){
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuthor()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "作者"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getCode()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "产品码"));
//        Assert.isTrue(!StringUtils.isEmpty(bookVO.getIsbn()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "ISBN"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书名"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getPress()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "出版社"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getQuoteid()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "引用的id"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuditorId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核人ID"));
//        Assert.isTrue(!StringUtils.isEmpty(bookVO.getBookaskUrl()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书问地址"));
        // 校验简介字数是否超长
        String remark = bookVO.getRemark();
        if(!StringUtils.isEmpty(remark)){
            Assert.isTrue(remark.length() <= Constant.LENGTH_1000_MAX, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "简介"));
        }
        Book baseBook  = bookMapper.selectByPrimaryKey(bookVO.getQuoteid());
        if (baseBook!=null && !baseBook.getCode().equals(bookVO.getCode())){
            return new JsonResultId(false,"图书产品编号不能修改");
        }
        Matcher matcher = pattern.matcher(bookVO.getName());
        if (matcher.matches()){
            return new JsonResultId(false,"图书名称含有特殊字符?|\\ /*\"<>:");
        }

//        //检测isbn是否已被创建
//        if(!"".equals(bookVO.getIsbn()) && bookVO.getIsbn()!=null){
//            boolean hasIsbn = bookService.hasIsbn(bookVO.getId(),bookVO.getIsbn());
//            if(hasIsbn){
//                return new JsonResultId( false, "修改失败,isbn号已经存在！");
//            }
//        }

        bookService.updateBook(bookVO, request);
        return new JsonResult(true, I18n.getMessage("update.book.success"));
    }

    /**
     * 图书下拉框
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @RequestMapping(value = "/downList", method = RequestMethod.GET)
    @RequiresPermissions(value = "/book/downList")
    public JsonResultList getBookArray(@RequestParam int dataType){
        Assert.notNull(dataType, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "数据类型"));

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        example.selectProperties("id", "name");
        criteria.andEqualTo("type", dataType);
        DataPermission.hasPermission(Book.class, Method.QUERY, bookService, criteria);
        List<Book> bookList = bookService.selectByExample(example);

        if(bookList != null && bookList.size() > 0){
            return new JsonResultList(mapper.mapList(bookList, BookMenuVO.class));
        }
        return new JsonResultList(false, I18n.getMessage("query.error"));
    }


    @Autowired
    private BookCatalogMapper bookCatalogMapper;
    /**
     * 图书明细
     * @param id
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
//    @RequiresPermissions(value = "/book/detail")
    public JsonResultData getBookDetail(@RequestParam int id){
        Assert.notNull(id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        // 权限验证
            validateRole(Method.GET, id);

        Book book = bookService.selectByPrimaryKey(id);
        if (book != null) {
            BookVO bookVO = mapper.map(book, BookVO.class);
            bookVO.setEditorId(book.getOnwer());
            AuditorMail mail =  bookService.getEmail(id);
            if (mail !=null)
            {
                bookVO.setEmail(mail.getAuditorEmail());
                bookVO.setTip(mail.getTip());
            }
            // 查询当前二级节点名
            if(bookVO.getCategoryId()!=0){
                BookCatalog bookCatalog2 = bookCatalogMapper.selectByPrimaryKey(bookVO.getCategoryId());
                bookVO.setCategory(bookCatalog2.getName());
            }

            return new JsonResultData(bookVO);
        }



        return new JsonResultData(false, I18n.getMessage("query.error"));
    }

    /**
     * isbn级联查询图书
     * @param isbn
     * @return
     * @author yanfei
     * @date 2015.10.22
     */
    @RequestMapping(value = "/reuse", method = RequestMethod.GET)
//    @RequiresPermissions(value = "/book/reuse")
    public JsonResultList linkBook(@RequestParam String isbn){
        Assert.notNull(isbn, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "ISBN"));


        if (StringUtil.validateGBK(isbn)) {
            return new JsonResultList(false, I18n.getMessage("query.regex"));
        }

        List<Book> bookList = bookService.getUnCreatedBookByCode(isbn);
        List<IsbnBookVO> idbnBookList = new ArrayList<>();
        IsbnBookVO isbnBookVO;
        if (bookList != null && bookList.size() > 0){
            //转换前端vo
            for(Book book : bookList){
                isbnBookVO = new IsbnBookVO();
                isbnBookVO.setId(book.getId());
                isbnBookVO.setCode(book.getCode());
                isbnBookVO.setIsbn(book.getIsbn());
                isbnBookVO.setCategory(book.getCategory());
                isbnBookVO.setCategoryId(book.getCategoryId());
                isbnBookVO.setName(book.getName());
                isbnBookVO.setAuthor(book.getAuthor());
                isbnBookVO.setPress(pressService.selectByPrimaryKey(book.getPressId()).getName());
                isbnBookVO.setPressId(book.getPressId());
                isbnBookVO.setRemark(book.getRemark());
                isbnBookVO.setCover(book.getCover());
                isbnBookVO.setEditorId(book.getOnwer());
                isbnBookVO.setEditor(book.getEditor());
                idbnBookList.add(isbnBookVO);
            }
            return new JsonResultList(idbnBookList, I18n.getMessage("query.success"));
        }
        return new JsonResultList(false, I18n.getMessage("query.error"));
    }

    /**
     * 设置审核邮箱
     * @param bookEmailVO
     * @return
     * @author yanfei
     * @date 2015.11.3
     */
    @RequestMapping(value = "/setEmail", method = RequestMethod.POST)
    @RequiresPermissions(value = "/book/setEmail")
    public JsonResult editEmail(@RequestBody @Valid BookEmailVO bookEmailVO){
        Assert.notNull(bookEmailVO.getEmail(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核邮箱"));
        Assert.notNull(bookEmailVO.getTip(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "课件下载提示"));

        bookService.editEmail(bookEmailVO.getEmail(), bookEmailVO.getTip(),0);
        return new JsonResult(true, I18n.getMessage("save.email.success"));
    }

    /**
     * 获得默认审核邮箱
     * @return
     * @author yanfei
     * @date 2015.11.3
     */
    @RequestMapping(value = "/detailEmail", method = RequestMethod.GET)
   // @RequiresPermissions(value = "/book/detailEmail")
    public JsonResultData detailEmail(){

        Map<String,String> map = new HashMap<>();
        map.put("email",bookService.getEmail(0).getAuditorEmail());
        map.put("tip",bookService.getEmail(0).getTip());
        return new JsonResultData(map);
    }

    /**
     * 校验数据
     * @param i
     * @param book
     * @author yanfei
     * @date 2015.11.14
     */
    private String validateData(int i, Book book){
        String message = "";
        if (book.getAuthor().length() > 50){
            message = MessageFormat.format(ConsHint.WORD_NUMBER_MAX, i, "作者");
        }else if (book.getCode().length() > 60){
            message =  MessageFormat.format(ConsHint.WORD_NUMBER_MAX, i, "产品编号");
        }else if (book.getName().length() > 60){
            message =  MessageFormat.format(ConsHint.WORD_NUMBER_MAX, i, "图书名称");
        }else if (book.getPress().length() > 255){
            message =  MessageFormat.format(ConsHint.WORD_NUMBER_MAX, i, "分社");
        }else if (!StringUtils.isEmpty(book.getRemark()) && book.getRemark().length() > Constant.LENGTH_1000_MAX){
            message =  MessageFormat.format(ConsHint.WORD_NUMBER_MAX, i, "简介");
        }

        if (StringUtils.isEmpty(message) && StringUtil.validateGBK(book.getCode())){
            message =  MessageFormat.format(ConsHint.STRING_GBK, i, "产品编号");
        }
        return message;
    }

    /**
     * 导入基础信息
     * @param data
     * @return
     * @throws IOException
     * @author yanfei (zml 改)
     * @date 2015.11.3 2017.1.22
     */
    @Transactional
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public JsonResult importBooks(@RequestParam MultipartFile data) throws IOException {
        List<Book> bookList = new ArrayList<>();
        List<Book> bkList = new ArrayList<>();
        Map<String, Integer> pressCache = new HashMap<>();
        Map<String, Integer> codeRepeat = new HashMap<>();
        Map<String, Integer> onWerMap = new HashMap<>();
        // 校验上传的文件
        String fileName = data.getOriginalFilename();
        if(StringUtils.isEmpty(fileName)){
            return new JsonResult(false,"文件名为空,请重新上传!");
        }
        String suffix = NameUtil.getExtensionName(fileName);
        if(!"xlsx".equals(suffix) && !"xls".equals(suffix)){
            return new JsonResult(false,"文件格式不正确!");
        }
        // 获取导入的教师信息集合
        bkList = bookService.readFromExcel(data.getInputStream());
        if (bkList.size() == 0){
            throw new ExcelException("不存在可导入的数据");
        }
        for(int i = 0; i < bkList.size(); i++){
            // 产品编号 字段必填
            if (StringUtils.isEmpty(bkList.get(i).getCode())) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '产品编号' 不能为空！");
            }
            // 图书名称 字段必填
            if (StringUtils.isEmpty(bkList.get(i).getName())) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '图书名称' 不能为空！");
            }
            // 编辑 字段必填
            if (StringUtils.isEmpty(bkList.get(i).getEditor())) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '编辑' 不能为空！");
            }
            // 分类 字段必填
            if (StringUtils.isEmpty(bkList.get(i).getCategory())) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '图书分类' 不能为空！");
            }

            // 分社 字段必填
            if (StringUtils.isEmpty(bkList.get(i).getPress())) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '分社' 不能为空！");
            }

            // 格式化产品编号
            try {
                bkList.get(i).setCode(codeFormat(bkList.get(i).getCode()));
            } catch (NumberFormatException e) {
                throw new ExcelException("导入失败！第" + (i+2) + "行, '产品编号' 存在非法数字！");
            }

            //检查category
            String category = bkList.get(i).getCategory();
            BookCatalog bookCatalog = new BookCatalog();
            bookCatalog.setName(category);
            bookCatalog.setLevelId(3);//必须是二级分类名称
            List<BookCatalog> bookCatalogList = bookCatalogService.select(bookCatalog);
            if(bookCatalogList==null || bookCatalogList.size()==0){
                throw new ServiceException("图书分类不存在,或者图书分类不是二级分类,请重新输入!");
            }

            BookCatalog bkCatalog = bookCatalogList.get(0);
            bkList.get(i).setCategoryId(bkCatalog.getId());

            // 检查 code 重复
            String code = bkList.get(i).getCode();

            // 检查 code 是否在文件中重复
            if (codeRepeat.get(code) != null){
                throw new ExcelException("导入失败！第" + codeRepeat.get(code) + "行与第" + (i+2)  + "行 '产品编号' 值重复！");
            }
            codeRepeat.put(code, i+2);

            // 检查 code 是否在数据库中重复
            Example bookExample = new Example(Book.class);
            Example.Criteria bookCriteria = bookExample.createCriteria();
            bookCriteria.andEqualTo("code",code);
            List<Book> list = bookService.selectByExample(bookExample);
            if(list != null && list.size()!=0){
                throw new ExcelException("产品编号: " + code + " 已经存在,请核对!");
            }

            // 检查bkList中分社是否存在
            String pressName = bkList.get(i).getPress();
            if (!StringUtils.isEmpty(pressName)){
                if(pressCache.get(pressName) != null){
                    bkList.get(i).setPressId(pressCache.get(pressName));
                }else {
                    Example example = new Example(Press.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("name",pressName);
                    List<Press> pressList = pressService.selectByExample(example);
                    if(pressList == null || pressList.size()==0){
                        log.debug("导入需要新增分社！ : " + pressName);
                        Press press = new Press();
                        press.setName(pressName);
                        press.setCreateId(CurrentUser.getUser().getId());
                        press.setRemark("导入基础图书数据时新增了该分社！");
                        press.setCreateTime(new Date());
                        pressService.insertSelective(press);
                        bkList.get(i).setPressId(press.getId());
                        pressCache.put(press.getName(), press.getId());
                    }else {
                        pressCache.put(pressName, pressList.get(0).getId());
                        int pressId = pressList.get(0).getId();
                        bkList.get(i).setPressId(pressId);
                    }
                }
            }

            // 处理编辑对应的 OnWer 值
            Integer userId = userService.getUserIdByUserName(bkList.get(i).getEditor(),bkList.get(i).getPressId());
            if (userId == null || userId == 0){
                // 查不到就新建
                User user = new User();
                user.setUserName(bkList.get(i).getEditor());
                user.setRole(1); // 默认 编辑员 0：管理员；1：编辑员；2：总编  3:审核员
                user.setStatus(2); //  1:正常2:未激活 3 删除 4待审核
                user.setPassword("e10adc3949ba59abbe56e057f20f883e"); // 默认密码 123456
                user.setPressId(bkList.get(i).getPressId()); // 分社

                //1.新增组织信息  t_org//新增一条自己的
                Org org = new Org();
                org.setName(user.getUserName());
                orgService.insertSelective(org);
                user.setOrgId(org.getId());
                //2.新增用户信息  t_user
                userService.insertSelective(user);
                //3.新增数据权限信息   t_use_org
                userService.insertUseOrg(user);
                userId = user.getId();
            }
            bkList.get(i).setOnwer(userId);
            bkList.get(i).setStatus(1);
            bkList.get(i).setType(1);
            bkList.get(i).setQuoteid(1);
            bookList.add(bkList.get(i));
        }
//        int i = 2;// 表头算一行，所以从2开始
        if (bookList != null && bookList.size() > 0) {
            for(Book book : bookList){
                Matcher matcher = pattern.matcher(book.getName());
                if (matcher.matches()){
                    book.setName(book.getName().replaceAll("\\\\", "-").replaceAll("/", "-").replaceAll(":", "-").replaceAll("\\*", "-").replaceAll("\\?", "-").replaceAll("<", "-").replaceAll(">", "-").replaceAll("|", "-"));
                    // throw new ExcelException(MessageFormat.format(ConsHint.BOOK_NAME, i, "图书名称","?|\\ /*\"<>:"));
                }
            }
            // 对 list 进行拆分。分次插入数据
            List<Book> cacheList = new ArrayList<>();
            for (int i = 1; i < bookList.size()+1;  i ++){
                cacheList.add(bookList.get(i-1));
                if (i % 1000 == 0 || i == bookList.size()){
                    bookService.insertBooks(cacheList);
                    cacheList.clear();
                }
            }
            return new JsonResult(true, I18n.getMessage("import.success"));
        }
        throw new ExcelException(I18n.getMessage("import.error"));
    }

    public String codeFormat(String code) throws NumberFormatException{
        code = code.replaceAll(" ", "");
        String rule = "-";
        if (!code.contains("-")){
            return String.format("%06d", Integer.parseInt(code))+"-01";
        }
        return String.format("%06d", Integer.parseInt(code.substring(0, code.indexOf(rule)))) + code.substring(code.indexOf(rule), code.length());
    }

    /**
     * 图书加锁
     * @param bookLockVo
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/lock", method = RequestMethod.PUT)
    public JsonResult deleteBook(@RequestBody BookLockVo bookLockVo) throws Exception{
        Assert.notNull(bookLockVo.getIds(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        Assert.notNull(bookLockVo.getStatus(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "状态码"));

        List<Object> idsList = bookLockVo.getIds();
        if(idsList == null || idsList.size()==0){
            throw new ServiceException("参数错误！");
        }

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",idsList);

        Book book = new Book();
        book.setStatus(bookLockVo.getStatus());
        bookService.updateByExampleSelective(book,example);

        return new JsonResult(true, I18n.getMessage("操作成功!"));
    }

    /**
     * 选取复制图书模板
     * @param code
     * @return
     * @author cuihao
     * @date 2017.10.21
     */
    @RequestMapping(value = "/selectMode", method = RequestMethod.GET)
    public JsonResultList validateCode(String code) throws Exception{
        Assert.notNull(code, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书code"));

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",Constant.ZERO);
        criteria.andLike("code", "%" + code + "%");
        // 如果是编辑员  只能查到分给自己的；
        if(CurrentUser.getUser().getRole() == 1){
            criteria.andEqualTo("onwer",CurrentUser.getUser().getId());
        }
        example.setOrderByClause("code");
        List<Book> bookList = bookService.selectByExample(example);

        if(bookList!=null && bookList.size()!=0){
            Iterator<Book> it = bookList.iterator();
            while(it.hasNext()){
                Book book = it.next();
                List<Book> books = bookService.validateCode(book.getCode());
                if(books==null || books.size()==0){
                    it.remove();
                }
            }

        }

        return new JsonResultList(bookList);
    }

    /**
     * 根据code查询其下未初始化的图书0108
     * @param code
     * @return
     * @author cuihao
     * @date 2017.10.21
     */
    @RequestMapping(value = "/selectUnInit", method = RequestMethod.GET)
    public JsonResultList selectUnInit(String code) throws Exception{
        Assert.notNull(code, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书code"));

        List<Book> books = bookService.validateCode(code);

        return new JsonResultList(books);
    }

    /**
     * 图书复用
     * @param baseBookId 新建图书引用的基础图书id
     * @param bookId 模板图书id
     * @return
     * @author cuihao
     * @date 2017.10.21
     */
    @RequestMapping(value = "/reuseBook", method = RequestMethod.GET)
    public JsonResult reuseBook(Integer baseBookId,Integer bookId,HttpServletRequest request) throws Exception{
        Assert.isTrue(baseBookId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "基础图书id"));
        Assert.isTrue(bookId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "模板图书id"));

        return bookService.reuseBook(baseBookId,bookId,request);
    }

    /**
     * 图书下拉框
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @RequestMapping(value = "/examineDownList", method = RequestMethod.GET)
    public JsonResultList getBookArray(Integer pressId, String userId,@RequestParam(required = false,defaultValue = "0") Integer flag ){
        Assert.isTrue(pressId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分社id"));

        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", Constant.ZERO);

        if(pressId!=0){
            criteria.andEqualTo("pressId",pressId);
        }

        if(userId!=null && !"".equals(userId)){//传空拿全部
            criteria.andEqualTo("auditorId",userId);
        }

        if(flag!=0){
            int formal=0;//0：正式出版物 1：非正式出版物
            if(flag==1){
                formal = 0;
            }else if(flag ==2){
                formal = 1;
            }
            criteria.andEqualTo("formal",formal);
        }

        List<Book> bookList = bookService.selectByExample(example);

        return new JsonResultList(mapper.mapList(bookList, BookMenuVO.class));
    }

    /**
     * 转码mp4
     * @return
     * @author yanfei
     * @date 2015.10.19
     */
    @Autowired
    private FileResMapper fileResMapper;
    @RequestMapping(value = "/confvert", method = RequestMethod.GET)
    public void confvert(HttpServletRequest request,int bookId) throws IOException {
        Map map = new HashMap<>();
        map.put("bookId",bookId);

        List<FileRes> list = fileResMapper.getFileRess(map);

        String abPath = PathUtil.getAppRootPath(request);
        String cpPath = "/home/data/2weima/tmp/";
        //  过滤不需要转换的文件
        try {
            FileUtils.forceMkdir(new File(cpPath));
            for (int i = 0; i < list.size(); i ++ ){
                System.out.println(list.get(i).getPath());
                Map resMap = RunCmdUtil.runCmdResMap("ffprobe -v quiet -print_format json -show_format -show_streams " + abPath + list.get(i).getPath().replace("\\","/"), cpPath);
                JSONObject json = JSONObject.parseObject(String.valueOf(resMap.get("info")));
                Object oJson = json.get("streams");
                JSONArray array = JSON.parseArray(oJson.toString());

                JSONObject videoStream = array.getJSONObject(0);
                JSONObject audioStream = array.getJSONObject(1);

                System.err.println(list.get(i).getPath());
                System.err.println(audioStream.getString("codec_name"));
                System.err.println(videoStream.getString("codec_name"));
                if ("h264".equalsIgnoreCase(videoStream.getString("codec_name")) && "aac".equalsIgnoreCase(audioStream.getString("codec_name"))) {
                    list.remove(i);
                    break;
                }
            }
        }catch (Exception e) {
            log.error("以命令方式获取文件详细信息失败" + e.getMessage());
        }


        for (FileRes fileRes:list) {
            String path = fileRes.getPath().replace("\\","/");
            String dir = path.substring(0,path.lastIndexOf("/")+1);
            FileUtils.forceMkdir(new File(cpPath+dir));
            String cmdStr = "ffmpeg -i "+path+" -vcodec libx264 -preset fast -crf 28 -y -acodec libmp3lame -ab 128k "+cpPath+path;
            try {
                System.out.println("ffmpeg -i "+abPath+path+" -vcodec libx264 -preset fast -crf 28 -y -acodec libfaac -ab 128k "+cpPath+path);
                RunCmdUtil.runCmdResMap("ffmpeg -i "+abPath+path+" -vcodec libx264 -preset fast -crf 28 -y -acodec libfaac -ab 128k "+cpPath+path,cpPath);
            } catch (Exception e) {
                try {
                    FileUtils.write(new File(cpPath+"rmdd.txt"),"path:"+path+"/r/n","utf-8",true);
                } catch (IOException e1) {
                    log.error("",e1);
                }
            }

        }
    }

    @RequestMapping(value = "/right", method = RequestMethod.PUT)
//    @RequiresPermissions(value = "/book/right")
    public JsonResult bookRight(@RequestParam(value = "editorId") int editorId,
                                @RequestParam(value = "id") String ids ,
                                @RequestParam(value = "pressId") int pressId ){
        return bookService.bookRight(editorId, ids, pressId);
    }

    /**
     * 图书下拉列表 仅保留图书下有资源的图书
     * @param pressId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/NewExamineDownList", method = RequestMethod.GET)
    public JsonResultList getNewExamineDownList(@RequestParam(value = "pressId", defaultValue = "0", required = false) Integer pressId,
                                                @RequestParam(value = "userId", defaultValue = "", required = false) String userId ){
        Assert.isTrue(pressId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分社id"));
        return bookService.getNewExamineDownList(pressId, userId);
    }

    /**
     * 图书设置仅限制微信扫描
     * @param flag 开关 1：是 2否
     * @param bookId 图书id
     * @return
     */
    @RequestMapping(value = "/setWetChat", method = RequestMethod.GET)
    public JsonResult setWetChat( Integer flag, Integer bookId ){
        Assert.isTrue(flag>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "flag"));
        Assert.isTrue(bookId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        return bookService.setWetChat(flag, bookId);
    }


    /**
     * 初始化t_book表基础数据的quoteId字段
     * 1代表未创建  2已创建
     * @return
     */
    @RequestMapping(value = "/initBase", method = RequestMethod.GET)
    public JsonResult initBase(){
        Example example = new Example(Book.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",0);

        List<Book> bookList = bookService.selectByExample(example);

        for(Book book:bookList){
            int quoteid = book.getQuoteid();
            Book baseBook = bookService.selectByPrimaryKey(quoteid);
            baseBook.setQuoteid(2);
            bookService.updateByPrimaryKeySelective(baseBook);
        }

        return new JsonResult(true,"success!");
    }

}
