package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.ClientAttrMapper;
import com.yxtech.sys.dao.UserMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.ClientBookVo;
import com.yxtech.sys.vo.bookQr.BookQRFile;
import com.yxtech.sys.vo.clientAttr.ClientAttrBookVo;
import com.yxtech.utils.excel.DocumentEmailHandler;
import com.yxtech.utils.excel.DocumentEmailNoHandler;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.mail.MailSender;
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
 * Created by Administrator on 2015/10/27.
 */
@Service
public class ClientAttributeService extends BaseService<ClientAttr>{
    private ClientAttrMapper clientAttrMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BookQrService bookQrService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResService resService;
    @Autowired
    private MailSender mailSender;

    @Resource(name = "clientAttrMapper")
    public void setClientAttrMapper(ClientAttrMapper clientAttrMapper) {
        setMapper(clientAttrMapper);
        this.clientAttrMapper = clientAttrMapper;
    }

    /**
     * 批量审核
     * @param idList
     * @param flag 1：通过；2：驳回
     * @param reason 驳回原因
     */
    @Transactional
    public boolean send(List<Object> idList,int flag,String reason)throws Exception{

        Example example = new Example(ClientAttr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",idList);
        List<ClientAttr> clientAttrList = selectByExample(example);
        if(clientAttrList == null || clientAttrList.size()==0){
            return false;
        }

        if(flag == 1){
            //循环发送邮件告诉用户已通过申请
            for (ClientAttr clientAttr : clientAttrList ) {
                //更改status字段为2 (1:待审核  2：审核通过（发送邮件）3：不通过)
                Example exampleClientAttr = new Example(ClientAttr.class);
                Example.Criteria criteriaClientAttr = exampleClientAttr.createCriteria();
                criteriaClientAttr.andEqualTo("clientId",clientAttr.getClientId());
                criteriaClientAttr.andEqualTo("bookId",clientAttr.getBookId());
                criteriaClientAttr.andEqualTo("qrId",clientAttr.getQrId());
                criteriaClientAttr.andEqualTo("type",Constant.ONE);
                clientAttr.setStatus(2);
                //下面的属性,都不改
                clientAttr.setEmail(null);
                clientAttr.setTarget(null);
                clientAttr.setCreateTime(new Date());
                this.updateByExampleSelective(clientAttr,exampleClientAttr);

                //发送邮件
                int bookId = clientAttr.getBookId();
                Book book=bookService.selectByPrimaryKey(bookId);
                Client client = clientService.selectByPrimaryKey(clientAttr.getClientId());

                List<BookQRFile> bqFileList=new ArrayList<BookQRFile>();
                BookQRFile bookQRFile=null;
                BookQr bookQr=null;

                List<BookQr> bqList=bookQrService.getListByBookId(bookId);
                if(bqList!=null&&bqList.size()>0){
                    bookQr=bqList.get(0);
                }else{
                    return false;
                }
                List<FileRes> resList=resService.getFileResesByQrId(clientAttr.getQrId());
                for (FileRes fileRes:resList) {
                    bookQRFile=new BookQRFile();
                    bookQRFile.setName(fileRes.getName());
                    bookQRFile.setSize(FileUtil.convertFileSize(fileRes.getSize()));
                    bqFileList.add(bookQRFile);
                }
                // 客户信息生成内容模版
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("book", book);
                dataMap.put("bqList", bqFileList);
                dataMap.put("client", client);
                DocumentEmailHandler mdoc = new DocumentEmailHandler();

                //预览地址,网站链接类型二维码直接显示链接,否则显示资源列表
                BookQr bq = bookQrService.selectByPrimaryKey(clientAttr.getQrId());
                if(StringUtils.isEmpty(bq.getNetUrl())){
                    dataMap.put("url",Constant.COURSE_QR_HTTP.replaceAll("coursewareOne","coursewareThr")+clientAttr.getQrId()+"&email="+client.getEmail()+"&openId="+client.getOpenId());
                }else{
                    dataMap.put("url", bq.getNetUrl());
                }

                String context =  mdoc.createHTML(dataMap) ;

                String subject = "文泉云盘移动阅读平台审核信息";
                String[] toMail = new String[]{client.getEmail()};

                mailSender.sendRichTextMail(context, subject, toMail);
            }
        }else if(flag == 2){
            //循环发送邮件告诉用户申请被驳回
            for (ClientAttr clientAttr : clientAttrList ) {
                //更改status字段为2 (1:待审核  2：审核通过（发送邮件）3：不通过)
                Example exampleClientAttr = new Example(ClientAttr.class);
                Example.Criteria criteriaClientAttr = exampleClientAttr.createCriteria();
                criteriaClientAttr.andEqualTo("clientId",clientAttr.getClientId());
                criteriaClientAttr.andEqualTo("bookId",clientAttr.getBookId());
                criteriaClientAttr.andEqualTo("type",Constant.ONE);
                clientAttr.setStatus(3);
                //下面的属性,都不改
                clientAttr.setQrId(null);
                clientAttr.setEmail(null);
                clientAttr.setTarget(null);
                clientAttr.setReason(reason);
                clientAttr.setCreateTime(new Date());
                this.updateByExampleSelective(clientAttr,exampleClientAttr);

                //发送邮件
                int bookId = clientAttr.getBookId();
                Book book=bookService.selectByPrimaryKey(bookId);
                Client client = clientService.selectByPrimaryKey(clientAttr.getClientId());

                List<BookQRFile> bqFileList=new ArrayList<BookQRFile>();
                BookQRFile bookQRFile=null;
                BookQr bookQr=null;

                List<BookQr> bqList=bookQrService.getListByBookId(bookId);
                if(bqList!=null&&bqList.size()>0){
                    bookQr=bqList.get(0);
                }else{
                    return false;
                }
                List<FileRes> resList=resService.getFileResesByQrId(bookQr.getId());
                for (FileRes fileRes:resList) {
                    bookQRFile=new BookQRFile();
                    bookQRFile.setName(fileRes.getName());
                    bookQRFile.setSize(FileUtil.convertFileSize(fileRes.getSize()));
                    bqFileList.add(bookQRFile);
                }
                // 客户信息生成内容模版
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("book", book);
                dataMap.put("bqList", bqFileList);
                dataMap.put("client", client);
                dataMap.put("reason", reason);
                DocumentEmailNoHandler mdoc = new DocumentEmailNoHandler();

                //预览地址
                dataMap.put("url",Constant.COURSE_QR_HTTP+bookQr.getId()+"&email="+client.getOpenId());
                String context =  mdoc.createHTML(dataMap) ;

                String subject = "文泉云盘移动阅读平台审核信息";
                String[] toMail = new String[]{client.getEmail()};

                mailSender.sendRichTextMail(context, subject, toMail);
            }
        }



        return true;
    }

    /**
     *
     * @param bookId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getList(int status , int bookId,int userId ,int pageNo, int pageSize){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null)
            throw new ServiceException("用户不存在!");
        int count = clientAttrMapper.getCountExamineList(status, bookId, userId, user.getRole(), (pageNo-1)*pageSize, pageSize);
        List<ClientAttrBookVo> cabs = clientAttrMapper.getExamineList(status, bookId, userId, user.getRole(), (pageNo-1)*pageSize, pageSize);
        return new JsonResultPage(new Page(count, pageNo, pageSize, cabs));
    }

    /**
     * 导出审核列表
     * @author lsn
     * @param bookId
     * @param status
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportList(int userId,int bookId, int status, HttpServletRequest request, HttpServletResponse response)throws Exception{
        User user=userMapper.selectByPrimaryKey(userId);
        Integer role=user.getRole();
        if(user==null)
            throw new ServiceException("用户不存在!");

        Map<String,Object> map=new HashMap<>();
        map.put("bookId",bookId);
        map.put("status",status);
        map.put("role",role);
        List<ClientBookVo> clientBookVoList = clientAttrMapper.exportList(map);
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("clientBookVoList", clientBookVoList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + (clientBookVoList.size() != 0 ? "freemarker/clientbookListScan.xls" : "freemarker/bookListScanNoData.xls");
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("审核信息.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("审核信息.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, clientBookVoList, "clientBookVoList", Constant.EXCEL_MAX_INDEX);

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

}





















































