package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.itextpdf.text.pdf.PdfReader;
import com.yxtech.common.BaseService;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.PowerQrVo;
import com.yxtech.sys.vo.bookQr.*;
import com.yxtech.utils.excel.DocumentEmailHandler;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.mail.MailSender;
import com.yxtech.utils.password.Base64;
import com.yxtech.utils.password.PayEncrypt;
import com.yxtech.utils.password.WqjiaoxueVo;
import com.yxtech.utils.pdf.Pdf;
import com.yxtech.utils.qr.QREncode;
import com.yxtech.utils.zip.ZipUtils;
import jodd.datetime.JDateTime;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Chenxh on 2015/10/13.
 */
@Service("bookQrService")
public class BookQrService extends BaseService<BookQr>{
    //图书二维码跟路径
    public static final String QR_ROOT_PATH="WEB-INF"+File.separator+"books";
    private BookQrMapper bookQrMapper;
    @Autowired
    private QrExportMapper qrExportMapper;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private FileResMapper fileResMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private ResService resService;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private QrUserInfoService qrUserInfoService;
    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientAttrMapper clientAttrMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private QrUserInfoMapper qrUserInfoMapper;
    @Autowired
    private QrViewRankMapper qrViewRankMapper;
    @Autowired
    private QrDownRankMapper qrDownRankMapper;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private BookQrAuthService bookQrAuthService;
    @Autowired
    private MailSender mailSender;

    @Value("#{configProperties['bookask.key']}")
    private String key;
    @Value("#{configProperties['bookask.returnUrl']}")
    private String returnUrl;
    @Value("#{configProperties['bookask.loginUrl']}")
    private String loginUrl;
    @Value("#{configProperties['backFront.key']}")
    private String backFrontKey;
    @Value("#{configProperties['backFront.timeout']}")
    private Long backFrontTimeout;

    public static  final int  WIDTH=240;

    @Resource(name = "bookQrMapper")
    public void setBookQrMapper(BookQrMapper bookQrMapper) {
        setMapper(bookQrMapper);
        this.bookQrMapper = bookQrMapper;
    }

    /**
     * 更新二维码样式
     * @param bookQrStyleVo 二维码样式VO
     * @return  消息
     */
    public Map<String, Object> editStyle(BookQrStyleVo bookQrStyleVo,HttpServletRequest request) throws Exception {
        File oldLogo=null;
        FileRes _fileRes = new FileRes();
        if (null == bookQrStyleVo.getQrIds() || bookQrStyleVo.getQrIds().size() == 0) {
            Book book=this.bookService.selectByPrimaryKey(bookQrStyleVo.getId());
            if(null!=book.getLogo() && book.getLogo().length()>0){


                _fileRes.setUuid(book.getLogo());
                _fileRes = this.fileResService.selectOne(_fileRes);
                oldLogo = new File(PathUtil.getAppRootPath(request)+_fileRes.getPath());
            }
            book.setWidth(Integer.parseInt(bookQrStyleVo.getWidth()));
            book.setLogo(bookQrStyleVo.getFileId());
            this.bookService.updateByPrimaryKey(book);
        }

        Map<String, Object> map = new HashMap<>();
        //获取网址基础信息
        String http = PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //获取临时的宽度高度信息
        int width = Integer.parseInt(bookQrStyleVo.getWidth());
        //获取logo，默认是null
        File logo = null;
        FileRes fileRes = null;
        if (!StringUtils.isBlank(bookQrStyleVo.getFileId())) {
            fileRes = new FileRes();
            fileRes.setUuid(bookQrStyleVo.getFileId());
            fileRes = this.fileResService.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }

        //QREncode
        //如果二维码IDS是空则更新整本书二维码样式
        //否则更新单个二维码样式
        //查询出需要更新的二维码信息
        List<BookQr> list;
        if (null == bookQrStyleVo.getQrIds() || bookQrStyleVo.getQrIds().size() == 0) {
            BookQr bookQr;
            bookQr = new BookQr();
            bookQr.setBookId(bookQrStyleVo.getId());
            list = this.bookQrMapper.select(bookQr);
        } else {
            Example example = new Example(BookQr.class);
            example.createCriteria().andIn("id", bookQrStyleVo.getQrIds());
            list = this.bookQrMapper.selectByExample(example);
        }
        //再更新二维码样式和修改二维码名称时候先遍历将已经存在的二维码删除
        this.clearQR(rootPath, list);
        //创建二维码

        if (bookQrStyleVo.getTab() == 0){
            List<BookQr> list1 = new ArrayList<>();
            List<BookQr> list2 = new ArrayList<>();
            List<BookQr> list3 = new ArrayList<>();
            for(BookQr bookQr:list){
                if(bookQr.getQrType()==1){
                    list1.add(bookQr);
                }else if(bookQr.getQrType()==2){
                    list2.add(bookQr);
                }else{
                    list3.add(bookQr);
                }
            }
            this.creatQRFile(rootPath, http, width, logo, list1,1,request,bookQrStyleVo.getText());
            this.creatQRFile(rootPath, http, width, logo, list2,2,request,bookQrStyleVo.getText());
            this.creatQRFile(rootPath, http, width, logo, list3,3,request,bookQrStyleVo.getText());

        }else {
            this.creatQRFile(rootPath, http, width, logo, list,bookQrStyleVo.getTab(),request,bookQrStyleVo.getText());

        }
        //如果有logo更新完后删除logo文件
        //要将样式信息存入图书表中，所以不能删除了。
        //会造成垃圾图片缓存在服务器中占用资源
        if (null != oldLogo && oldLogo.exists()) {
            boolean flag = oldLogo.delete();
            if (flag) {
                this.fileResService.deleteByPrimaryKey(_fileRes.getId());
            }
        }
        map.put("message", I18n.getMessage("success"));
        map.put("status", true);
        return map;
    }

    /**
     * 新增二维码
     * @param bookQrAddVo  新增的VO
     * @param request request
     * @return     消息
     */
    public Map<String, Object> saveBookQR(BookQRAddVO bookQrAddVo,HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();

        //获取网址基础信息
        String http= PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //通过 bookQrStyleVo查询Book对象
        //Book book=xxxx;
        //二维码的宽度和高度，从Book对象中获取
        Book book = this.bookService.selectByPrimaryKey(bookQrAddVo.getId());
        int width=WIDTH;
        if(null!=book && book.getWidth()!=null&&book.getWidth()>=100){
            width=book.getWidth();
        }
        //是否有logo
        File logo=null;
        if(null!=book && book.getLogo()!=null && book.getLogo().length()>0) {
            FileRes fileRes = new FileRes();
            fileRes.setUuid(book.getLogo());
            fileRes=this.fileResService.selectOne(fileRes);
            if(null!=fileRes && fileRes.getPath()!=null){
                logo=new File(rootPath+fileRes.getPath());
                if(!logo.exists() || !logo.isFile()){
                    logo=null;
                }
            }
        }

        //保存bookQR信息到数据库
        List<BookQr> list = new ArrayList<>();
        BookQr bookQr=new BookQr();
        bookQr.setName(bookQrAddVo.getName());
        bookQr.setBookId(bookQrAddVo.getId());
        bookQr.setQrType(bookQrAddVo.getQrType());
        bookQr.setOnwer(book.getOnwer());
        bookQr.setViewNum(0);
        int status = this.getCheckStatus(bookQrAddVo.getId());
        if(status != 0){
            bookQr.setIsSend(status);
        }
        this.bookQrMapper.insertSelective(bookQr);
        list.add(bookQr);

        //创建二维码
        this.creatQRFile(rootPath, http, width, logo, list,bookQrAddVo.getQrType(), request,"");
        map.put("message", I18n.getMessage("success"));
        map.put("status",true);
        map.put("id", bookQr.getId());
        return map;
    }

    /**
     * 复制图书二维码
     * @param bookQr  新增的VO
     * @param request request
     * @return     消息
     */
    public Map<String, Object> reuseBookQR(Integer newBookId,BookQr bookQr,HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();

        //获取网址基础信息
        String http= PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //通过 bookQrStyleVo查询Book对象
        //Book book=xxxx;
        //二维码的宽度和高度，从Book对象中获取
        Book book = this.bookService.selectByPrimaryKey(newBookId);
        int width=WIDTH;
        if(null!=book && book.getWidth()!=null&&book.getWidth()>=100){
            width=book.getWidth();
        }
        //是否有logo
        File logo=null;
        if(null!=book && book.getLogo()!=null && book.getLogo().length()>0) {
            FileRes fileRes = new FileRes();
            fileRes.setUuid(book.getLogo());
            fileRes=this.fileResService.selectOne(fileRes);
            if(null!=fileRes && fileRes.getPath()!=null){
                logo=new File(rootPath+fileRes.getPath());
                if(!logo.exists() || !logo.isFile()){
                    logo=null;
                }
            }
        }

        //保存bookQR信息到数据库
        List<BookQr> list = new ArrayList<>();
        bookQr.setId(null);
        bookQr.setBookId(newBookId);
        this.bookQrMapper.insertSelective(bookQr);
        list.add(bookQr);

        //创建二维码
        this.creatQRFile(rootPath, http, width, logo, list,bookQr.getQrType(), request,"");
        map.put("message", I18n.getMessage("success"));
        map.put("status",true);
        map.put("id", bookQr.getId());
        return map;
    }

    /**
     * 删除二维码
     * @param ids 二维码的ID数组
     * @param request
     * @throws Exception
     */
    public void deleteBookQr(List<Integer> ids, HttpServletRequest request) throws Exception{
        // 根据ids查询文件路径
        for (Integer id : ids) {
            // 查询二维码实体对象
            BookQr bookQr = this.bookQrMapper.selectByPrimaryKey(id);
            if (null != bookQr) {

                // 根据qrId查询并删除Res数据及文件
                Example example1 = new Example(Res.class);
                example1.createCriteria().andEqualTo("qrId",id);
                List<Res> resList = resService.selectByExample(example1);
                for (Res res1:resList) {
                    if (null != res1) {
                        resService.delete(res1);
                        fileResService.deleteForFileByUUID(res1.getFileUuid(),request);
                        /**
                         * 向出版社删除图书和资源关系
                         */
                        userCenterService.deleteBookRes(res1.getId());
                    }
                }
                // 删除二维码实体数据，删除文件
                bookQrMapper.deleteByPrimaryKey(id);
                fileResService.deleteForFileByUUID(bookQr.getUrl(), request);
                //删除举报列表
                Example example = new Example(Report.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("qrcodeId",bookQr.getId());
                reportService.deleteByExample(example);
                //删除扫描统计、预览统计、下载统计列表
                QrUserInfo qrUserInfo = new QrUserInfo();
                qrUserInfo.setQrId(id);
                qrUserInfoMapper.delete(qrUserInfo);

                QrViewRank qrViewRank = new QrViewRank();
                qrViewRank.setQrId(id);
                qrViewRankMapper.delete(qrViewRank);

                QrDownRank qrDownRank = new QrDownRank();
                qrDownRank.setQrId(id);
                qrDownRankMapper.delete(qrDownRank);
                // add by liukailong 20170401 begin
                Book bk = new Book();
                bk.setId(bookQr.getBookId());
                Book book = bookService.selectOne(bk);
                book.setViewNum(book.getViewNum()-bookQr.getViewNum());
                book.setPreviewNum(book.getPreviewNum()-bookQr.getPreviewNum());
                bookService.updateByPrimaryKeySelective(book);
                // add by liukailong 20170401 end
            }
        }
    }

    /**
     * 删除二维码
     * @param ids   二维码的ID数组
     * @param request request
     */
    public void deleteBookQr2(List<Integer> ids,HttpServletRequest request) throws Exception {
        String rootPath = PathUtil.getAppRootPath(request);
        for (Integer id : ids) {
            //查询出二维码的实体对象
            BookQr bookQr=this.bookQrMapper.selectByPrimaryKey(id);
            //验证是否存在
            if(null==bookQr)
                continue;
            //查询出对应的二维码图片存储信息
            FileRes fileRes =new FileRes();
            fileRes.setUuid(bookQr.getUrl());
            fileRes =this.fileResService.selectOne(fileRes);
            File file = new File(rootPath + fileRes.getPath());
            //删除二维码
            this.bookQrMapper.deleteByPrimaryKey(id);
            //验证图片是否存在，存在则删除
            if(file.exists()) {
                boolean flag=file.delete();
                if(!flag) {
                    throw new Exception(I18n.getMessage("error"));
                }
            }
            //删除文件系统数据
            this.fileResService.deleteByPrimaryKey(fileRes.getId());
            Res r = new Res();
            r.setQrId(bookQr.getId());
            List<Res> list=this.resService.select(r);
            for (Res res : list) {
                FileRes fr =new FileRes();
                fr.setUuid(res.getFileUuid());
                fr=this.fileResService.selectOne(fr);
                this.fileResService.deleteByPrimaryKey( fr.getId());
                this.resService.delete(res);
            }
        }
    }

    /**
     * 编辑二维码
     * @param bookQr bookQr
     * @param request request
     */
    public void edit(BookQr bookQr,HttpServletRequest request) throws Exception {
        //获取网址基础信息
        String http= PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        BookQr bqr = this.bookQrMapper.selectByPrimaryKey(bookQr.getId());
        //判断要修改的对象名称是不是改变，改变则需要重新生成二维码
        if(!bookQr.getName().equals(bqr.getName())){
            Book book = this.bookService.selectByPrimaryKey(bqr.getBookId());
            int width=WIDTH;
            if(null!=book && book.getWidth()!=null){
                width=book.getWidth();
            }
            //是否有logo
            File logo=null;
            if(null!=book && book.getLogo()!=null && book.getLogo().length()>0) {
                FileRes fileRes = new FileRes();
                fileRes.setUuid(book.getLogo());
                fileRes=this.fileResService.selectOne(fileRes);
                if(null!=fileRes && fileRes.getPath()!=null){
                    logo=new File(rootPath+fileRes.getPath());
                    if(!logo.exists() || !logo.isFile()){
                        logo=null;
                    }
                }
            }
            List<BookQr> list = new ArrayList<>();
            list.add(bqr);
            //再更新二维码样式和修改二维码名称时候先遍历将已经存在的二维码删除
            this.clearQR(rootPath,list);

            //创建二维码
            list.clear();
            bookQr.setBookId(bqr.getBookId());
            bookQr.setOnwer(bqr.getOnwer());
            list.add(bookQr);
            this.creatQRFile(rootPath, http, width, logo, list,bookQr.getQrType(),request,"");
        }else{
            //根据主键更新不为空的值
            this.bookQrMapper.updateByPrimaryKeySelective(bookQr);
        }
        if(null!=bookQr.getResource() && bookQr.getResource().size()>0){
            //绑定资源关系
            List<Object> resIds = bookQr.getResource();
            Res res = new Res();
            res.setQrId(bookQr.getId());
            Example ex = new Example(Res.class);
            ex.createCriteria().andIn("id", resIds);
            this.resService.updateByExampleSelective(res, ex);
        }

    }

    /**
     * 导出
     * @param bookIds bookIds
     * @param request request
     * @return  文件
     * @throws IOException
     */
    public File export(List<Integer> bookIds,HttpServletRequest request) throws Exception {
        String uuid=UUID.randomUUID().toString();
        JDateTime jDateTime = new JDateTime();

        //递归文件
        List<String> srcFiles = new ArrayList<>();
        for (Integer bookId : bookIds) {
            Book book=this.bookService.selectByPrimaryKey(bookId);
            if (null == book)
                continue;
            //获取需要压缩文件目录列表
//            srcFiles.add(PathUtil.getAppRootPath(request)+QR_ROOT_PATH+getBookNamePath(bookId));

            BookQr qr = new BookQr();
            qr.setBookId(bookId);

            //重新生成pdf时,先查询该书课件二维码下有没有课件 如果没有 不导出该课件二维码;
            Example exam = new Example(BookQr.class);
            exam.createCriteria().andEqualTo("bookId",bookId).andEqualTo("qrType",1);

            List<BookQr>  bookQrList  =  selectByExample(exam);

            if (bookQrList != null && bookQrList.size() > 0) {

                Example example1 = new Example(Res.class);
                example1.createCriteria().andEqualTo("qrId",bookQrList.get(0).getId());
                List<Res> resList = resService.selectByExample(example1);

                //该书没有课件资源  过滤课件二维码的压缩
                if (resList == null || resList.size()==0){
                    qr.setQrType(2);
                }

            }

            List<BookQr> allList=this.bookQrMapper.select(qr);
            List<BookQr> list = new ArrayList<>();
            for(BookQr bookQr : allList){
                if(bookQr.getQrType()==1 || bookQr.getQrType()==2){
                    list.add(bookQr);
                }
            }
            String bookPath = "";
            if (null != list && list.size() > 0) {
                FileRes fileRes = new FileRes();
                fileRes.setUuid(list.get(0).getUrl());
                fileRes =  fileResService.selectOne(fileRes);
//                srcFiles.add(PathUtil.getAppRootPath(request) + fileRes.getPath());
                int lastIndexOf = fileRes.getPath().lastIndexOf(File.separator );
                bookPath = fileRes.getPath().substring(0, lastIndexOf );

//                String pdf = PathUtil.getAppRootPath(request) + QR_ROOT_PATH + getBookNamePath(bookId) + File.separator + book.getName() + ".pdf";
                String pdf = PathUtil.getAppRootPath(request) + bookPath + "/" + book.getName().replace("/"," ") + ".pdf";

                //生成pdf
                Pdf.createQrPdf(list,pdf,PathUtil.getAppRootPath(request),fileResService);
            }else{//为空  删除之前生成的pdf
                File file = new File(PathUtil.getAppRootPath(request) + QR_ROOT_PATH + getBookNamePath(bookId) + File.separator + book.getName().replace("/"," ") + ".pdf");
                if (file.exists()){
                    file.delete();
                }
            }
            srcFiles.add(PathUtil.getAppRootPath(request)+bookPath);
        }

        //导出单个图书 设置文件名为 书名 + 日期+ 时间
        String bookName ="";
        if (bookIds.size() == 1){
            Book book=this.bookService.selectByPrimaryKey(bookIds.get(0));
            bookName = book.getName().replace("/"," ")+"-";
        }
        //生成zip压缩路径
        String filePath= PathUtil.getAppRootPath(request)+"WEB-INF"+File.separator+"zip"+File.separator+bookName+jDateTime.toString("YYYYMMDD")+File.separator+uuid;
        File zip=new File(filePath);
        //创建文件夹
        boolean flag=true;
        if(!zip.exists())
            flag=zip.mkdirs();
        if(!flag) {
            throw new Exception(I18n.getMessage("error"));
        }
        //压缩成zip
        String destZipPath=zip.getPath()+File.separator+bookName+jDateTime.toString("YYYYMMDDhhmmss")+".zip";
        //把课件资源二维码和扩展资源二维码拷贝到与zip相同目录下
        List<String> newSrcFiles = new ArrayList<>();
        if(srcFiles.size()>0){
            for(String qrPath:srcFiles){
                String tempPath = qrPath.substring(qrPath.lastIndexOf(File.separator)+1,qrPath.length());
                String str_bookid = tempPath.substring(tempPath.lastIndexOf("-")+1,tempPath.length());
                String newQrPath = filePath+File.separator+tempPath;
                newSrcFiles.add(newQrPath);
                //创建书籍目录
                File newQrFile = new File(newQrPath);
                if(!newQrFile.exists()){
                    newQrFile.mkdirs();
                }
                //课件二维码和扩展资源二维码分目录
                String mainResPath = newQrPath+File.separator+"课件二维码";
                String addResPath = newQrPath+File.separator+"扩展资源二维码";
                //先查询该书课件二维码下有没有课件 如果有，才去创建 课件二维码文件夹
                File mainResFile = new File(mainResPath);
                System.out.println("tempPath=="+tempPath);
                System.out.println("str_bookid=="+str_bookid);
                long bookId = Long.valueOf(str_bookid);
                Example exam = new Example(BookQr.class);
                exam.createCriteria().andEqualTo("bookId",bookId).andEqualTo("qrType",1);
                List<BookQr>  bookQrList  =  selectByExample(exam);
                if (bookQrList != null && bookQrList.size() > 0) {
                    if (!mainResFile.exists()) {
                        mainResFile.mkdirs();
                    }
                    //循环拷贝课件资源二维码
                    for(BookQr bookQr:bookQrList){
                        String fileUuid = bookQr.getUrl();
                        FileRes fileRes = new FileRes();
                        fileRes.setUuid(fileUuid);
                        List<FileRes> list = fileResService.select(fileRes);
                        if(list!=null &&list.size()!=0){
                            FileRes fres = list.get(0);
                            File file = new File(PathUtil.getAppRootPath(request)+File.separator+fres.getPath());
                            this.copyFile(file, new File(mainResPath + File.separator + file.getName()));
                        }
                    }

                }
                //先查询该书扩展资源二维码下有没有扩展资源 如果有，才去创建 扩展资源二维码文件夹
                File addResFile = new File(addResPath);
                Example examAdd = new Example(BookQr.class);
                examAdd.createCriteria().andEqualTo("bookId",bookId).andEqualTo("qrType",2);
                List<BookQr>  bookQrAddList  =  selectByExample(examAdd);
                if (bookQrAddList != null && bookQrAddList.size() > 0) {
                        if (!addResFile.exists()) {
                            addResFile.mkdirs();
                        }
                        //循环拷贝扩展资源二维码
                    for(BookQr bookQr:bookQrAddList){
                        String fileUuid = bookQr.getUrl();
                        FileRes fileRes = new FileRes();
                        fileRes.setUuid(fileUuid);
                        List<FileRes> list = fileResService.select(fileRes);
                        if(list!=null &&list.size()!=0){
                            FileRes fres = list.get(0);
                            File file = new File(PathUtil.getAppRootPath(request)+File.separator+fres.getPath());
                            this.copyFile(file,new File(addResPath+File.separator+file.getName()));
                        }
                    }

                }

                File root = new File(qrPath);
                File[] files = root.listFiles();
                for(File file:files){
                    String fileName = file.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
                    //拷贝pdf文件放上级目录
                    if ("pdf".equals(suffix)) {
                        this.copyFile(file, new File(newQrPath + File.separator + fileName));
                    }
                }

            }
        }

        ZipUtils.createZip(destZipPath,new ArrayList<String>(), newSrcFiles.toArray());
        File destZip = new File(destZipPath);
        if(destZip.exists()){
            return destZip;
        }
        return null;
    }

    /**
     * 导出刮刮乐二维码zip文件
     *
     * @param qrcodeId 二维码id
     * @param number 倒出二维码数量
     * @param request   request
     * @throws IOException
     */
    public void serialNumExportExcel(int number,Integer qrcodeId,String data,HttpServletRequest request, HttpServletResponse response) throws Exception{
        //获取网址基础信息
        String http = PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //获取bookQr和book
        BookQr bookQr = selectByPrimaryKey(qrcodeId);
        Book book = bookService.selectByPrimaryKey(bookQr.getBookId());
        List<String> list = new ArrayList<>();

        //接受定制信息
        BookQrStyleVo bookQrStyleVo = new BookQrStyleVo();
        try {
            JSONObject userObj = JSONObject.fromObject(data);
            bookQrStyleVo = (BookQrStyleVo)JSONObject.toBean(userObj,BookQrStyleVo.class);
        } catch (JSONException e) {
            throw new ServiceException("参数data解析失败！");
        }
        //获取logo，默认是null
        File logo = null;
        FileRes fileRes = null;
        if (!StringUtils.isBlank(bookQrStyleVo.getFileId())) {
            fileRes = new FileRes();
            fileRes.setUuid(bookQrStyleVo.getFileId());
            fileRes = this.fileResService.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }
        //获取宽度
        int qrWidth = WIDTH;
        if (!StringUtils.isBlank(bookQrStyleVo.getWidth())) {
            qrWidth = Integer.valueOf(bookQrStyleVo.getWidth());
        }
        //先 生成number个刮刮卡二维码,带序列号
        List<String> uuidList = userCenterService.getSerializedUuid(2,number,book.getCode());
        if(uuidList.size() != number){
            throw new ServiceException("出版社序列号中心接口调用失败!");
        }
        //先 生成number个二维码,带序列号
        for(int i=0;i<number;i++){
//            String uuid = UUID.randomUUID().toString();
            String uuid = uuidList.get(i);
            String contents = Constant.GUA_QR_HTTP + bookQr.getId() + "&activation=" + uuid;

            list.add(contents);
        }


        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("book", book);
        beans.put("list", list);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/bookAuthTemplate.xls";
        InputStream in = null;
        OutputStream out = null;
        //构造导出文档名称
        String excelName = "刮刮卡二维码列表_"+book.getCode().trim()+new SimpleDateFormat("_YYYYMMddHHmm").format(new Date());
        String filename= URLEncoder.encode(excelName+".xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String((excelName+".xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplateAuth(book,out, in, null, null, list, "list", Constant.EXCEL_MAX_INDEX*100);
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

        //保存导出记录
        QrExport qrExport = new QrExport();
        qrExport.setNum(number);
        qrExport.setQrId(qrcodeId);
        qrExport.setCreatorId(CurrentUser.getUser().getId());
        qrExport.setCreatorAccount(CurrentUser.getUser().getEmail());
        qrExport.setCreateTime(new Date());
        qrExport.setType(1);
        qrExportMapper.insertSelective(qrExport);
    }

    /**
     * 导出刮刮卡二维码
     * @param number     数量
     * @param qrcodeId   二维码id
     * @return  文件
     * @throws IOException
     */
    public File serialNumExport(int number,Integer qrcodeId,String data,HttpServletRequest request) throws Exception {
        //获取网址基础信息
        String http = PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //获取bookQr和book
        BookQr bookQr = selectByPrimaryKey(qrcodeId);
        Book book = bookService.selectByPrimaryKey(bookQr.getBookId());
        int bookId = book.getId();
        List<BookQr> list = new ArrayList<>();
        //删除( *-gua )文件夹
        String tempPath = getSerialBookNamePath(bookQr.getBookId());

        //接受定制信息
        BookQrStyleVo bookQrStyleVo = new BookQrStyleVo();
        try {
            JSONObject userObj = JSONObject.fromObject(data);
            bookQrStyleVo = (BookQrStyleVo)JSONObject.toBean(userObj,BookQrStyleVo.class);
        } catch (JSONException e) {
            throw new ServiceException("参数data解析失败！");
        }
        //获取logo，默认是null
        File logo = null;
        FileRes fileRes = null;
        if (!StringUtils.isBlank(bookQrStyleVo.getFileId())) {
            fileRes = new FileRes();
            fileRes.setUuid(bookQrStyleVo.getFileId());
            fileRes = this.fileResService.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }
        //获取宽度
        int qrWidth = WIDTH;
        if (!StringUtils.isBlank(bookQrStyleVo.getWidth())) {
            qrWidth = Integer.valueOf(bookQrStyleVo.getWidth());
        }
        //先 生成number个二维码,带序列号
        for(int i=0;i<number;i++){
            int index = i+1;
            String uuid =this.creatSerialQRFile(rootPath, tempPath,qrWidth, logo, bookQr,request,bookQrStyleVo.getText(),index);
            BookQr bq = new BookQr();
            bq.setName(bookQr.getName() + "_" + index + ".png");
            bq.setQrType(3);
            list.add(bq);
        }

        //然后压缩成zip,并返回
        String uuid=UUID.randomUUID().toString();
        JDateTime jDateTime = new JDateTime();

        //递归文件
        List<String> srcFiles = new ArrayList<>();
        List<String> deleteFileName = new ArrayList<>();
        //获取需要压缩文件目录列表
        srcFiles.add(PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath);

        String pdf = PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath + File.separator + book.getName() + ".pdf";
        //生成pdf
        Pdf.createGuaQrPdf(list, pdf, PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath, book);
        //检查pdf生成页数
        PdfReader reader = new PdfReader(pdf);
        int pagecount= reader.getNumberOfPages();
        if(number/100!=pagecount){
            throw new ServiceException("因为网络原因,导出pdf页数不够,请重新再试！");
        }

        //导出单个图书 设置文件名为 书名 + 日期+ 时间
        String bookName = book.getName();
        //生成zip压缩路径
        String filePath= PathUtil.getAppRootPath(request)+"WEB-INF"+File.separator+"zip"+File.separator+bookName+jDateTime.toString("YYYYMMDD")+File.separator+uuid;
        File zip=new File(filePath);
        //创建文件夹
        boolean flag=true;
        if(!zip.exists())
            flag=zip.mkdirs();
        if(!flag) {
            throw new Exception(I18n.getMessage("error"));
        }
        //压缩成zip
        String destZipPath=zip.getPath()+File.separator+bookName+jDateTime.toString("YYYYMMDDhhmmss")+".zip";
        ZipUtils.createZip(destZipPath,deleteFileName, srcFiles.toArray());
        //删除临时文件夹
        String guaPath = rootPath +QR_ROOT_PATH + tempPath;
        File file = new File(guaPath);
        if (file.exists()&&file.isDirectory()) {
            File[] files = file.listFiles();//声明目录下所有的文件 files[];
            for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                if(files[i].exists()&&files[i].isFile())
                files[i].delete();
            }
            file.delete();
        }
        File destZip = new File(destZipPath);
        if(destZip.exists()){
            return destZip;
        }
        return null;
    }

    /**
     * 导出权限二维码
     * @param number     数量
     * @param qrcodeId   二维码id
     * @return  文件
     * @throws IOException
     */
    public File authNumExport(int number,Integer qrcodeId,String data,HttpServletRequest request) throws Exception {
        //获取网站根目录
        String rootPath = PathUtil.getAppRootPath(request);
        //获取BookQrAuth和book
        BookQrAuth bookQrAuth = bookQrAuthMapper.selectByPrimaryKey(qrcodeId);
        // BookQrAuth转换为bookQr
        BookQr bookQr = new BookQr(bookQrAuth);
        Book book = bookService.selectByPrimaryKey(bookQr.getBookId());

        List<BookQr> list = new ArrayList<>();
        //生成临时存储权限二维码的文件夹
        String tempPath = getAuthBookNamePath(bookQr.getBookId());

        //接受定制信息
        BookQrStyleVo bookQrStyleVo = new BookQrStyleVo();
        try {
            JSONObject userObj = JSONObject.fromObject(data);
            bookQrStyleVo = (BookQrStyleVo)JSONObject.toBean(userObj,BookQrStyleVo.class);
        } catch (JSONException e) {
            throw new ServiceException("参数data解析失败！");
        }
        //获取logo，默认是null
        File logo = null;
        FileRes fileRes = null;
        if (!StringUtils.isBlank(bookQrStyleVo.getFileId())) {
            fileRes = new FileRes();
            fileRes.setUuid(bookQrStyleVo.getFileId());
            fileRes = this.fileResService.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }
        //先 生成number个权限二维码,带序列号
        for(int i=0;i<number;i++){
            int index = i+1;
            String uuid =this.creatAuthQRFile(rootPath, tempPath, WIDTH, logo, bookQr,request,bookQrStyleVo.getText(),index);
            BookQr bq = new BookQr();
            bq.setName(bookQr.getName() + "_" + index + ".png");
            bq.setQrType(4);
            list.add(bq);
        }

        //然后压缩成zip,并返回
        String uuid=UUID.randomUUID().toString();
        JDateTime jDateTime = new JDateTime();

        //递归文件
        List<String> srcFiles = new ArrayList<>();
        List<String> deleteFileName = new ArrayList<>();
        //获取需要压缩文件目录列表
        srcFiles.add(PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath);

        String pdf = PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath + File.separator + book.getName() + ".pdf";
        //生成pdf
        Pdf.createAuthQrPdf(list, pdf, PathUtil.getAppRootPath(request) + QR_ROOT_PATH + tempPath, book);
        //检查pdf生成页数
        PdfReader reader = new PdfReader(pdf);
        int pagecount= reader.getNumberOfPages();
        if(number/100!=pagecount){
            throw new ServiceException("因为网络原因,导出pdf页数不够,请重新再试！");
        }

        //导出单个图书 设置文件名为 书名 + 日期+ 时间
        String bookName = book.getName();
        //生成zip压缩路径
        String filePath= PathUtil.getAppRootPath(request)+"WEB-INF"+File.separator+"zip"+File.separator+bookName+jDateTime.toString("YYYYMMDD")+File.separator+uuid;
        File zip=new File(filePath);
        //创建文件夹
        boolean flag=true;
        if(!zip.exists())
            flag=zip.mkdirs();
        if(!flag) {
            throw new Exception(I18n.getMessage("error"));
        }
        //压缩成zip
        String destZipPath=zip.getPath()+File.separator+bookName+jDateTime.toString("YYYYMMDDhhmmss")+".zip";
        ZipUtils.createZip(destZipPath,deleteFileName, srcFiles.toArray());
        //删除临时文件夹
        String guaPath = rootPath +QR_ROOT_PATH + tempPath;
        File file = new File(guaPath);
        if (file.exists()&&file.isDirectory()) {
            File[] files = file.listFiles();//声明目录下所有的文件 files[];
            for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                if(files[i].exists()&&files[i].isFile())
                    files[i].delete();
            }
            file.delete();
        }

        File destZip = new File(destZipPath);
        if(destZip.exists()){
            return destZip;
        }
        return null;
    }

    /**
     * 导出
     * @param bookQrIds bookQrIds
     * @param request request
     * @return  文件
     * @throws IOException
     */
    public File exportQrcode(List bookQrIds,Integer bookId,HttpServletRequest request) throws Exception {
        String uuid=UUID.randomUUID().toString();
        JDateTime jDateTime = new JDateTime();
        //递归文件
        List<String> srcFiles = new ArrayList<>();
        List<String> deleteFileName = new ArrayList<>();

        Book book=this.bookService.selectByPrimaryKey(bookId);
        if (null == book)
            return null;


        //获取需要压缩文件目录列表
//        srcFiles.add(PathUtil.getAppRootPath(request)+QR_ROOT_PATH+getBookNamePath(bookId));

        //根据图书ID查询出该书籍下所有二维码信息

        //先查询该书课件二维码 添加到过滤列表不导出该课件二维码
        Example exam = new Example(BookQr.class);
        exam.createCriteria().andEqualTo("bookId",bookId).andNotIn("id",bookQrIds);
        List<BookQr>  bookQrList  =  selectByExample(exam);
        if (bookQrList != null && bookQrList.size() > 0) {
            for (BookQr bookQr:  bookQrList) {
                FileRes fileRes = new FileRes();
                fileRes.setUuid(bookQr.getUrl());
                fileRes =  fileResService.selectOne(fileRes);
//                deleteFileName.add(fileRes.getPath());
//                srcFiles.add(fileRes.getPath());
            }
        }

        Example exam1 = new Example(BookQr.class);
        exam1.createCriteria().andEqualTo("bookId",bookId).andIn("id",bookQrIds);
        List<BookQr> list=this.bookQrMapper.selectByExample(exam1);
        String bookPath = "";
        for (BookQr bookQr : list) {
            FileRes fileRes = new FileRes();
            fileRes.setUuid(bookQr.getUrl());
            fileRes =  fileResService.selectOne(fileRes);
            srcFiles.add(PathUtil.getAppRootPath(request) + fileRes.getPath());
            int lastIndexOf = fileRes.getPath().lastIndexOf(File.separator );
//            int lastIndexOf = fileRes.getPath().lastIndexOf("/");
            bookPath = fileRes.getPath().substring(0, lastIndexOf );
        }

//        List<BookQr> list= new ArrayList<>();
        if (null != list && list.size() > 0) {
//            String pdf = PathUtil.getAppRootPath(request) + QR_ROOT_PATH + getBookNamePath(bookId) + File.separator + book.getName() + ".pdf";
//            String pdf = PathUtil.getAppRootPath(request) + bookPath + File.separator + book.getName() + ".pdf";
            String pdf = PathUtil.getAppRootPath(request) + bookPath + "/" + book.getName().replace("/"," ") + ".pdf";
            srcFiles.add(pdf);
            System.out.println("======++++++++++++++++++======="+ pdf);
            //生成pdf
            Pdf.createQrPdf(list,pdf,PathUtil.getAppRootPath(request),fileResService);
        }else{//为空  删除之前生成的pdf
            File file = new File(PathUtil.getAppRootPath(request) + QR_ROOT_PATH + getBookNamePath(bookId) + File.separator + book.getName().replace("/"," ") + ".pdf");
            if (file.exists()){
                file.delete();
            }
        }
        //导出单个图书 设置文件名为 书名 + 日期+ 时间
        String bookName =book.getName().replace("/"," ")+"-";
        //生成zip压缩路径
        String filePath= PathUtil.getAppRootPath(request)+"WEB-INF"+File.separator+"zip"+File.separator+bookName+jDateTime.toString("YYYYMMDD")+File.separator+uuid;
        File zip=new File(filePath);
        //创建文件夹
        boolean flag=true;
        if(!zip.exists())
            flag=zip.mkdirs();
        if(!flag) {
            throw new Exception(I18n.getMessage("error"));
        }
        //压缩成zip
        String destZipPath=zip.getPath()+File.separator+bookName+jDateTime.toString("YYYYMMDDhhmmss")+".zip";
        ZipUtils.createZip(destZipPath,deleteFileName, srcFiles.toArray());
        File destZip = new File(destZipPath);
        if(destZip.exists()){
            return destZip;
        }
        return null;

    }


    /**
     * 首次新增或者修改二维码的样式时候可以调用
     * @param rootPath 存储文件的根路径地址 如：D:\\tomcat\\webapps\\project\\
     * @param http      网站路径地址 http://www.cnds.net:80/project
     * @param widthAndHeight 二维码的宽度和高度
     * @param logo  二维码logo，可以是null
     * @param bookQrList 要更新的二维码列表
     */
    public void creatQRFile(String rootPath,String http,int widthAndHeight,File logo,List<BookQr> bookQrList,int qrType, HttpServletRequest request,String text) throws Exception {
        String fart = "png";
        for (BookQr bookQr : bookQrList) {
            String relativePath=QR_ROOT_PATH+getBookNamePath(bookQr.getBookId());
            String root=rootPath+relativePath;
            // String contents=http+"/qrcode/detail?id="+bookQr.getId();
            String contents = "";
            if (qrType == 1){
                contents= Constant.COURSE_QR_HTTP+bookQr.getId();

            }else if(qrType == 2){
                contents= Constant.BASE_QR_HTTP+bookQr.getId();
            }else if(qrType == 3){
                contents= Constant.GUA_QR_HTTP+bookQr.getId();
            }
            //System.out.println("二维码地址："+contents);
            File file = null;
            if(qrType == 3){
                file = new File(root,bookQr.getName()+"_gua."+fart);
            }else{
                file = new File(root,bookQr.getName()+"."+fart);
            }
            if(!file.getParentFile().exists()) {
                boolean b=file.getParentFile().mkdirs();
                if(!b) {
                    throw new Exception(I18n.getMessage("error"));
                }
            }


            if (StringUtils.isNotBlank(bookQr.getUrl())) {

               /* Res res = new Res();
                res.setQrId(bookQr.getId());
                res = resService.selectOne(res);

                FileRes fileRes = new FileRes();
                fileRes.setUuid(res.getFileUuid());

                 fileRes = this.fileResService.selectOne(fileRes);*/
                bookQr.setName(bookQr.getName());
            }
            //判断文件是否存在，存在则名称加编号
            int i=1;
            String fileName = bookQr.getName();
            while(file.exists() && (qrType == 2 || qrType == 1)) {
                fileName=bookQr.getName()+"("+i+")";
                file = new File(root,fileName+"."+fart);
                i++;
            }
            bookQr.setName(fileName);
            String url= "";
            if(qrType == 3){
                url=relativePath+File.separator+fileName+"_gua."+fart;
            }else{
                url=relativePath+File.separator+fileName+"."+fart;
            }
            String uuid= UUID.randomUUID().toString();
            bookQr.setUrl(uuid);
            this.bookQrMapper.updateByPrimaryKeySelective(bookQr);
            //二维码
            //创建FILERES
            FileRes fileRes = new FileRes();
            fileRes.setUuid(uuid);
            fileRes.setName(fileName);
            fileRes.setPath(url);
            fileRes.setSuffix(fart);
            fileRes.setSize(0);
            this.fileResService.insert(fileRes);


//            https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx91127260dd5e0e88&redirect_uri=http%3A%2F%2Fwww.tupmbook.com%2Fpreview.html%3Fid%3D1660&response_type=code&scope=snsapi_userinfo&state=852147999#wechat_redirect
//             add by liukailong 20160921
//            引导用户进入授权页面同意授权，获取code
//            在原来的contents上面加上微信的一些东西
//            contents = URLEncoder.encode(contents,"utf-8");//TODO
//            contents = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ Constant.WEIXIN_APPID + "&redirect_uri="+ contents + "&response_type=code&scope=snsapi_userinfo&state=852147999#wechat_redirect";//TODO
            //生成二维码
            QREncode.encode_QR_CODE(contents, widthAndHeight, widthAndHeight, fart, file, logo,request,text);
        }
    }

    /**
     * 生成刮刮卡二维码
     * @param rootPath 存储文件的根路径地址 如：D:\\tomcat\\webapps\\project\\
     * @param tempPath      临时文件夹
     * @param widthAndHeight 二维码的宽度和高度
     * @param logo  二维码logo，可以是null
     * @param bookQr 要更新的二维码列表
     */
    private String creatSerialQRFile(String rootPath,String tempPath,int widthAndHeight,File logo,BookQr bookQr, HttpServletRequest request,String text,int number) throws Exception {
        String fart = "png";
        String relativePath = QR_ROOT_PATH + tempPath;
        String root = rootPath + relativePath;
        // String contents=http+"/qrcode/detail?id="+bookQr.getId();
        String uuid = UUID.randomUUID().toString();
        String contents = Constant.GUA_QR_HTTP + bookQr.getId() + "&activation=" + uuid;
        //System.out.println("二维码地址："+contents);
        File file = new File(root, bookQr.getName() + "_" + number + "." + fart);
        if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) {
                throw new Exception(I18n.getMessage("error"));
            }
        }

//            https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx91127260dd5e0e88&redirect_uri=http%3A%2F%2Fwww.tupmbook.com%2Fpreview.html%3Fid%3D1660&response_type=code&scope=snsapi_userinfo&state=852147999#wechat_redirect
//             add by liukailong 20160921
//            引导用户进入授权页面同意授权，获取code
//            在原来的contents上面加上微信的一些东西
//        contents = URLEncoder.encode(contents, "utf-8");//TODO
//        contents = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constant.WEIXIN_APPID + "&redirect_uri=" + contents + "&response_type=code&scope=snsapi_userinfo&state=852147999#wechat_redirect";//TODO
        //生成二维码
        QREncode.encode_QR_CODE(contents, widthAndHeight, widthAndHeight, fart, file, logo, request, text);

        return uuid;
    }

    /**
     * 生成权限二维码
     * @param rootPath 存储文件的根路径地址 如：D:\\tomcat\\webapps\\project\\
     * @param tempPath      临时文件夹
     * @param widthAndHeight 二维码的宽度和高度
     * @param logo  二维码logo，可以是null
     * @param bookQr 要更新的二维码列表
     */
    private String creatAuthQRFile(String rootPath,String tempPath,int widthAndHeight,File logo,BookQr bookQr, HttpServletRequest request,String text,int number) throws Exception {
        String fart = "png";
        String relativePath = QR_ROOT_PATH + tempPath;
        String root = rootPath + relativePath;
        String uuid = UUID.randomUUID().toString();
        String contents = Constant.authHttp + bookQr.getId() + "&authkey=" + uuid;
        //System.out.println("二维码地址："+contents);
        File file = new File(root, bookQr.getName() + "_" + number + "." + fart);
        if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) {
                throw new Exception(I18n.getMessage("error"));
            }
        }

        //生成二维码
        QREncode.encode_QR_CODE(contents, widthAndHeight, widthAndHeight, fart, file, logo, request, text);

        return uuid;
    }

    /**
     * 用于删除已经存在的二维码文件
     * @param rootPath   根路径
     * @param bookQrList 二维码列表
     */
    private void clearQR(String rootPath,List<BookQr> bookQrList){
        for (BookQr bookQr : bookQrList) {
            FileRes fileRes = new FileRes();
            fileRes.setUuid(bookQr.getUrl());
            fileRes =this.fileResService.selectOne(fileRes);
            String root = rootPath + fileRes.getPath();
            File file = new File(root);
            if (file.exists()) {
                boolean b=file.delete();
                if(b)
                    this.fileResService.deleteByPrimaryKey(fileRes.getId());
            }
        }
    }

    /**
     * 生成图书名称和作者文件路径信息
     * @param bookId bookId
     * @return 返回图书路径名称
     */
    public String getBookNamePath(Integer bookId) {
        Book book = this.bookService.selectByPrimaryKey(bookId);
        //return File.separator + book.getName() + "-" + book.getAuthor()+ "-" + book.getId();
        return File.separator + book.getId();
    }

    /**
     * 生成图书名称和作者文件路径信息(用于刮刮卡)
     * @param bookId bookId
     * @return 返回图书路径名称
     */
    public String getSerialBookNamePath(Integer bookId) {
        String uuid = "刮刮卡二维码";
        Book book = this.bookService.selectByPrimaryKey(bookId);
        return getBookNamePath(bookId)+ "-gua" + File.separator +uuid;
    }

    /**
     * 生成图书名称和作者文件路径信息(用于权限二维码)
     * @param bookId bookId
     * @return 返回图书路径名称
     */
    public String getAuthBookNamePath(Integer bookId) {
        String uuid = "权限二维码";
        Book book = this.bookService.selectByPrimaryKey(bookId);
        return getBookNamePath(bookId)+ "-auth" + File.separator +uuid;
    }

    /**
     * 生成图书名称和作者文件路径
     * @param bookId bookId
     * @return 返回图书路径名称
     */
    public void creatBookPath(Integer bookId,HttpServletRequest request)throws Exception{
        Book book = this.bookService.selectByPrimaryKey(bookId);
        File file=new File(PathUtil.getAppRootPath(request) + QR_ROOT_PATH+getBookNamePath(bookId) );
        //生成课件二维码
//        saveBookQR(new BookQRAddVO(bookId, bookService.selectByPrimaryKey(bookId).getName(), 1), request);
        //生成刮刮乐二维码
        saveBookQR(new BookQRAddVO(bookId, book.getName(), 3), request);
        //生成权限二维码(只在t_qr_auth表中插入一条记录,不需要生成二维码图片)
        BookQrAuth bookQrAuth = new BookQrAuth();
        bookQrAuth.setBookId(bookId);
        bookQrAuth.setName(book.getName());
        bookQrAuth.setOnwer(book.getOnwer());
        bookQrAuthMapper.insertSelective(bookQrAuth);

        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 获取课件或者刮刮乐二维码明细   包括课件列表   课件二维码显示url
     * @param id
     * @param activation 如果为 "",代表课件；否则代表刮刮乐
     * @return
     * @author hesufang
     * @date 2015.11.3
     */
    public CoursewareDetailVO coursewareDetail(int id,String activation,String openid,String code,String state){
        if(!"".equals(activation) && activation != null && !"".equals(openid) && openid != null){
            //刮刮乐二维码
            //查找qrUserInfo
            Example example = new Example(QrUserInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("activation",activation);
            List<QrUserInfo> qrUserInfoList = qrUserInfoService.selectByExample(example);
            QrUserInfo qrUserInfo = null;
            if(qrUserInfoList!=null && qrUserInfoList.size()!=0){
                qrUserInfo = qrUserInfoList.get(0);
                //如果查出的openid和用户openid不相同,证明这个二维码已被扫过
                if(!openid.equals(qrUserInfo.getOpenid())){
                    return null;
                }
            }else{
                //绑定关系
                Example example1 = new Example(QrUserInfo.class);
                Example.Criteria criteria1 = example1.createCriteria();
                criteria1.andEqualTo("id",qrUserInfo.getId());
                qrUserInfo.setActivation(activation);
                qrUserInfoService.updateByExampleSelective(qrUserInfo,example1);
            }
        }

        //查找课件二维码信息
        BookQr bookQr = this.bookQrMapper.selectByPrimaryKey(id);
        if (null == bookQr){
            log.warn("课件二维码不存在id为"+id);
            throw new ServiceException("课件二维码不存在");
        }
        //根据图书id查找图书信息
        Book book = this.bookService.selectByPrimaryKey(bookQr.getBookId());
        if(!StringUtils.isBlank(code) && !StringUtils.isBlank(state) && !StringUtils.isBlank(openid)){
            // add by liukailong 20160912 begin
            //二维码扫描次数+1
            if(bookQr.getViewNum() == null){
                bookQr.setViewNum(1);
            }else{
                bookQr.setViewNum(bookQr.getViewNum()+1);
            }
            this.updateByPrimaryKeySelective(bookQr);
            // add by liukailong 20160912 end

            // add by liukailong 20160912 begin
            //二维码所属book扫描次数+1
            book.setViewNum(book.getViewNum()+1);
            bookService.updateByPrimaryKeySelective(book);
            // add by liukailong 20160912 end
        }

        //图书信息转换
        CoursewareDetailVO detailVO = mapper.map(book, CoursewareDetailVO.class);

        //查询设置课件二维码图片地址
        detailVO.setUrl(bookQr.getUrl());
        detailVO.setNetUrl(bookQr.getNetUrl());

        //设置书问地址
        detailVO.setBookaskUrl(book.getBookaskUrl());
        //打包压缩状态
        detailVO.setZipStatus(bookQr.getZipStatus());
        detailVO.setOnlyWebchat(book.getOnlyWebchat());
        //查询下载审核提示
        AuditorMail mail = bookService.getEmail(bookQr.getBookId());
        if (mail != null){

            detailVO.setTip(mail.getTip());
        }

        //获取课件资源列表
        //1.查询出该课件二维码下所有课件uuid
        Example example1 = new Example(Res.class);
        example1.createCriteria().andEqualTo("qrId",id);
        List<Res> resList = this.resService.selectByExample(example1);
        if (resList==null || resList.size()<1){
            throw  new ServiceException("资源不存在");
        }

        //2.根据uuid 查询所有资源
        List<FileRes> fileReses = fileResService.getFileResList(resList);
        if(fileReses != null && fileReses.size()>0){
            for(FileRes file :fileReses){
                file.setFileId(file.getUuid());
                //file.setDownLoad();
                file.setPath(file.getZhixueid().toString());
                file.setFileName(file.getName());
                if(FileUtil.isSupportVideo(file.getSuffix())){
                    //视频下载要用七牛云id
                    file.setDownUrl(file.getQizhixueid().toString());
                }else{
                    file.setDownUrl(file.getViewurl());
                }
            }
        }
        detailVO.setResource(fileReses);
        //设置是否保密
        detailVO.setCheck(bookQr.getSecrecy());
        detailVO.setSendCustomer(bookQr.getIsSend());

        // 20160930 add by liukailong begin
        detailVO.setOpenId(openid);
        Example example = new Example(Client.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openid);
        List<Client> clientList = clientMapper.selectByExample(example);
        if(clientList!=null && clientList.size()>0){
            if(!StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("2");
                detailVO.setOpenEmail(clientList.get(0).getEmail());
            }else{
                detailVO.setOpenStatus("");
                detailVO.setOpenEmail("");
            }
        }else{
            if(StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("");
            }else{
                detailVO.setOpenStatus("1");
            }
            detailVO.setOpenEmail("");
        }
        // 20160930 add by liukailong end

        return  detailVO;
    }


    /**
     * 图书课件二维码设置
     * @param setlVO
     * @author hesufang
     * @date 2015.11.4
     */
    public void setDetail(CoursewareSetlVO setlVO){
        //1.查找图书对应的课件二维码
        BookQr bookQr = new BookQr();
        bookQr.setBookId(setlVO.getId());
        bookQr.setQrType(1);
        bookQr  =  bookQrMapper.selectOne(bookQr);

        //该书没有课件二维码
        if (bookQr == null){
            return;
        }

        Example example = new Example(Res.class);
        example.createCriteria().andEqualTo("qrId",bookQr.getId());

        Res res = new Res();
        res.setSecrecy(setlVO.getCheck());
        res.setIsSend(setlVO.getSendCustomer());
        resService.updateByExampleSelective(res,example);

    }

    /**
     * 图书课件二维码设置(已废弃)
     * @param vo
     * @author cuihao
     * @date 2017.5.15
     */
    public void sets(CoursewareSetNewVO vo){
        String ids = vo.getIds();
        String[] arrIds = ids.split(",");
        List<Object> idList = Arrays.asList(arrIds);

        Example example = new Example(BookQr.class);
        example.createCriteria().andIn("id",idList);

        BookQr bookQr = new BookQr();
        bookQr.setSecrecy(vo.getCheck());
        bookQr.setIsSend(vo.getSendCustomer());
        bookQrMapper.updateByExampleSelective(bookQr,example);
    }

    /**
     * 查询图书课件二维码是自动审核还是人工审核
     * @param bookId
     * @return 0:没有课件二维码 1:人工审核 2:自动审核
     * @author cuihao
     * @date 2017.5.15
     */
    public int getCheckStatus(Integer bookId){
        BookQr bookQrEP = new BookQr();
        bookQrEP.setQrType(1);
        bookQrEP.setBookId(bookId);
        List<BookQr> bookQrList = bookQrMapper.select(bookQrEP);
        if(bookQrList==null || bookQrList.size()==0){
            return 0;
        }else{
            BookQr bookQr = bookQrList.get(0);
            return bookQr.getIsSend();
        }
    }

    /**
     * 图书课件二维码设置
     * @param vo
     * @author cuihao
     * @date 2017.5.15
     */
    public JsonResult setExamineVo(SetExamineVo vo){
        Example example = new Example(BookQr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bookId",vo.getBookId());
        criteria.andEqualTo("qrType",Constant.ONE);

        BookQr bookQr = new BookQr();
        bookQr.setSecrecy(Constant.TWO);//默认保密
        bookQr.setIsSend(vo.getFlag());
        bookQrMapper.updateByExampleSelective(bookQr,example);

        // 1人工2自动
        if (vo.getFlag() == 2) {
            //人工改成自动审核那么把审核列表中这本图书的待审核置为审核通过
            Example exampleCr = new Example(ClientAttr.class);
            Example.Criteria criteriaCr = exampleCr.createCriteria();
            criteriaCr.andEqualTo("bookId",vo.getBookId());
            criteriaCr.andEqualTo("status",1);

            ClientAttr clientAttr = new ClientAttr();
                clientAttr.setStatus(4);
            clientAttrMapper.updateByExampleSelective(clientAttr,exampleCr);
        }else{
            //自动改成人工审核,那么把审核列表中这本图书的自动审核通过置为人工审核通过
            Example exampleCr = new Example(ClientAttr.class);
            Example.Criteria criteriaCr = exampleCr.createCriteria();
            criteriaCr.andEqualTo("bookId",vo.getBookId());
            criteriaCr.andEqualTo("status",4);

            ClientAttr clientAttr = new ClientAttr();
            clientAttr.setStatus(2);
            clientAttrMapper.updateByExampleSelective(clientAttr,exampleCr);
        }

        return new JsonResult(true, "设置成功");
    }

    /**
     * 根据bookid查询
     */
    public List<BookQr>  getListByBookId(int bookId){
        return bookQrMapper.getListByBookId(bookId);
    }

    /**
     * 查询二维码列表
     *
     * @param keyword
     * @param tab
     * @param page
     * @param pageSize
     * @return
     */
    public JsonResultPage getBookQrList(String keyword, Integer tab, int bookId, int page, int pageSize) {
        Example example = new Example(BookQr.class);
        example.setOrderByClause("id DESC");
        Example.Criteria criteria = example.createCriteria();
        //权限验证
//        DataPermission.hasPermission(BookQr.class, Method.QUERY, this, criteria);
        if (!StringUtils.isBlank(keyword)) {
            criteria.andLike("name", "%" + keyword + "%");
        }
        if (bookId != 0) {
            criteria.andEqualTo("bookId", bookId);
        }
        if (tab != 0) {
            criteria.andEqualTo("qrType", tab);
        }
        RowBounds rowBounds = new RowBounds(page, pageSize);
        JsonResultPage jsonResultPage = selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<BookQr> bookQrList = jsonResultPage.getItems();
        if (bookQrList.size() == 0) {
            return jsonResultPage;
        }

        Iterator<BookQr> it = bookQrList.iterator();
        while (it.hasNext()) {
            BookQr bookQr = it.next();

            //如果不是网站链接形式的二维码,需要判断其下有没有资源，如果没有，不返回
            if (StringUtils.isEmpty(bookQr.getNetUrl())) {
                //按照资源index从小到大次序查询
                Example exampleRes = new Example(Res.class);
                exampleRes.setOrderByClause("indexs asc");
                Example.Criteria criteriaRes = exampleRes.createCriteria();
                criteriaRes.andEqualTo("qrId", bookQr.getId());
                List<Res> list = resService.selectByExample(exampleRes);
                List<Object> objectList = new ArrayList<>();
                if (list != null && list.size() != 0) {
                    for (Res rs : list) {
                        FileRes fr = new FileRes();
                        fr.setUuid(rs.getFileUuid());
                        FileRes fileRes = fileResMapper.selectOne(fr);
                        if (fileRes != null) {
                            rs.setSuffix(fileRes.getSuffix());
                            rs.setFileName(fileRes.getName());
                            rs.setFileId(String.valueOf(fileRes.getId()));
                            rs.setSize(fileRes.getSize());
                            //rs.setDownUrl(fileRes.getQizhixueid().toString());
                            if (FileUtil.isSupportVideo(fileRes.getSuffix())) {
                                //视频下载要用七牛云id
                                rs.setDownUrl(fileRes.getQizhixueid().toString());
                            } else {
                                String viewurl = fileRes.getViewurl();
                                if(viewurl.contains("http://cms.izhixue.cn")){
                                    String arr[] = viewurl.split("=");
                                    viewurl = arr[1];
                                }
                                rs.setDownUrl(viewurl);
                            }
                            //预览用阿里云id
                            rs.setPath(fileRes.getZhixueid().toString());
                        }
                    }
                    objectList.addAll(list);
                    bookQr.setResource(objectList);
                } else {
                    //如果该二维码下面没有资源则不显示
                    it.remove();
                }
            }
            bookQr.setSendCustomer(bookQr.getIsSend());
        }

        return new JsonResultPage(new Page(jsonResultPage.getTotal(), page, pageSize, bookQrList));
    }

    /**
     * 复制一个文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    private void copyFile(File src, File dest) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];

        int length;

        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }

    /**
     * 限制读取设置， 根据二维码ID，限制扩展资源二维码资源的读取！
     * 1 直接读取，2 限制读取
     * 判断该扩展资源二维码可读属性值
     * 如果是 1 改成 2 的时候，同时更新 Book 的属性值 isPublication 改成 2
     * 如果是 2 改成 1 的时候，检查该图书下的扩展资源二维码 canRead 是否有 2 如果有，不更新 Book isPublication 的值, 如果没有，则更新 Book 的属性值 isPublication 为 1
     * @param bookQrParamVO
     * @return
     * @autor zml
     */
    @Transactional
    public JsonResult editExtendedQr(BookQrParamVO bookQrParamVO) {
        // 准备数据
        String qrId = bookQrParamVO.getQrId();
        String[] qrIds = qrId.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : qrIds) {
            if (!StringUtils.isEmpty(id)){
                idList.add(Integer.parseInt(id));
            }
        }
        // 批量更新
        for (Integer id : idList) {
            int flag = bookQrParamVO.getFlag();
            BookQr bookQr = bookQrMapper.selectByPrimaryKey(id);
            // 获取图书对象
            Book book = bookMapper.selectByPrimaryKey(bookQr.getBookId());
            if (flag == 2) { // 1 改成 2
                if (bookQr.getCanRead() == 1) {
                    bookQr.setCanRead(2);
                    bookQrMapper.updateByPrimaryKeySelective(bookQr);
                }
                if (book.getIsPublication() == 1) {
                    book.setIsPublication(2);
                    bookMapper.updateByPrimaryKeySelective(book);
                }
            }
            if (flag == 1) { // 2 改成 1
                if (bookQr.getCanRead() == 2) {
                    bookQr.setCanRead(1);
                    bookQrMapper.updateByPrimaryKeySelective(bookQr);
                }
                if (getCountBookQrByCanRead(bookQr.getBookId(), 2) == 0) {
                    book.setIsPublication(1);
                    bookMapper.updateByPrimaryKeySelective(book);
                }
            }
        }
        return new JsonResult(true, "更新成功！");
    }

    /**
     * 统计图书下是否还有限制读取的资源
     * 返回索要统计的可读扩展资源二维码数量
     * 1 直接读取，2 限制读取
     * @param bookId
     * @param canRead
     *
     * @return
     */
    public Integer getCountBookQrByCanRead(int bookId, int canRead){
        if (bookId == 0 || canRead == 0) {
            log.error("统计图书下扩展资源二维码的可读性时，出现错误！");
            throw new ServiceException("统计图书下扩展资源二维码的可读性时，出现错误！");
        }
        Example example = new Example(BookQr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bookId", bookId);
        criteria.andEqualTo("qrType", 2);   // 2 代表扩展资源的二维码
        criteria.andEqualTo("canRead", canRead);
        return bookQrMapper.selectCountByExample(example);
    }

    /**
     * 根据 bookId 查询该书下的二维码下的资源是否为空， 为空返回 true， 不为空返回 false
     * @param bookId
     * @return
     */
    public boolean getBookQrResIsEmpty(Integer bookId) {
        Example example = new Example(BookQr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("bookId", bookId);
        criteria.andEqualTo("qrType", 1);
        List<BookQr> bookQrList = bookQrMapper.selectByExample(example);
        if (bookQrList.size() == 0){
            return true;
        }
        for (BookQr bookQr : bookQrList) {
            Example resE = new Example(Res.class);
            Example.Criteria resC = resE.createCriteria();
            resC.andEqualTo("qrId", bookQr.getId());
            List<Res> resList = resService.selectByExample(resE);
            if (resList.size() != 0) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public JsonResultData<BookQrDetailVO> bookQrOtherDetail(Integer id, String code, String state) {
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(id);
        if(bookQr == null){
            throw new ServiceException("该二维码有问题，请联系管理员反馈错误");
        }
        Book bk = new Book();
        bk.setId(bookQr.getBookId());
        Book book = bookService.selectOne(bk);
        BookQrDetailVO detailVO = new BookQrDetailVO();
        detailVO.setCanRead(bookQr.getCanRead());
        //二维码浏览量+1
        if (bookQr.getViewNum() == null) {
            bookQr.setViewNum(1);
        } else {
            bookQr.setViewNum(bookQr.getViewNum() + 1);
        }
        this.updateByPrimaryKeySelective(bookQr);
        //二维码所属book浏览量+1
        book.setViewNum(book.getViewNum()+1);
        bookService.updateByPrimaryKeySelective(book);
        //保存qruserinfo,浏览器扫描也要统计扫描量,给一个默认的openid值(exploreropenid)
        QrUserInfo qrUserInfo = new QrUserInfo();
        qrUserInfo.setType(3);
        qrUserInfo.setQrId(id);
        qrUserInfo.setOpenid("exploreropenid");
        qrUserInfo.setCreateTime(new Date());
        qrUserInfoService.insertSelective(qrUserInfo);

        //查询下载审核提示
        AuditorMail mail = bookService.getEmail(bookQr.getBookId());
        if (mail != null){
            detailVO.setTip(mail.getTip());
        }
        detailVO.setSendCustomer(bookQr.getIsSend());
        detailVO.setName(bookQr.getName());
//        detailVO.setNote(bookQr.getNote());
        detailVO.setUrl(bookQr.getUrl());
        detailVO.setNetUrl(bookQr.getNetUrl());
        detailVO.setBookName(book.getName());
        detailVO.setBookaskUrl(book.getBookaskUrl());
        detailVO.setCategoryId(book.getCategoryId());
        detailVO.setOnlyWebchat(book.getOnlyWebchat());
        detailVO.setAuthor(book.getAuthor());
        detailVO.setCode(book.getCode());
        detailVO.setPress(book.getPress());
        detailVO.setRemark(book.getRemark());
        detailVO.setZipStatus(bookQr.getZipStatus());
        //查询出对应的资源信息 set到VO
        Example example1 = new Example(Res.class);
        example1.setOrderByClause("indexs asc");
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("qrId", bookQr.getId());

        List<Res> resList=resService.selectByExample(example1);
        for (Res res1 : resList) {
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid",res1.getFileUuid());
            List<FileRes> fileResList =  this.fileResService.selectByExample(ep);
            if (null == fileResList) continue;
            for (FileRes fr : fileResList){
                res1.setFileId(res1.getFileUuid());
                res1.setSuffix(fr.getSuffix());
                res1.setFileName(fr.getName());
                res1.setPath(fr.getZhixueid().toString());
                if(FileUtil.isSupportVideo(fr.getSuffix())){
                    res1.setDownUrl(fr.getQizhixueid().toString());
                }else{
                    res1.setDownUrl(fr.getViewurl());
                }
                res1.setSize(fr.getSize());
                res1.setCreateTime(fr.getCreateTime());
            }
        }
        detailVO.setResource(resList);
        JsonResultData<BookQrDetailVO> json;
        json = new JsonResultData<>(true, I18n.getMessage("success"), detailVO);
        return json;
    }

    @Transactional
    public JsonResultData<BookQrDetailVO> bookQrQQDetail(Integer id, String code, String state, String qrAppId, String qrAppKey, String qqCallBackUrl,HttpServletRequest request) throws Exception{
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(id);
        if(bookQr == null){
            throw new ServiceException("该二维码有问题，请联系管理员反馈错误");
        }
        Book bk = new Book();
        bk.setId(bookQr.getBookId());
        Book book = bookService.selectOne(bk);
        BookQrDetailVO detailVO = new BookQrDetailVO();
        String openid = "";
        //用户同意QQ授权
        if(!StringUtils.isBlank(code) && !StringUtils.isBlank(state)){
            openid = qrUserInfoService.saveQQUserInfo(id, code, qrAppId, qrAppKey, qqCallBackUrl);
            if(!StringUtils.isBlank(openid)){
                // QQ接入出版社用户中心
                userCenterService.userCenter(2, openid,request);
                // zml 170731 begin
                // 获取权限二维码的ID,如果是资源是限制读取且没有扫描对应图书的权限二维码,则此次扫描不算数
                int qrAuthId = bookQrAuthService.getIdByBookId(book.getId());
                detailVO.setRightStatus(qrUserInfoService.getRightStatus(2, qrAuthId, openid));
                detailVO.setCanRead(bookQr.getCanRead());
                // zml 170732 end

                // add by liukailong 20160912 begin
                if (detailVO.getCanRead() != 2 || detailVO.getRightStatus() != 1) {
                    //二维码浏览量+1
                    if (bookQr.getViewNum() == null) {
                        bookQr.setViewNum(1);
                    } else {
                        bookQr.setViewNum(bookQr.getViewNum() + 1);
                    }

                    this.updateByPrimaryKeySelective(bookQr);
                    //二维码所属book浏览量+1
                    book.setViewNum(book.getViewNum()+1);
                    bookService.updateByPrimaryKeySelective(book);
                }else {
                    //无效扫描,需要把QrUserInfo中 id和openid 最新的一条删除掉
                    System.out.println("*******************无效扫描");
                    Example example = new Example(QrUserInfo.class);
                    example.setOrderByClause("id desc");
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("qrId",id);
                    criteria.andEqualTo("openid",openid);
                    criteria.andEqualTo("type", 2);
                    List<QrUserInfo> list = qrUserInfoService.selectByExample(example);
                    if(list!=null && list.size()!=0){
                        qrUserInfoService.delete(list.get(0));
                    }
                }

                // add by liukailong 20160912 end
            }else{
                return new JsonResultData(false,"网络环境不好,请重新扫描!");
            }

        }

        //查询下载审核提示
        AuditorMail mail = bookService.getEmail(bookQr.getBookId());
        if (mail != null){
            detailVO.setTip(mail.getTip());
        }
        detailVO.setSendCustomer(bookQr.getIsSend());
        detailVO.setName(bookQr.getName());
//        detailVO.setNote(bookQr.getNote());
        detailVO.setUrl(bookQr.getUrl());
        detailVO.setNetUrl(bookQr.getNetUrl());
        detailVO.setBookName(book.getName());
        detailVO.setBookaskUrl(book.getBookaskUrl());
        detailVO.setCategoryId(book.getCategoryId());
        detailVO.setOnlyWebchat(book.getOnlyWebchat());
        detailVO.setAuthor(book.getAuthor());
        detailVO.setCode(book.getCode());
        detailVO.setPress(book.getPress());
        detailVO.setRemark(book.getRemark());
        detailVO.setZipStatus(bookQr.getZipStatus());
        //查询出对应的资源信息 set到VO
        Example example1 = new Example(Res.class);
        example1.setOrderByClause("indexs asc");
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("qrId", bookQr.getId());

        List<Res> resList=resService.selectByExample(example1);
        for (Res res1 : resList) {
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid",res1.getFileUuid());
            List<FileRes> fileResList =  this.fileResService.selectByExample(ep);
            if (null == fileResList) continue;
            for (FileRes fr : fileResList){
                res1.setFileId(res1.getFileUuid());
                res1.setSuffix(fr.getSuffix());
                res1.setFileName(fr.getName());
                res1.setPath(fr.getZhixueid().toString());
                if(FileUtil.isSupportVideo(fr.getSuffix())){
                    res1.setDownUrl(fr.getQizhixueid().toString());
                }else{
                    res1.setDownUrl(fr.getViewurl());
                }
                res1.setSize(fr.getSize());
                res1.setCreateTime(fr.getCreateTime());
            }
        }
        detailVO.setResource(resList);

        // 20160930 add by liukailong begin
        detailVO.setOpenId(openid);
        Example example = new Example(Client.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openid);
        List<Client> clientList = clientMapper.selectByExample(example);
        if(clientList!=null && clientList.size()>0){
            if(!StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("2");
                detailVO.setOpenEmail(clientList.get(0).getEmail());
            }else{
                detailVO.setOpenStatus("");
                detailVO.setOpenEmail("");
            }
        }else{
            if(StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("");
            }else{
                detailVO.setOpenStatus("1");
            }
            detailVO.setOpenEmail("");
        }
        // 20160930 add by liukailong end
        JsonResultData<BookQrDetailVO> json;
        json = new JsonResultData<>(true, I18n.getMessage("success"), detailVO);
        return json;
    }

    /**
     * 微信扫码
     * @param id
     * @param code
     * @param state
     * @return
     */
    @Transactional
    public JsonResultData<BookQrDetailVO> bookQrWeiXinDetail(Integer id, String code, String state,HttpServletRequest request) throws Exception{
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(id);
        if(bookQr == null){
            throw new ServiceException("该二维码有问题，请联系管理员反馈错误");
        }
        Book bk = new Book();
        bk.setId(bookQr.getBookId());
        Book book = bookService.selectOne(bk);
        BookQrDetailVO detailVO = new BookQrDetailVO();
        String openid = "";
        String unionid = "";
        //用户同意微信授权
        if(!StringUtils.isBlank(code) && !StringUtils.isBlank(state)){//TODO
            String openidPointUnionid  = qrUserInfoService.qrUserInfoSave(id,code,"");//TODO
            if(!StringUtils.isBlank(openidPointUnionid)){
                //接入出版社用户中心
                String phone = userCenterService.userCenter(1, openidPointUnionid,request);
                detailVO.setPhone(phone);
                openid = openidPointUnionid.split(",")[0];
                unionid = openidPointUnionid.split(",")[1];
                // zml 170731 begin
                // 获取权限二维码的ID,如果是资源是限制读取且没有扫描对应图书的权限二维码,则此次扫描不算数
                int qrAuthId = bookQrAuthService.getIdByBookId(book.getId());
                detailVO.setRightStatus(qrUserInfoService.getRightStatus(1, qrAuthId, openid));
                detailVO.setCanRead(bookQr.getCanRead());
                // zml 170732 end

                // add by liukailong 20160912 begin
                System.out.println("detailVO.getCanRead()=="+detailVO.getCanRead());
                System.out.println("detailVO.getRightStatus()=="+detailVO.getRightStatus());
                if (detailVO.getCanRead() != 2 || detailVO.getRightStatus() != 1) {
                    //二维码浏览量+1
                    if (bookQr.getViewNum() == null) {
                        bookQr.setViewNum(1);
                    } else {
                        bookQr.setViewNum(bookQr.getViewNum() + 1);
                    }

                    this.updateByPrimaryKeySelective(bookQr);
                    //二维码所属book浏览量+1
                    book.setViewNum(book.getViewNum()+1);
                    bookService.updateByPrimaryKeySelective(book);
                }else{
                    //无效扫描,需要把QrUserInfo中 id和openid 最新的一条删除掉
                    System.out.println("*******************无效扫描");
                    Example example = new Example(QrUserInfo.class);
                    example.setOrderByClause("id desc");
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("qrId",id);
                    criteria.andEqualTo("openid",openid);
                    List<QrUserInfo> list = qrUserInfoService.selectByExample(example);
                    if(list!=null && list.size()!=0){
                        qrUserInfoService.delete(list.get(0));
                    }
                }

                // add by liukailong 20160912 end
            }else{
                return new JsonResultData(false,"网络环境不好,请重新扫描!");
            }

        }//TODO


        //查询下载审核提示
        AuditorMail mail = bookService.getEmail(bookQr.getBookId());
        if (mail != null){
            detailVO.setTip(mail.getTip());
        }
        detailVO.setSendCustomer(bookQr.getIsSend());
        detailVO.setName(bookQr.getName());
//        detailVO.setNote(bookQr.getNote());
        detailVO.setUrl(bookQr.getUrl());
        detailVO.setNetUrl(bookQr.getNetUrl());
        detailVO.setBookName(book.getName());
        detailVO.setBookaskUrl(book.getBookaskUrl());
        detailVO.setCategoryId(book.getCategoryId());
        detailVO.setOnlyWebchat(book.getOnlyWebchat());
        detailVO.setAuthor(book.getAuthor());
        detailVO.setCode(book.getCode());
        detailVO.setPress(book.getPress());
        detailVO.setRemark(book.getRemark());
        detailVO.setZipStatus(bookQr.getZipStatus());
        //非网站链接二维码才去查询资源
        if(StringUtils.isEmpty(bookQr.getNetUrl())){
            //查询出对应的资源信息 set到VO
            Example example1 = new Example(Res.class);
            example1.setOrderByClause("indexs asc");
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("qrId", bookQr.getId());

            List<Res> resList=resService.selectByExample(example1);
            for (Res res1 : resList) {
                Example ep = new Example(FileRes.class);
                ep.createCriteria().andEqualTo("uuid",res1.getFileUuid());
                List<FileRes> fileResList =  this.fileResService.selectByExample(ep);
                if (null == fileResList) continue;
                for (FileRes fr : fileResList){
                    res1.setFileId(res1.getFileUuid());
                    res1.setSuffix(fr.getSuffix());
                    res1.setFileName(fr.getName());
                    res1.setPath(fr.getZhixueid().toString());
                    if(FileUtil.isSupportVideo(fr.getSuffix())){
                        res1.setDownUrl(fr.getQizhixueid().toString());
                    }else{
                        res1.setDownUrl(fr.getViewurl());
                    }
                    res1.setSize(fr.getSize());
                    res1.setCreateTime(fr.getCreateTime());
                }
            }
            detailVO.setResource(resList);
        }
        // 20160930 add by liukailong begin
        detailVO.setOpenId(openid);
        detailVO.setUnionid(unionid);
        Example example = new Example(Client.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openid);
        List<Client> clientList = clientMapper.selectByExample(example);
        if(clientList!=null && clientList.size()>0){
            if(!StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("2");
                detailVO.setOpenEmail(clientList.get(0).getEmail());
            }else{
                detailVO.setOpenStatus("");
                detailVO.setOpenEmail("");
            }
        }else{
            if(StringUtils.isBlank(openid)){
                detailVO.setOpenStatus("");
            }else{
                detailVO.setOpenStatus("1");
            }
            detailVO.setOpenEmail("");
        }
        // 20160930 add by liukailong end
        JsonResultData<BookQrDetailVO> json;
        json = new JsonResultData<>(true, I18n.getMessage("success"), detailVO);
        return json;
    }

    /**
     * 导出权限二维码
     * @author lsn
     * @param editor
     * @param isbns
     * @param keyword
     * @param pressId
     * @param type
     */
    public void exportList(Integer editor,String isbns,String keyword,int pressId,int type,int categoryId,int categorysuperId, HttpServletRequest request, HttpServletResponse response)throws Exception{
            // 查询 Book 表
            Example example = new Example(Book.class);
            example.setOrderByClause("id DESC");
            Example.Criteria criteria = example.createCriteria();
            Example.Criteria criteria2 = example.createCriteria();
            Example.Criteria criteria3 = example.createCriteria();
            // 1、模糊查询图书
            if (!org.springframework.util.StringUtils.isEmpty(keyword)) {
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
                    if (!org.springframework.util.StringUtils.isEmpty(code)){
                        codeList.add(code);
                    }
                }
                if (codeList.size() == 1){
                    criteria.andLike("code", "%" + codeList.get(0) + "%");
                    criteria2.andLike("code", "%" + codeList.get(0) + "%");
                    criteria3.andLike("code", "%" + codeList.get(0) + "%");
                }else if (codeList.size()!=0){
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

        if (categoryId!=0){
            criteria.andEqualTo("categoryId",categoryId);
            criteria2.andEqualTo("categoryId",categoryId);
            criteria3.andEqualTo("categoryId",categoryId);
        }
        if (categorysuperId!=0){
            criteria.andEqualTo("categorySuperId",categorysuperId);
            criteria2.andEqualTo("categorySuperId",categorysuperId);
            criteria3.andEqualTo("categorySuperId",categorysuperId);
        }

            List<PowerQrVo> powerQrVoList=new ArrayList<>();
            List<Book> bookList=bookMapper.selectByExample(example);

            for (Book books:bookList){
            BookQrAuth QrAuth = new BookQrAuth();
            QrAuth.setBookId(books.getId());
            List<BookQrAuth> bookQrAuthList = bookQrAuthMapper.select(QrAuth);
            if(bookQrAuthList==null || bookQrAuthList.size()==0)
                throw new ServiceException("没有查询到此图书的权限二维码!");

            BookQrAuth bookQrAuth = bookQrAuthList.get(0);
            Example ex = new Example(QrExport.class);
            ex.setOrderByClause("createTime desc");
            Example.Criteria Qrcriteria = ex.createCriteria();
            Qrcriteria.andEqualTo("qrId",bookQrAuth.getId());
            Qrcriteria.andEqualTo("type",2);
            List<QrExport> list = this.qrExportMapper.selectByExample(ex);
            if(list==null || list.size()==0){
                PowerQrVo powerQrVo=new PowerQrVo();
                powerQrVo.setBookId(books.getId());
                powerQrVo.setCode(books.getCode());
                powerQrVo.setName(books.getName());
                powerQrVo.setCategory(books.getCategory());
                powerQrVo.setQuoteId("是");
                powerQrVo.setPress(books.getPress());
                powerQrVo.setEditor(books.getEditor());
                powerQrVo.setAuthor(books.getAuthor());

                powerQrVoList.add(powerQrVo);
            }else {
                for(QrExport qrExport:list){
                    PowerQrVo powerQrVo=new PowerQrVo();
                    powerQrVo.setBookId(books.getId());
                    powerQrVo.setCode(books.getCode());
                    powerQrVo.setName(books.getName());
                    powerQrVo.setCategory(books.getCategory());
                    powerQrVo.setQuoteId("是");
                    powerQrVo.setPress(books.getPress());
                    powerQrVo.setEditor(books.getEditor());
                    powerQrVo.setAuthor(books.getAuthor());
                    powerQrVo.setOprator(qrExport.getCreatorAccount());
                    powerQrVo.setNum(qrExport.getNum());
                    powerQrVo.setCreateTime(qrExport.getCreateTime());

                    powerQrVoList.add(powerQrVo);
                }
            }
            }
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("powerQrVoList", powerQrVoList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + (powerQrVoList.size() != 0 ? "freemarker/powerQrVoListScan.xls" : "freemarker/bookListScanNoData.xls");
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("权限二维码.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("权限二维码.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, powerQrVoList, "powerQrVoList", Constant.EXCEL_MAX_INDEX);

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
     * 书问教师认证
     * @return
     */
    public String getBookAskToken(String mobile,String bookcode) throws Exception{
        String source = "yunpan";
        String exp =String.valueOf(System.currentTimeMillis() + 3600*1000).substring(0,10);

        WqjiaoxueVo vo = new WqjiaoxueVo();
        vo.setMobile(mobile);
        vo.setBookcode(bookcode);
        vo.setSource(source);
        vo.setExp(exp);
        vo.setReturnUrl(returnUrl);

        String jsonParams = JSON.toJSONString(vo);
        System.out.println("jsonParams:"+jsonParams);

        String token = PayEncrypt.encryptMode(key, jsonParams).replace("\n","");
        System.out.println("解密结果：" + PayEncrypt.decryptMode(key, token));
        String sendUrl = loginUrl + "?token=" + token;
        System.out.println("跳转地址："+sendUrl);
//        response.sendRedirect(sendUrl);

        return sendUrl;
    }

    /**
     * 生成resourceDetail.html的sign
     */
    public JsonResultData getHtmlSign() {
        String sign = Base64.getBase64(backFrontKey + "-" + System.currentTimeMillis()/1000);
        System.out.println("sign:"+sign);
        SignVo vo = new SignVo();
        vo.setSign(sign);

        return new JsonResultData<>(vo);
    }

    /**
     * 校验resourceDetail.html是否失效
     */
    public JsonResultData checkHtmlSign(String sign) {
        String originalSign = Base64.getFromBase64(sign);
        System.out.println("originalSign:" + originalSign);
        if (!originalSign.contains(backFrontKey)) {
            return new JsonResultData<>(false, "sign参数错误!");
        }
        String arr[] = originalSign.split("-");
        String strLastTime = arr[1];
        Pattern pattern = Pattern.compile("[0-9]*");
        //判断是否由纯数字组成
        if(!pattern.matcher(strLastTime).matches()){
            return new JsonResultData<>(false, "sign参数错误!");
        }
        long lastTime = Long.valueOf(arr[1]);
        long nowTime = System.currentTimeMillis();
        System.out.println("lastTime:"+lastTime+" nowTime:"+nowTime);
        long duration = nowTime - lastTime;
        if (duration > backFrontTimeout*1000 || duration<- backFrontTimeout*1000) {
            System.out.println("时间:"+duration+" 链接失效");
            return new JsonResultData<>(new CheckSignVo(2));
        } else {
            System.out.println("时间:"+duration+" 链接有效");
            return new JsonResultData<>(new CheckSignVo(1));
        }
    }

    /**
     * 给申请人发邮件
     * @author liukailong
     * 2016-10-13
     */
    public void sendEmailToClient(BookQr bookQr,Client client)throws Exception{
        DocumentEmailHandler mdoc = new DocumentEmailHandler();
        List<BookQRFile> bqFileList=new ArrayList<BookQRFile>();
        BookQRFile bookQRFile = null;
        List<FileRes> resList=resService.getFileResesByQrId(bookQr.getId());
        for (FileRes fileRes:resList) {
            bookQRFile=new BookQRFile();
            bookQRFile.setName(fileRes.getName());
            bookQRFile.setSize(FileUtil.convertFileSize(fileRes.getSize()));
            bqFileList.add(bookQRFile);
        }

        // 客户信息生成内容模版
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("book", bookService.selectByPrimaryKey(bookQr.getBookId()));
        dataMap.put("bqList", bqFileList);
        dataMap.put("client", client);

        //预览地址
        dataMap.put("url",Constant.COURSE_QR_HTTP.replaceAll("coursewareOne","coursewareThr")+bookQr.getId()+"&email="+client.getEmail()+"&openId="+client.getOpenId());
        String context =  mdoc.createHTML(dataMap) ;

        String subject = "文泉云盘移动阅读平台审核信息";
        String[] toMail = new String[]{client.getEmail()};

        mailSender.sendRichTextMail(context, subject, toMail);

    }

}
