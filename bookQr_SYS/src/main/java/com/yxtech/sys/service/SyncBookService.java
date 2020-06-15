package com.yxtech.sys.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.common.json.JsonResult;
import com.yxtech.sys.dao.PressMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookCatalog;
import com.yxtech.sys.domain.SysConfig;
import com.yxtech.sys.vo.sync.BookInfo;
import com.yxtech.sys.vo.sync.SyncBook;
import com.yxtech.utils.qr.HttpTookit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * create by zml on 2017/12/14 10:17
 */
@Service
public class SyncBookService {
    public final static Logger log = LoggerFactory.getLogger(SyncBookService.class);

    /**
     * 进度
     */
    public double progress = 0;
    public boolean bool = true;

    @Value("#{configProperties['zhixueyun.host']}")
    private String host;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private BookService bookService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BookCatalogService bookCatalogService;

    /**
     * 同步
     * @return
     */
    public JsonResult syncBookBaseInfo() throws Exception{
        if (bool) {
            bool = false;
            // 从数据库里获取上次同步时间点
            SysConfig sysConfig = sysConfigService.selectByPrimaryKey(1);
            Date time = sysConfig.getSyncTime();
            String syncTime = DateFormatUtils.format(time, "yyyy-MM-dd");
            // 获取 token
            String token = userCenterService.getToken();
            String reqUrl = host + "/book/byLastTime?page=1&count=99999&token=" + token + "&time=" + syncTime;
            // 记录当前日期
            Date nowTime = new Date();

            try {
                // 发送请求接口返回对象数据
                String result = HttpTookit.doGet(reqUrl, null, "utf-8", true);
                if (!StringUtils.isEmpty(result)) {
                    SyncBook syncBookList = JSONArray.parseObject(result, SyncBook.class);
                    if (syncBookList != null && syncBookList.getBooks().size() != 0) {

                        updateBookInfo(syncBookList);
                        // 更新同步时间点 新增数据字典表
                        sysConfigService.updateSysConfigBySyncTime(nowTime);
                        bool = true;
                        this.progress = 100;
                        return new JsonResult(true, "数据同步成功！");
                    } else {
                        // 没有要更新的数据
                        bool = true;
                        this.progress = 100;
                        return new JsonResult(true, "没有要同步的数据！");
                    }
                }else {
                    // 没有要更新的数据
                    bool = true;
                    return new JsonResult(false, "他们没有返回要同步的数据！请求接口: " + reqUrl);
                }
            } catch (Exception e) {
                log.error("同步图书失败！时间点未更新！", e);
                // 如果报错不更新同步时间点
                // 解析 Book 对象的值
                bool = true;
                throw e;
            }finally {
                // 不管成功或者失败，都保证可以第二次点击同步
                this.progress = 100;
                bool = true;
            }
        }else {
            return new JsonResult(false, "当前已有同步任务正在执行！");
        }
    }

    public void updateBookInfo(SyncBook syncBookList) throws Exception {
        DecimalFormat df = new DecimalFormat("######0.00");
        List<BookInfo> bookList = syncBookList.getBooks();
        for (int i = 0; i < bookList.size(); i++) {
            String code = bookList.get(i).getBookId();
            // 跟据 code 获取相关图书数据,这时候的图书是
            List<Book> books = bookService.getBookListByCode(code);
            if (books.size() != 0) {
                // 更新所查询到的基础数据和已新增的数据
                for (Book book : books) {
                    book.setName(bookList.get(i).getBookName());
                    book.setIsbn(bookList.get(i).getISBN());
//                        book.setPress(bookInfo.getBranch());
//                        book.setEditor(bookInfo.getEditor());
                    book.setAuthor(bookList.get(i).getAuthor());
                    book.setSwCategory(bookList.get(i).getSwCategory());
                    book.setWdCategory(bookList.get(i).getWdCategory());
                    book.setZyCategory(bookList.get(i).getZyCategory());
                    book.setKcCategory(bookList.get(i).getKcCategory());
                    book.setRemark(bookList.get(i).getIntroduce());
                    // 获取这个图书的最新分类 ID 以及一级分类的ID
                    setBookCategory(book, bookList.get(i));
                    bookService.updateSyncBookById(book);
                }
            }else {
                // 新增这条数据
                Book book = new Book();
                book.setCode(bookList.get(i).getBookId());
                book.setIsbn(bookList.get(i).getISBN());
                book.setName(bookList.get(i).getBookName());
                book.setPress(bookList.get(i).getBranch());
                book.setEditor(bookList.get(i).getEditor());
                book.setAuthor(bookList.get(i).getAuthor());
                book.setSwCategory(bookList.get(i).getSwCategory());
                book.setWdCategory(bookList.get(i).getWdCategory());
                book.setZyCategory(bookList.get(i).getZyCategory());
                book.setKcCategory(bookList.get(i).getKcCategory());
                book.setRemark(bookList.get(i).getIntroduce());
                book.setQuoteid(1);
                // 获取这个图书的最新分类 ID 以及一级分类的ID
                setBookCategory(book, bookList.get(i));
                bookService.insertBookBaseInfo(book);
            }
            // 计算百分比
            this.progress = Double.valueOf(df.format((Double.valueOf(i+1)/bookList.size() * 100)));
        }
    }

    /**
     * 处理类别
     * @param book
     * @param bookInfo
     */
    public void setBookCategory(Book book, BookInfo bookInfo){
        if (!StringUtils.isEmpty(bookInfo.getZtfCategory())) {
            BookCatalog bookCatalog = bookCatalogService.getBookCatalogIdByName(bookInfo.getZtfCategory());
            if (bookCatalog != null) {
                book.setCategory(bookCatalog.getName());
                book.setCategoryId(bookCatalog.getId());
                book.setCategorySuperId(bookCatalog.getParentId());
            }
        }
    }

    @Autowired
    private PressMapper pressMapper;

    public JsonResult syncBookList() {
        this.progress = 0;
        DecimalFormat df = new DecimalFormat("######0.00");
        if (bool) {
            bool = false;
            // 获取 token
            String token = userCenterService.getToken();
            String reqUrl = host + "/book/all?page=1&count=10" + "&token=" + token + "&time=1990-12-26";
            // 发送请求接口返回对象数据
            String result = HttpTookit.doGet(reqUrl, null, "utf-8", true);
            SyncBook syncBookList = JSONArray.parseObject(result, SyncBook.class);
            System.out.println(syncBookList.getBooks().size());
            List<BookInfo> bookList = syncBookList.getBooks();
            // 百分比计算
            for (int i = 0; i < bookList.size(); i++) {
                if (!StringUtils.isEmpty(bookList.get(i).getBranch())) {
                    Integer pressId = pressMapper.getPressIdByName(bookList.get(i).getBranch());
                    System.out.println(pressId);
                }
                this.progress = Double.valueOf(df.format((Double.valueOf(i)/bookList.size() * 100)));
            }
            bool = true;
            return new JsonResult(true, "ok");
        }else {
            return new JsonResult(false, "当前已有同步任务正在执行！");
        }
    }

    /**
     * 获取进度
     * @return
     */
    public double getProgress() {
        System.out.println(this.progress);
        return progress;
    }


    /**
     * 重置同步进度条
     * @return
     */
    public double reset() {
        // 不管成功或者失败，都保证可以第二次点击同步
        this.progress = 0;
        bool = true;
        return 1;
    }


}
