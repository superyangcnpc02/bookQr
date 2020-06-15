package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.dao.ReportMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.ReportListVo;
import com.yxtech.sys.vo.ReportMailVo;
import com.yxtech.sys.vo.resource.ResourceDetail;
import com.yxtech.sys.vo.resource.ResourceMailVo;
import com.yxtech.utils.excel.DocumentResHandler;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.mail.MailSender;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/27.
 */
@Service
public class ReportService extends BaseService<Report>{
    private ReportMapper reportMapper;

    @Autowired
    private BookQrService bookQrService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ResService resService;

    @Autowired
    private FileResMapper fileResMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private FileResService fileResService;
    @Autowired
    private MailSender mailSender;

    @Resource(name = "reportMapper")
    public void setReportMapper(ReportMapper reportMapper) {
        setMapper(reportMapper);
        this.reportMapper = reportMapper;
    }

    /**
     *
     * @param bookId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getList(int bookId,String keyword, int status,int pageNo, int pageSize) {
        //查看这本书是否存在
        Book book = null;
        //根据bookId查找二维码列表
        Example exampleBq = new Example(BookQr.class);
        exampleBq.selectProperties("id");
        Example.Criteria criteria = exampleBq.createCriteria();
        if(bookId!=0){
            criteria.andEqualTo("bookId",bookId);
            book =bookService.selectByPrimaryKey(bookId);
        }
        List<BookQr> qrList = bookQrService.selectByExample(exampleBq);
        if(qrList == null || qrList.size() == 0 ){
            System.out.println("qrList 为空!");
            return new JsonResultPage(new Page()) ;
        }

        List<Object> qrIdList = new ArrayList<>();
        for(BookQr bookQr :qrList){
            qrIdList.add(bookQr.getId());
        }

        //通过keyword查找
        List<Object> resIdList = new ArrayList<>();
        if(!"".equals(keyword)){
            resIdList = fileResMapper.findResByName(keyword);
            if(resIdList==null || resIdList.size()==0){
                return new JsonResultPage(new Page()) ;
            }
        }

        //查找举报列表
        Example exampleReport = new Example(Report.class);
        exampleReport.setOrderByClause("id desc");
        Example.Criteria criteriaReport = exampleReport.createCriteria();
        criteriaReport.andIn("qrcodeId",qrIdList);
        if (status!=0){
            criteriaReport.andEqualTo("status",status);
        }
        if(!"".equals(keyword)){
            criteriaReport.andIn("fileId",resIdList);
        }

        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        JsonResultPage jsonResultPage = this.selectByExampleAndRowBounds2JsonResultPage(exampleReport, rowBounds);

        List<Report> reportList = jsonResultPage.getItems();
        if (null == reportList || Constant.ZERO == reportList.size()) {
            return jsonResultPage;
        }

        //转换前端
        List<ReportListVo> reportListVos = new ArrayList<>();
        ResourceDetail resourceDetail = null;
        for (Report report : reportList) {
            BookQr bookQr = bookQrService.selectByPrimaryKey(report.getQrcodeId());
            ReportListVo reportListVo = new ReportListVo();
            reportListVo.setEmail(report.getEmail());
            reportListVo.setId(report.getId());
            reportListVo.setCreateTime(report.getCreateTime());
            //书籍id和书籍名称
            if(bookId==0){
                book = bookService.selectByPrimaryKey(bookQr.getBookId());
            }
            reportListVo.setBookId(book.getId());
            reportListVo.setBookName(book.getName());
            reportListVo.setStatus(report.getStatus());
            reportListVo.setQrId(report.getQrcodeId());
            reportListVo.setTag(bookQr.getQrType());
            reportListVo.setQrName(bookQr.getName());
            //资源名称
            Res res = resService.selectByPrimaryKey(report.getFileId());
            //文件
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid",res.getFileUuid());
            List<FileRes> fileResList = this.fileResService.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0 ){
                resourceDetail = new ResourceDetail(fileResList.get(0));
                resourceDetail.setId(res.getId());
                resourceDetail.setText(res.getText());
                resourceDetail.setOnwer(res.getOnwer());
                resourceDetail.setNum(res.getNum());
            }

            reportListVo.setResourceDetail(resourceDetail);
            Integer type=report.getType();
            reportListVo.setReportType(type);
            //简介
            reportListVo.setRemark(report.getRemark());

            reportListVos.add(reportListVo);
        }

        Page page = new Page(jsonResultPage.getTotal(),jsonResultPage.getNum(),jsonResultPage.getSize(),reportListVos);
        return new JsonResultPage(page);
    }
    /**
     *
     * @param pressId
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getbooks(int pressId,String keyword, int pageNo, int pageSize) {
        //当前登录用户
        User user = CurrentUser.getUser();
        int role = user.getRole();

        int offset = (pageNo-1)*pageSize;
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("offset",offset);
        map.put("pageSize",pageSize);
        map.put("keyword",keyword);
        map.put("pressId",pressId);

        //如果是编辑员,只能查看属于自己的书籍
        if(role==1){
            map.put("onwer",user.getId());
        }else{
            map.put("onwer","");
        }

        List<Book> list = bookMapper.selectReportBooks(map);
        int total = bookMapper.countReportBooks(map);
        System.out.println("total:"+total);

        List<Book> resultList = new ArrayList<>();
        for(Book book:list){
            Book bookTarget = bookMapper.selectByPrimaryKey(book.getId());
            if(bookTarget!=null){
                resultList.add(bookTarget);
            }
        }

        Page page = new Page(total,pageNo,pageSize,resultList);

        return new JsonResultPage(page);
    }

    /**
     *
     * @param bookId
     * @return
     */
    public void exportReportList(int bookId, HttpServletRequest request, HttpServletResponse response) {
        //查看这本书是否存在
        Book book = null;
        //根据bookId查找二维码列表
        Example exampleBq = new Example(BookQr.class);
        exampleBq.selectProperties("id");
        Example.Criteria criteria = exampleBq.createCriteria();
        if(bookId!=0){
            criteria.andEqualTo("bookId",bookId);
            book =bookService.selectByPrimaryKey(bookId);
        }
        List<BookQr> qrList = bookQrService.selectByExample(exampleBq);
        if(qrList == null || qrList.size() == 0 ){
            return;
        }

        List<Object> qrIdList = new ArrayList<>();
        for(BookQr bookQr :qrList){
            qrIdList.add(bookQr.getId());
        }

        //查找举报列表
        Example exampleReport = new Example(Report.class);
        Example.Criteria criteriaReport = exampleReport.createCriteria();
        criteriaReport.andIn("qrcodeId",qrIdList);


        List<Report> reportList = reportMapper.selectByExample(exampleReport);
        if (null == reportList || Constant.ZERO == reportList.size()) {
            throw new ServiceException("无数据");
        }

        //转换前端
        List<ReportListVo> reportListVos = new ArrayList<>();
        ResourceDetail resourceDetail = null;
        for (Report report : reportList) {
            BookQr bookQr = bookQrService.selectByPrimaryKey(report.getQrcodeId());
            ReportListVo reportListVo = new ReportListVo();
            reportListVo.setId(report.getId());
            //书籍id和书籍名称
            if(bookId==0){
                book = bookService.selectByPrimaryKey(bookQr.getBookId());
            }
            reportListVo.setBookId(book.getId());
            reportListVo.setBookName(book.getName());
            reportListVo.setQrId(report.getQrcodeId());
            reportListVo.setTag(bookQr.getQrType());
            reportListVo.setTagName(bookQr.getQrType()==1?"课件二维码":"扩展资源二维码");
            reportListVo.setQrName(bookQr.getName());
            //资源名称
            Res res = resService.selectByPrimaryKey(report.getFileId());
            //文件
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid",res.getFileUuid());
            List<FileRes> fileResList = this.fileResService.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0 ){
                resourceDetail = new ResourceDetail(fileResList.get(0));
                resourceDetail.setId(res.getId());
                resourceDetail.setText(res.getText());
                resourceDetail.setOnwer(res.getOnwer());
                resourceDetail.setNum(res.getNum());
            }

            reportListVo.setResourceDetail(resourceDetail);
            reportListVo.setResourceName(resourceDetail==null?"":resourceDetail.getName());
            reportListVo.setReportType(report.getType());
            String  resTypeName="";//1内容反馈 .2版权申诉 .3不良信息举报
            if(report.getType()==1){
                resTypeName="内容反馈";
            }else if (report.getType()==2){
                resTypeName="版权申诉";
            }else{
                resTypeName="不良信息举报";
            }
            reportListVo.setResTypeName(resTypeName);
            //简介
            reportListVo.setRemark(report.getRemark());
            if(report.getStatus()==1){
                reportListVo.setStatusInfo("已处理");
            }else{
                reportListVo.setStatusInfo("未处理");
            }
            reportListVo.setCreateTime(report.getCreateTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            reportListVo.setReportTime(simpleDateFormat.format(report.getCreateTime()));
            reportListVo.setEmail(report.getEmail());

            reportListVos.add(reportListVo);
        }

        //列表为空
        if (reportListVos == null || reportListVos.size() == 0) {
            throw new ServiceException("无数据");
        }
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("reportList", reportListVos);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/reportListTemplate.xls";
        InputStream in = null;
        OutputStream out = null;
        try {
            String fileName = URLEncoder.encode("反馈列表.xls" , "utf-8"); //为了解决中文名称乱码问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                fileName = new String("反馈列表.xls".getBytes("UTF-8"), "iso-8859-1");
            }
            response.setHeader("Content-disposition", "attachment;filename="+fileName+"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, reportListVos, "reportList", Constant.EXCEL_MAX_INDEX);
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
     *
     * @param reportMailVo
     */
    public void sendEmail(ReportMailVo reportMailVo)throws Exception{
        int reportId = reportMailVo.getId();  //举报id
        String email = reportMailVo.getEmail(); //转发邮箱

        //获取资源详情
        Report report = selectByPrimaryKey(reportId);
        ResourceMailVo resourceMailVo = resService.getByResId(report.getFileId());
        resourceMailVo.setRemark(report.getRemark());

        //发送邮件
        // 客户信息生成内容模版
        Map<String, Object> dataMap = new HashMap<String, Object>();
        if(report.getType() == Constant.ONE){
            dataMap.put("type", "内容反馈");
        }
        if(report.getType() == Constant.TWO){
            dataMap.put("type", "版权申诉");
        }
        if(report.getType() == 3){
            dataMap.put("type", "不良信息举报");
        }
        dataMap.put("email", report.getEmail());
        dataMap.put("detail", resourceMailVo);
        dataMap.put("toUrl", Constant.emailReportHttp + resourceMailVo.getId()+"&openId=&qrcodeId="+resourceMailVo.getQrId());
        DocumentResHandler mdoc = new DocumentResHandler();

        String context =  mdoc.createHTML(dataMap) ;
        String subject = "文泉云盘移动阅读平台反馈信息";

        String[] toMail = new String[]{email};


        //发送邮件
        mailSender.sendRichTextMail(context, subject, toMail);
        report.setStatus(1);
        reportMapper.updateByPrimaryKeySelective(report);

    }
}
