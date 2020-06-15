package com.yxtech.sys.controller;

import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.Mail;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.SendEmailVo;
import com.yxtech.sys.vo.bookQr.MailVo;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.StringUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.mail.MailSender;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hesufang on 2015/10/16.
 */

@RestController("MailController")
@Scope("prototype")
@RequestMapping(value = "/email")
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private ClientAttrService clientAttrService;

    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private MailSender mailSender;
    @Value("#{configProperties['email.downPreUrl']}")
    private String downPreUrl;

    /**
     * 获取邮箱列表
     * @param bookId  图书id
     * @param pageNo  页码
     * @param pageSize  条数
     * @param keyword  关键字
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JsonResultPage getMailList(@RequestParam(value = "pressId",defaultValue = "0",required = false) int pressId,
                                      @RequestParam(value = "id",defaultValue = "0",required = false) int bookId,
                               @RequestParam(value = "page" ,defaultValue = "1") int pageNo,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "keyword") String keyword) {

        Assert.isTrue(bookId >= 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书ID"));

        return mailService.getMailsByBookID(bookId,pressId,pageNo,pageSize,StringUtil.escape4Like(keyword));
    }


    /**
     * 删除邮箱
     * @param mailIds  邮箱t_mail_book_count id  list集合
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public JsonResult deleteMaills(@RequestParam(value = "id") List<Integer> mailIds,@RequestParam(value = "bookId") Integer bookId) {
        Assert.isTrue(bookId >= 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书ID"));
        Assert.isTrue(mailIds!=null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱id"));

        mailService.deleteMaills(mailIds,bookId);
        return new JsonResult(true,"删除邮箱成功");
    }


    /**
     * 导出邮箱列表
     * @param id   图书id
     * @param request
     * @param response
     */
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void export(@RequestParam("id")int id,HttpServletRequest request,HttpServletResponse response) throws IOException{
        Assert.isTrue(id >= 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书ID"));

        //查询数据
        List<MailVo> mailList = mailService.getMailListByBookID(id);

        //列表为空
        if (mailList==null || mailList.size()==0){
            throw  new ServiceException("无数据");
        }


        Map<String,Object> beans = new HashMap<String,Object>();
        beans.put("mailList", mailList);

        //模版配置文件路径
        String templateFileName= PathUtil.getAppRootPath(request)+"WEB-INF"+ File.separator+"classes"+File.separator+ "freemarker/mailTemplate.xls";


        InputStream in=null;
        OutputStream out=null;

        String filename= URLEncoder.encode("邮箱列表.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("邮箱列表.xls").getBytes("UTF-8"), "iso-8859-1");
        }

        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");

            in=new BufferedInputStream(new FileInputStream(templateFileName));
            out=response.getOutputStream();

            ExcelUtil.generateExcelByTemplate(out, in, null, null, mailList, "mailList", Constant.EXCEL_MAX_INDEX) ;
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if(in != null) {
                try {
                    in.close();
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


    /**
     *发送邮件
     * @param sendEmailVo
     * @return
     */
    @RequestMapping(value = "/collection",method = RequestMethod.POST)
    public JsonResult sendEmaill(@RequestBody SendEmailVo sendEmailVo)throws  Exception{
        String url = downPreUrl + sendEmailVo.getUrl();
        System.out.println("----------------------url=="+url);
        if(!StringUtil.validateEmail(sendEmailVo.getEmail())){
            return new JsonResult(false,"邮箱格式不正确!");
        }

      //查找二维码对应的书籍
       BookQr bookQr =  bookQrService.selectByPrimaryKey(sendEmailVo.getId());

        String subject = "文泉云盘移动阅读平台";
        String context = "感谢使用文泉云盘移动阅读平台 <a href='"+url+"'>【"+bookQr.getName()+"】下载</a>";
        String[] toMail = new String[]{sendEmailVo.getEmail()};
        try {
            mailSender.sendRichTextMail(context, subject, toMail);

            //向数据库添加记录  1.表示发送邮件次数
            mailService.addMailAndCount(bookQr, sendEmailVo.getEmail(),1);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult(false,"邮件发送失败");
        }
        return new JsonResult(true,"邮件发送成功");
    }


/**
     * 验证客户邮箱
     * @param qrId  课件二维码id
     * @param email  客户邮箱
     * @return
     */
    @RequestMapping(value = "/verify",method = RequestMethod.POST)
    public JsonResult verifyEmaill(@RequestParam("id")int qrId,@RequestParam("email")String email,HttpServletRequest request)throws Exception{
        Assert.isTrue(qrId > 0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码ID"));
        Assert.isTrue(email != null, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));

        BookQr bookQr = bookQrService.selectByPrimaryKey(qrId);
        //1.先查询t_client_attribute 有该记录
        if (clientAttrService.existEmail(email,bookQr.getBookId())){
            return  new JsonResult(true,"请填写密码");
        }else {
            return new JsonResult(false,"请确认信息");
        }
    }

}
