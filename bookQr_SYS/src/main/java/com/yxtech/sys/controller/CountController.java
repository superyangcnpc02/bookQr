package com.yxtech.sys.controller;

import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.service.BookQrService;
import com.yxtech.sys.service.BookService;
import com.yxtech.sys.service.QrViewRankService;
import com.yxtech.sys.service.ResService;
import com.yxtech.utils.file.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by liukailong on 2016/09/10
 * 统计Controller
 */
@RestController
@RequestMapping(value = "/count")
public class CountController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ResService resService;

    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private QrViewRankService qrViewRankService;

    /**
     * 预览排行
     * @param bookId
     * @param pressId
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/viewRank", method = RequestMethod.GET)
    public JsonResultPage viewRank(@RequestParam(name = "beginTime",required = false) String beginTime,
                                   @RequestParam(name = "endTime",required = false) String endTime,
                                   @RequestParam(name = "flag", required = false) Integer flag,
                                   @RequestParam("bookId") Integer bookId,@RequestParam("pressId") Integer pressId,
                                   @RequestParam(value="keyword",required = false) String keyword,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam("page") int pageNo,
                                   @RequestParam(value = "queryType", defaultValue = "0", required = false) int queryType) throws Exception{
        Assert.isTrue(bookId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"书ID"));
        Assert.isTrue(pressId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"分社ID"));
        return bookService.viewRank(beginTime, endTime, flag, StringUtil.escape4Like(keyword) , pageNo, pageSize,bookId,pressId,queryType);
    }

    /**
     * 导出预览排行
     * @param pressId
     * @param response
     */
    @RequestMapping(value = "/exportViewRank",method = RequestMethod.GET)
    public void exportViewRank(@RequestParam(value = "beginTime",required = false) String beginTime,
                               @RequestParam(value = "endTime",required = false) String endTime,
                               @RequestParam(value = "flag", defaultValue = "0", required = false) Integer flag,
                               @RequestParam(value = "bookId", defaultValue = "0", required = false) Integer bookId,
                               @RequestParam(value = "pressId", required = false) Integer pressId,
                               @RequestParam(value="keyword", defaultValue = "", required = false) String keyword,
                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.isTrue(pressId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"分社ID"));
        bookService.exportViewRank(beginTime, endTime, flag, StringUtil.escape4Like(keyword),bookId, pressId, request, response);
    }

    /**
     * 下载排行
     * @param bookId
     * @param qrcodeId
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/downLoadRank", method = RequestMethod.GET)
    public JsonResultPage downLoadRank(@RequestParam(name = "beginTime",required = false) String beginTime,
                                       @RequestParam(name = "endTime",required = false) String endTime,
                                       @RequestParam(name = "flag", required = false) Integer flag,
                                       @RequestParam("bookId") Integer bookId,@RequestParam("qrcodeId") Integer qrcodeId,
                                       @RequestParam(value="keyword",required = false) String keyword,
                                       @RequestParam("pageSize") int pageSize,
									   @RequestParam("pressId") Integer pressId,
                                       @RequestParam("page") int page) throws Exception{
        Assert.isTrue(bookId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"书ID"));
        Assert.isTrue(qrcodeId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"二维码ID"));
        return resService.downLoadRank(beginTime, endTime, flag, StringUtil.escape4Like(keyword) , page, pageSize,bookId,qrcodeId, pressId);
    }

    /**
     * 导出下载排行
     * @param bookId
     * @param qrcodeId
     * @param response
     */
    @RequestMapping(value = "/exportDownRank",method = RequestMethod.GET)
    public void exportDownRank(@RequestParam(name = "beginTime",required = false) String beginTime,
                               @RequestParam(name = "endTime",required = false) String endTime,
                               @RequestParam(name = "flag", required = false) Integer flag,
                               @RequestParam("bookId") Integer bookId,@RequestParam("qrcodeId") Integer qrcodeId,
                               @RequestParam(value="keyword",required = false) String keyword,
                               @RequestParam("pressId") Integer pressId,
                               HttpServletRequest request, HttpServletResponse response)  throws Exception{
        Assert.isTrue(bookId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"书ID"));
        Assert.isTrue(qrcodeId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"分社ID"));
        resService.exportDownRank(beginTime, endTime, flag, StringUtil.escape4Like(keyword) , bookId,qrcodeId, pressId, request, response);
    }

    /**
     * 扫描排行
     * @param pressId
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/bookRank", method = RequestMethod.GET)
    public JsonResultPage bookRank(@RequestParam(name = "beginTime",required = false) String beginTime,
                                   @RequestParam(name = "endTime",required = false) String endTime,
                                   @RequestParam(name = "flag", required = false) Integer flag,
                                   @RequestParam("pressId") Integer pressId,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam("page") int page,
                                   @RequestParam(value = "queryType", defaultValue = "0", required = false) int queryType) throws Exception {
        Assert.isTrue(pressId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社ID"));
        return bookService.bookRank(beginTime,endTime, flag, page, pageSize,pressId,queryType);
    }

    /**
     * 导出扫描排行
     * @param pressId
     * @return
     * @author liukailong
     * @date 2016/9/10
     */
    @RequestMapping(value = "/exportBookRank", method = RequestMethod.GET)
    public void exportBookRank(@RequestParam(name = "beginTime",required = false) String beginTime,
                               @RequestParam(name = "endTime",required = false) String endTime,
                               @RequestParam("pressId") Integer pressId,
                               @RequestParam(name = "flag", required = false) Integer flag,
                               HttpServletRequest request,
                               HttpServletResponse response) throws Exception{
        Assert.isTrue(pressId>=0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"出版社ID"));
        bookService.exportBookRank(beginTime,endTime, pressId,flag,request,response);
    }

    /**
     * 新增预览统计
     * @param id
     * @return
     * @author liukailong
     * @date 2017/3/31
     */
    @RequestMapping(value = "/addViewRank", method = RequestMethod.GET)
    public JsonResult addViewRank(@RequestParam("id") Integer id,@RequestParam("openId") String openId){
        Assert.isTrue(id>0,MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"二维码ID"));
        Assert.isTrue(!StringUtils.isEmpty(openId),MessageFormat.format(ConsHint.ARG_VALIDATION_ERR,"openId"));

        return qrViewRankService.addViewRank(id,openId);
    }

    /**
     * 初始化book表的priviewNum的值  只用一次
     * @return
     * @author liukailong
     * @date 2017/4/1
     */
    @RequestMapping(value = "/updataPrivewNum", method = RequestMethod.GET)
    public void updataPrivewNum(){
        Example bookExample = new Example(Book.class);
        Example.Criteria criteria = bookExample.createCriteria();
        criteria.andEqualTo("type",0);
        List<Book> bookList = bookService.selectByExample(bookExample);
        for (Book book : bookList) {
            Example example = new Example(BookQr.class);
            example.createCriteria().andEqualTo("bookId", book.getId());
            List<BookQr> bookQrList = bookQrService.selectByExample(example);
            int priviewNum = 0;
            int viewNum = 0;
            if(bookQrList != null && bookQrList.size() > 0){
                for (BookQr qr : bookQrList) {
                    if(qr.getPreviewNum() != null){
                        priviewNum = priviewNum + qr.getPreviewNum();
                    }
                    if(qr.getViewNum() != null){
                        viewNum = viewNum + qr.getViewNum();
                    }
                }
            }

            Book bk = new Book();
            bk.setId(book.getId());
            Book book2 = bookService.selectOne(bk);
            book2.setPreviewNum(priviewNum);
            book2.setViewNum(viewNum);
            bookService.updateByPrimaryKeySelective(book2);
        }
    }

}
