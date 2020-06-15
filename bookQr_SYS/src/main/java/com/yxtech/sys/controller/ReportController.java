package com.yxtech.sys.controller;

import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.dao.ReportMapper;
import com.yxtech.sys.dao.ResMapper;
import com.yxtech.sys.domain.Report;
import com.yxtech.sys.domain.Res;
import com.yxtech.sys.service.ReportService;
import com.yxtech.sys.vo.ReportMailVo;
import com.yxtech.sys.vo.ReportVo;
import com.yxtech.utils.excel.DocumentResTucaoHandler;
import com.yxtech.utils.file.StringUtil;
import com.yxtech.utils.mail.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chenxh on 2015/9/22.
 */

@RestController
@Scope("prototype")
@RequestMapping(value = "/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private ResMapper resMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private MailSender mailSender;

    @Value("#{configProperties['complaints.auto.email']}")
    private String mail;

    /**
     * 新增举报
     * @param reportVo
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public JsonResult add(@RequestBody ReportVo reportVo)throws Exception {
        Assert.isTrue(reportVo.getId()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));
        Assert.isTrue(reportVo.getId()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "资源id"));
        Assert.isTrue(reportVo.getQrcodeId()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.notNull(reportVo.getRemark(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "说明"));

        Res res = resMapper.selectByPrimaryKey(reportVo.getId());
        if(res==null)
            return new JsonResult(false,"这个资源已不存在!");

        Report report = new Report();
        report.setEmail(reportVo.getEmail());
        report.setFileId(reportVo.getId());
        report.setQrcodeId(reportVo.getQrcodeId());

        Integer mytype=reportVo.getReportType();
        if (mytype == 4) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("type", "吐槽");
            dataMap.put("email", report.getEmail());
            dataMap.put("detail", reportVo);
            DocumentResTucaoHandler mdoc = new DocumentResTucaoHandler();
            String context =  mdoc.createHTML(dataMap) ;
            String subject = "文泉云盘移动阅读平台反馈信息";
            String[] toMail = new String[]{mail};
            //发送邮件
            mailSender.sendRichTextMail(context, subject, toMail);
            report.setStatus(1);
            reportMapper.updateByPrimaryKeySelective(report);
        }
        report.setType(mytype);



        report.setRemark(reportVo.getRemark());
        report.setCreateTime(new Date());

        reportService.insertSelective(report);

        return new JsonResult(true,"新增反馈成功");
    }


    /**
     * 获取举报信息的书籍列表
     * @param pageNo  页码
     * @param pageSize  条数
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/books",method = RequestMethod.GET)
    public JsonResultPage getbooks(@RequestParam(value = "keyword",defaultValue = "",required = false) String keyword,
                                   @RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                   @RequestParam(value = "pressId", defaultValue = "0") int pressId) {

        Assert.isTrue(pageNo>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "当前页"));
        Assert.isTrue(pageSize>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "每页数量"));

        return reportService.getbooks(pressId, StringUtil.escape4Like(keyword),pageNo,pageSize);
    }

    /**
     * 获取某本书的举报信息
     * @param pageNo  页码
     * @param pageSize  条数
     * @param bookId  书籍id
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JsonResultPage getList(@RequestParam(value = "bookId") int bookId,
                                  @RequestParam(value = "keyword",defaultValue = "",required = false) String keyword,
                                  @RequestParam(value = "status",defaultValue = "0") int status,
                                  @RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Assert.isTrue(bookId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书籍id"));

        return reportService.getList(bookId,keyword,status,pageNo,pageSize);
    }

    /**
     * 导出某本书的反馈信息
     * @param bookId  书籍id
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/exportReportList",method = RequestMethod.GET)
    public void exportReportList(@RequestParam(value = "bookId") int bookId,HttpServletRequest request,HttpServletResponse response) {

        Assert.isTrue(bookId>=0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "书籍id"));

        reportService.exportReportList(bookId,request,response);
    }

    /**
     * 转发邮箱
     * @param reportMailVo
     * @return
     * @author cuihao
     */
    @RequestMapping(value = "/sendEmail",method = RequestMethod.POST)
    public JsonResult sendEmail(@RequestBody ReportMailVo reportMailVo) throws Exception{
        Assert.isTrue(reportMailVo.getId()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "举报ID"));
        Assert.notNull(reportMailVo.getEmail(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));


        reportService.sendEmail(reportMailVo);

        return new JsonResult(true,"转发成功!");
    }



}
