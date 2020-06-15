package com.yxtech.sys.controller;

import com.google.common.collect.Lists;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.datapiess.Method;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultId;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.UserMapper;
import com.yxtech.sys.domain.AuditorMail;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.BookVO;
import com.yxtech.utils.file.StringUtil;
import com.yxtech.utils.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 非正式图书管理控制器
 * @author cuihao
 * @since 2017-5-9
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/publication")
public class PublicationController {

    private static final Logger log = LoggerFactory.getLogger(PublicationController.class);

    @Autowired
    private PublicationService publicationService;
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

    /*
     * 上传类型白名单
     */
    private List<String> fileTypes = Lists.newArrayList(Constant.FILE_MIME_XLS, Constant.FILE_MIME_XLSX);

    //校验文件名不能有特殊字符
    private Pattern pattern = Pattern.compile("^.*[/|\\\\|:|\\*|\\?|\"|<|>].*$");



    /**
     * 验证权限
     * @param method
     * @author cuihao
     * @date 2015.10.23
     */
    private void validateRole(Method method, Integer id){
//        if(CurrentUser.getUser().getRole() != 0){
//            Example example = new Example(Book.class);
//            Example.Criteria criteria = example.createCriteria();
//            if(id != null){
//                DataPermission.hasPermission(Book.class, method, publicationService, criteria, id);
//            }else {
//                DataPermission.hasPermission(Book.class, method, publicationService, criteria);
//            }
//        }
    }

    /**
     * 新建图书
     * @param bookVO
     * @return
     * @author cuihao
     * @date 2015.10.16
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultId saveBook(@RequestBody BookVO bookVO, HttpServletRequest request) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuthor()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "作者"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书名"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getPress()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "出版社"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核邮箱"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getTip()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "提示信息"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuditorId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核人ID"));

        //检测isbn是否已被创建
        if(!"".equals(bookVO.getIsbn()) && bookVO.getIsbn()!=null){
            boolean hasIsbn = publicationService.hasIsbn(0,bookVO.getIsbn());
            if(hasIsbn){
                return new JsonResultId( false, "新增失败,isbn号已经存在！");
            }
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
        bookVO.setOnwer(bookVO.getEditorId());
        bookVO.setFormal(Constant.ONE);
        bookVO.setType(Constant.ZERO);
        bookVO.setCreateTime(new Date());
         int id = publicationService.saveBook(bookVO, request);

        return new JsonResultId(id,"新增成功!");
    }

    /**
     * 图书列表
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @author cuihao
     * @date 2015.10.16
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResultPage getBookList(@RequestParam(value = "keyword",defaultValue = "",required = false) String keyword,
                                      @RequestParam int page,
                                      @RequestParam int pageSize,
                                      @RequestParam(value = "userId", defaultValue = "0", required = false) Integer userId,
                                      @RequestParam(value = "orderTime", defaultValue = "0", required = false) Integer orderTime,
                                      @RequestParam(value = "isbns",required = false) String isbns,
                                      @RequestParam(value = "pressId", defaultValue = "0") Integer pressId,
                                      @RequestParam(value = "parentCategoryId", defaultValue = "0") Integer parentCategoryId,
                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        Assert.notNull(page, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页码"));
        Assert.notNull(pageSize, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "分页数"));
        return publicationService.getBookList(StringUtil.escape4Like(keyword), page, pageSize, userId, orderTime , isbns,pressId,parentCategoryId,categoryId);
    }


    /**
     * 更新图书
     * @param bookVO
     * @return
     * @author cuihao
     * @date 2017.5.9
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public JsonResult updateBook(@RequestBody BookVO bookVO, HttpServletRequest request){
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuthor()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "作者"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书名"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getPress()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "出版社"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getEmail()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核邮箱"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getTip()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "提示信息"));
        Assert.isTrue(!StringUtils.isEmpty(bookVO.getAuditorId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核人ID"));
        // 校验简介字数是否超长
        String remark = bookVO.getRemark();
        if(!StringUtils.isEmpty(remark)){
            Assert.isTrue(remark.length() <= Constant.LENGTH_1000_MAX, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "简介"));
        }
        // 校验图书名称含有特殊字符
        Matcher matcher = pattern.matcher(bookVO.getName());
        if (matcher.matches()){
            return new JsonResultId(false,"图书名称含有特殊字符?|\\ /*\"<>:");
        }
        //检测isbn是否已被创建
        if(!"".equals(bookVO.getIsbn()) && bookVO.getIsbn()!=null){
            boolean hasIsbn = publicationService.hasIsbn(bookVO.getId(),bookVO.getIsbn());
            if(hasIsbn){
                return new JsonResultId( false, "修改失败,isbn号已经存在！");
            }
        }

        publicationService.updateBook(bookVO, request);
        return new JsonResult(true, I18n.getMessage("update.book.success"));
    }

    /**
     * 图书明细
     * @param id
     * @return
     * @author cuihao
     * @date 2017.5.9
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResultData getBookDetail(@RequestParam int id){
        Assert.notNull(id, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        // 权限验证
        validateRole(Method.GET, id);

        Book book = publicationService.selectByPrimaryKey(id);
        if (book != null) {
            BookVO bookVO = mapper.map(book, BookVO.class);
            bookVO.setEditorId(book.getOnwer());
            AuditorMail mail =  publicationService.getEmail(id);
            if (mail !=null)
            {
                bookVO.setEmail(mail.getAuditorEmail());
                bookVO.setTip(mail.getTip());
            }

            return new JsonResultData(bookVO);
        }

        return new JsonResultData(false, I18n.getMessage("query.error"));
    }

}
