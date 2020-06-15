package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.service.*;
import com.yxtech.sys.vo.bookQr.*;
import com.yxtech.sys.vo.user.BookAskUserStatusVO;
import com.yxtech.sys.vo.user.UpdateClientInfoVo;
import com.yxtech.sys.vo.user.UserStatusForAutoVO;
import com.yxtech.sys.vo.user.UserStatusVO;
import com.yxtech.sys.vo.yun.ZhiZipResult;
import com.yxtech.utils.excel.DocumentEmailHandler;
import com.yxtech.utils.excel.DocumentHandler;
import com.yxtech.utils.file.*;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.mail.MailSender;
import jodd.datetime.JDateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by Chenxh on 2015/10/13.
 * 图书二维码管理
 */
@RestController("bookQrController")
@Scope("prototype")
@SessionAttributes("onwer")
@RequestMapping(value = "/qrcode")
public class BookQrController {

    public final static Logger log = LoggerFactory.getLogger(BookQrController.class);
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private ResService resService;
    @Autowired
    private BookService bookService;
    @Autowired
    private QrUserInfoService qrUserInfoService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientAttrMapper clientAttrMapper;
    @Autowired
    private QrExportMapper qrExportMapper;
    @Autowired
    private BookQrMapper bookQrMapper;
    @Autowired
    private BookQrAuthService bookQrAuthService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private OperationService operationService;
    @Autowired
    private QrUserInfoMapper qrUserInfoMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ClientService clientService;
    @Value("#{configProperties['wx.auth.receive.qr.dev']}")
    private String receiveQrDev;
    @Value("#{configProperties['wx.auth.receive.qr.test']}")
    private String receiveQrTest;
    @Value("#{configProperties['wx.auth.receive.qr.editor']}")
    private String receiveQrEditor;
    @Value("#{configProperties['wx.auth.receive.qr.online']}")
    private String receiveQrOnline;
    @Value("#{configProperties['wx.auth.receive.qr.local']}")
    private String receiveQrLocal;

    @Value("#{configProperties['wx.auth.receive.course.dev']}")
    private String receiveCourseDev;
    @Value("#{configProperties['wx.auth.receive.course.test']}")
    private String receiveCourseTest;
    @Value("#{configProperties['wx.auth.receive.course.editor']}")
    private String receiveCourseEditor;
    @Value("#{configProperties['wx.auth.receive.course.online']}")
    private String receiveCourseOnline;
    @Value("#{configProperties['wx.auth.receive.course.local']}")
    private String receiveCourseLocal;

    @Value("#{configProperties['wx.auth.receive.gua.dev']}")
    private String receiveGuaDev;
    @Value("#{configProperties['wx.auth.receive.gua.test']}")
    private String receiveGuaTest;
    @Value("#{configProperties['wx.auth.receive.gua.editor']}")
    private String receiveGuaEditor;
    @Value("#{configProperties['wx.auth.receive.gua.online']}")
    private String receiveGuaOnline;
    @Value("#{configProperties['wx.auth.receive.gua.local']}")
    private String receiveGuaLocal;

    @Value("#{configProperties['wx.auth.receive.auth.dev']}")
    private String receiveAuthDev;
    @Value("#{configProperties['wx.auth.receive.auth.test']}")
    private String receiveAuthTest;
    @Value("#{configProperties['wx.auth.receive.auth.editor']}")
    private String receiveAuthEditor;
    @Value("#{configProperties['wx.auth.receive.auth.online']}")
    private String receiveAuthOnline;
    @Value("#{configProperties['wx.auth.receive.auth.local']}")
    private String receiveAuthLocal;

    @Value("#{configProperties['qq.appId']}")
    private String qqAppId;
    @Value("#{configProperties['qq.appKey']}")
    private String qqAppKey;
    @Value("#{configProperties['qq.callBackUrl']}")
    private String qqCallBackUrl;
    public static  final int  WIDTH=240;


    /**
     * 查询二维码列表
     *
     * @param keyword  查询二维名称
     * @param page     页码
     * @param pageSize 每页大小
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @RequiresPermissions("/qrcode/list")
    public JsonResultPage getBookQrList(@RequestParam(value = "keyword") String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(defaultValue = "2") Integer tab,
                               @RequestParam Integer bookId) throws Exception {
        return bookQrService.getBookQrList(keyword, tab, bookId, page, pageSize);
    }

    /**
     * 修改二维码样式
     * @param bookQrStyleVo 二维码bookQrStyleVo
     * @param request  request
     * @return  返回json
     * @throws Exception
     */
    @RequestMapping(value = "/style", method = RequestMethod.PUT)
    @RequiresPermissions("/qrcode/style")
    public Map<String, Object> addStyle(@Valid @RequestBody BookQrStyleVo bookQrStyleVo, HttpServletRequest request) throws Exception {
        List<Integer> list = new ArrayList<>();
        for (Object o : bookQrStyleVo.getQrIds()) {
            list.add(Integer.parseInt(o.toString()));
//            DataPermission.hasPermission(BookQr.class, Method.UPDATE, bookQrService, null, Integer.parseInt(o.toString()));
        }

        Map<String, Object> map;
        map = this.bookQrService.editStyle(bookQrStyleVo, request);
        return map;
    }

    /**
     * 新增网站链接二维码
     * @param vo
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/addWebQr", method = RequestMethod.POST)
    public JsonResultId saveBook(@RequestBody AddWebQr vo, HttpServletRequest request) throws Exception {
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getBookId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "链接名称"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getNetUrl()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "链接地址"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getQrType()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码类型"));

        BookQr bookQr = new BookQr();
        bookQr.setBookId(vo.getBookId());
        bookQr.setName(vo.getName());
        bookQr.setNetUrl(vo.getNetUrl());
        bookQr.setQrType(vo.getQrType());
        int status = bookQrService.getCheckStatus(vo.getBookId());
        if(status != 0){
            bookQr.setIsSend(status);
        }

        bookQrService.insertSelective(bookQr);

        //获取网址基础信息
        String http= PathUtil.getHttpURL(request);
        String rootPath = PathUtil.getAppRootPath(request);
        //二维码的宽度和高度，从Book对象中获取
        Book book = this.bookService.selectByPrimaryKey(vo.getBookId());
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
        list.add(bookQr);
        //创建二维码
        bookQrService.creatQRFile(rootPath, http, width, logo, list,vo.getQrType(), request,"");

        //本来此类型二维码没有任何资源,但为了使整体数据结构一致性,虚构了资源类和文件类
        FileRes fileRes = new FileRes();
        String uuid= UUID.randomUUID().toString();
        fileRes.setUuid(uuid);
        fileRes.setName(vo.getName());
        fileRes.setPath("/虚构网站链接二维码");
        fileRes.setSuffix("txt");
        fileRes.setSize(0);
        this.fileResService.insert(fileRes);
        Res res = new Res();               // 初始化Resource对象
        res.setSecrecy(Constant.SECRECY);  // 设置资源保密 1：不保密 2：保密 默认是1
        res.setOnwer(book.getOnwer());     // 设置拥有者
        res.setFileUuid(uuid);             // 资源key UUID
        res.setDownLoad(true);//默认为可以下载
        res.setQrId(bookQr.getId());
        res.setFileType(1);
        res.setIndexs(0);
        this.resService.insertSelective(res);

        return new JsonResultId(bookQr.getId(),"创建成功!");

    }

    /**
     * 编辑网站链接二维码
     * @param vo
     * @return
     * @author yanfei
     * @date 2015.10.16
     */
    @RequestMapping(value = "/editWebQr", method = RequestMethod.PUT)
    public JsonResult saveBook(@RequestBody EditWebQr vo, HttpServletRequest request) throws Exception {
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getId()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getName()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "链接名称"));
        Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(vo.getNetUrl()), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "链接地址"));

        BookQr bookQr = new BookQr();
        bookQr.setId(vo.getId());
        bookQr.setName(vo.getName());
        bookQr.setNetUrl(vo.getNetUrl());

        bookQrService.updateByPrimaryKeySelective(bookQr);
        return new JsonResult(true,"修改成功!");
    }

    /**
     * 导出zip文件
     *
     * @param ids 导出图书ids
     * @param request   request
     * @throws IOException
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
//    @RequiresPermissions("/qrcode/export")
    public void export(@RequestParam(name = "ids") List<Integer> ids, HttpServletRequest request,HttpServletResponse response) throws Exception {
        //权限验证
        for (Integer id : ids) {
            //DataPermission.hasPermission(Book.class, Method.UPDATE, bookService, null, id);
        }
        File zip = this.bookQrService.export(ids, request);
        if(zip.exists()) {
            int len = 0;
            String fileName = URLEncoder.encode(zip.getName() , "utf-8"); //为了解决中文名称乱码问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                fileName = new String(zip.getName().getBytes("UTF-8"),
                        "iso-8859-1");
            }
            FileInputStream  fis = new FileInputStream(zip);
            byte[] b = new byte[2048];
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition","attachment;filename=\"" + fileName + "\"");
            //获取响应报文输出流对象
            ServletOutputStream out =response.getOutputStream();
            //输出
            while ((len = fis.read(b)) > 0){
                out.write(b, 0, len);
            }
            out.flush();
            out.close();
            fis.close();

            //删除压缩包及父级文件夹
            zip.delete();
            if (zip.getParentFile().exists()) {
                FileUtil.deleteDir(zip.getParentFile());
            }
        }
       // return new ResponseEntity<>(FileUtils.readFileToByteArray(zip), headers, HttpStatus.CREATED);
    }

    /**
     * 导出刮刮乐二维码zip文件
     *
     * @param qrcodeId 二维码id
     * @param number 倒出二维码数量
     * @param request   request
     * @throws IOException
     */
    @Transactional
    @RequestMapping(value = "/serialNumExport1", method = RequestMethod.GET)
    public void serialNumExport(int number,Integer qrcodeId,String data, HttpServletRequest request,HttpServletResponse response) throws Exception {
//        //权限验证
//        for (Integer id : ids) {
//            //DataPermission.hasPermission(Book.class, Method.UPDATE, bookService, null, id);
//        }
        File zip = this.bookQrService.serialNumExport(number,qrcodeId, data,request);
        if(zip.exists()) {
            int len = 0;
            String fileName = URLEncoder.encode(zip.getName() , "utf-8"); //为了解决中文名称乱码问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                fileName = new String(zip.getName().getBytes("UTF-8"),
                        "iso-8859-1");
            }
            FileInputStream  fis = new FileInputStream(zip);
            byte[] b = new byte[2048];
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition","attachment;filename=\"" + fileName + "\"");
            //获取响应报文输出流对象
            ServletOutputStream out =response.getOutputStream();
            //输出
            while ((len = fis.read(b)) > 0){
                out.write(b, 0, len);
            }
            out.flush();
            out.close();
            fis.close();

            //删除压缩包及父级文件夹
            zip.delete();
            if (zip.getParentFile().exists()) {
                zip.getParentFile().delete();
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
    }

    /**
     * 导出刮刮乐二维码zip文件
     *
     * @param qrcodeId 二维码id
     * @param number 倒出二维码数量
     * @param request   request
     * @throws IOException
     */
    @Transactional
    @RequestMapping(value = "/serialNumExport", method = RequestMethod.GET)
    public void serialNumExportExcel(int number,Integer qrcodeId,String data, HttpServletRequest request,HttpServletResponse response) throws Exception {
        bookQrService.serialNumExportExcel(number,qrcodeId, data,request,response);
    }

    /**
     * 刮刮卡二维码导出记录列表 +
     * @param qrId
     * @author liukailong
     * @return
     */
    @RequestMapping(value = "/exportRecord", method = RequestMethod.GET)
    public JsonResultList exportRecord(@RequestParam(name = "qrId") Integer qrId) {
        Example ex = new Example(QrExport.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("qrId",qrId);
        criteria.andEqualTo("type",1);

        List<QrExport> list = this.qrExportMapper.selectByExample(ex);
        if(list==null || list.size()==0){
            return new JsonResultList(new ArrayList<>());
        }

        List<ExportRecordVo> recordVoList = new ArrayList<>();
        for(QrExport qrExport:list){
            ExportRecordVo vo = new ExportRecordVo(qrExport);
            recordVoList.add(vo);
        }
        return new JsonResultList(true, MessageFormat.format(ConsHint.SUCCESS, "查询导出记录列表"), recordVoList);
    }

    /**
     * 新增二维码
     * @param bookQRAddVO 二维码vo
     * @param request request
     * @return 返回json
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("/qrcode/add")
    public Map<String, Object> addBookQr(@Valid @RequestBody BookQRAddVO bookQRAddVO, HttpServletRequest request) throws Exception {
        Map<String, Object> map;
        if(bookQRAddVO.getQrType()==null)
            bookQRAddVO.setQrType(2);
        map = this.bookQrService.saveBookQR(bookQRAddVO, request);
        return map;
    }

    /**
     * 删除二维码
     *
     * @param id 二维码ID
     * @param request request
     * @return 返回json
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @RequiresPermissions("/qrcode/delete")
    @Transactional
    public JsonResult delete(@RequestParam("id") List<Integer> id, HttpServletRequest request) throws Exception {
        //记录操作行为
        operationService.record(id,Constant.BOOKQR,request);

        for (Integer integer : id) {
            //权限验证);
//            DataPermission.hasPermission(BookQr.class,Method.DELETE,bookQrService,null,integer);
        }
        this.bookQrService.deleteBookQr(id, request);

        return new JsonResult(true, I18n.getMessage("success"));
    }

    /**
     * 编辑二维码
     * @param bookQr 二维码
     * @param request HttpServletRequest
     * @return 返回json
     * @throws Exception
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @RequiresPermissions("/qrcode/edit")
    public JsonResult qrEdit(@RequestBody BookQr bookQr, HttpServletRequest request) throws Exception {
        //权限验证
//        DataPermission.hasPermission(BookQr.class, Method.UPDATE, bookQrService, null,bookQr.getId());

        this.bookQrService.edit(bookQr, request);
        return new JsonResult(true, I18n.getMessage("success"));
    }


    /**
     * 扩展资源二维码跳转
     * @return
     */
    @RequestMapping(value = "/qrRedirec", method = RequestMethod.GET)
    public void qrRedirec(@RequestParam(name = "id") Integer id,
                        @RequestParam(value="code") String code,
                        @RequestParam(value="state") String state,
                        HttpServletResponse response) {
        String[] ss = state.split("_");
        String mode = ss[1];
        String paraStr = id + "&code="+code+"&state="+ss[0];
        try {
            if (mode.equals("online")){
                response.sendRedirect(this.receiveQrOnline+paraStr);
            } else if (mode.equals("dev")){
                response.sendRedirect(this.receiveQrDev+paraStr);
            } else if (mode.equals("test")){
                //编辑部版二维码拼接token
                String base64Token = com.yxtech.utils.password.Base64.getBase64(String.valueOf(System.currentTimeMillis())).replace("=","*");
                paraStr+="-"+ base64Token;
                System.out.println("##################url="+this.receiveQrTest+paraStr);
                response.sendRedirect(this.receiveQrTest+paraStr);
            } else if (mode.equals("editor")){
                response.sendRedirect(this.receiveQrEditor+paraStr);
            } else if (mode.equals("local")){
                response.sendRedirect(this.receiveQrLocal+paraStr);
            }
        } catch (IOException e) {
            log.error("error",e);
        }
    }

    /**
     * 扩展资源、课件资源二维码详情
     * @param id 二维码id
     * @return 返回json
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public JsonResultData<BookQrDetailVO> detail(@RequestParam(name = "id", required = false) Integer id,
                                                 @RequestParam(value="code",required = false) String code,
                                                 @RequestParam(value="state",required = false) String state,
                                                 HttpServletRequest request) throws Exception{
        log.debug("***detail code="+code+" state="+state);
        if ("wx".equals(state)) {
            return bookQrService.bookQrWeiXinDetail(id, code, state,request);
        }else if ("qq".equals(state)) {
            return bookQrService.bookQrQQDetail(id, code, state, qqAppId, qqAppKey, qqCallBackUrl,request);
        }else {
            // 其他
            return bookQrService.bookQrOtherDetail(id, code, state);
        }
    }

    /**
     * 上传logo等图片
     *
     * @param icon 图标uuid
     * @param request request
     * @return  返回json
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value = "/logo", method = RequestMethod.POST)
//    @RequiresPermissions("/qrcode/logo")
    public Map<String, Object> logo(
            QrCodeLogoVo icon, HttpServletRequest request
    ) throws Exception {


        Map<String, Object> map = new HashMap<>();
        //生成上传文件的路径信息，按天生成
        String savePath = Constant.FILE_PATH + File.separator + "icon" + File.separator + new JDateTime().toString("YYYYMMDD");
        String saveDirectory = PathUtil.getAppRootPath(request) + savePath;
        //验证路径是否存在，不存在则创建目录
        File path = new File(saveDirectory);
        if (!path.exists()){
            boolean b=path.mkdirs();
            if(!b){
                throw new Exception(I18n.getMessage("error"));
            }

        }

        String fileName = icon.getData().getOriginalFilename();

        //获取文件属性
        FileRes file = new FileRes();
        file.setName(NameUtil.getFileNameNoEx(fileName));
        file.setSuffix(NameUtil.getExtensionName(fileName));
        String uuid = UUID.randomUUID().toString();
        file.setUuid(uuid);
        file.setSize(new Long(icon.getData().getSize()).intValue());
        file.setPath(savePath + File.separator + uuid + "." + file.getSuffix());
        //实际将文件存储到服务器
        icon.getData()
                .transferTo(new File(saveDirectory, uuid + "." + file.getSuffix()));
        fileResService.insert(file);
        map.put("fileId", file.getUuid());
        map.put("message", I18n.getMessage("success"));
        map.put("status", true);
        return map;
    }

    /**
     * 课件二维码跳转
     * @return
     */
    @RequestMapping(value = "/courseRedirec", method = RequestMethod.GET)
    public void courseRedirec(@RequestParam(name = "id") Integer id,
                        @RequestParam(value="code") String code,
                        @RequestParam(value="state") String state,
                        HttpServletResponse response) {
        String[] ss = state.split("_");
        String mode = ss[1];
        String paraStr = id + "&code="+code+"&state="+ss[0];
        try {
            if (mode.equals("online")){
                response.sendRedirect(this.receiveCourseOnline+paraStr);
            } else if (mode.equals("dev")){
                response.sendRedirect(this.receiveCourseDev+paraStr);
            } else if (mode.equals("test")){
                response.sendRedirect(this.receiveCourseTest+paraStr);
            } else if (mode.equals("editor")){
                response.sendRedirect(this.receiveCourseEditor+paraStr);
            } else if (mode.equals("local")){
                response.sendRedirect(this.receiveCourseLocal+paraStr);
            }
        } catch (IOException e) {
            log.error("error",e);
        }
    }

    /**
     * 刮刮卡二维码跳转
     * @return
     */
    @RequestMapping(value = "/guaRedirec", method = RequestMethod.GET)
    public void guaRedirec(@RequestParam(name = "id") Integer id,
                           @RequestParam(value="activation") String activation,
                           @RequestParam(value="code") String code,
                           @RequestParam(value="state") String state,
                           HttpServletResponse response) {
        String[] ss = state.split("_");
        String mode = ss[1];
        String paraStr = id + "&activation=" + activation +"&code=" + code + "&state=" + ss[0];
        try {
            if (mode.equals("online")){
                response.sendRedirect(this.receiveGuaOnline+paraStr);
            } else if (mode.equals("dev")){
                response.sendRedirect(this.receiveGuaDev+paraStr);
            } else if (mode.equals("test")){
                response.sendRedirect(this.receiveGuaTest+paraStr);
            } else if (mode.equals("editor")){
                response.sendRedirect(this.receiveGuaEditor+paraStr);
            } else if (mode.equals("local")){
                response.sendRedirect(this.receiveGuaLocal+paraStr);
            }
        } catch (IOException e) {
            log.error("error",e);
        }
    }

    /**
     * 权限二维码跳转
     * @return
     */
    @RequestMapping(value = "/authRedirec", method = RequestMethod.GET)
    public void authRedirec(@RequestParam(name = "id") Integer id,
                           @RequestParam(value="authkey") String authkey,
                           @RequestParam(value="code") String code,
                           @RequestParam(value="state") String state,
                           HttpServletResponse response) {
        String[] ss = state.split("_");
        String mode = ss[1];
        String paraStr = id + "&authkey=" + authkey +"&code=" + code + "&state=" + ss[0];
        try {
            if (mode.equals("online")){
                response.sendRedirect(this.receiveAuthOnline+paraStr);
            } else if (mode.equals("dev")){
                response.sendRedirect(this.receiveAuthDev+paraStr);
            } else if (mode.equals("test")){
                response.sendRedirect(this.receiveAuthTest+paraStr);
            } else if (mode.equals("editor")){
                response.sendRedirect(this.receiveAuthEditor+paraStr);
            } else if (mode.equals("local")){
                response.sendRedirect(this.receiveAuthLocal+paraStr);
            }
        } catch (IOException e) {
            log.error("error",e);
        }
    }

    /**
     * 获取刮刮卡二维码明细
     * @param id  课件二维码id
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.3
     */
    @RequestMapping(value = "/coursewareDetail", method = RequestMethod.GET)
    public JsonResultData coursewareDetail(@RequestParam(name = "id") Integer id,
                                           @RequestParam(value="activation",required = false) String activation,
                                           @RequestParam(value="code",required = false) String code,
                                           @RequestParam(value="state",required = false) String state,
                                           HttpServletRequest request) throws Exception {
        log.debug("***coursewareDetail code="+code+" state="+state);
        if("other".equals(state)){
            CoursewareDetailVO detailVO = this.bookQrService.coursewareDetail(id, activation, "", code, state);
            return new JsonResultData(detailVO);
        }else{
            //用户同意微信授权
            String openid = "";
            if(!StringUtils.isBlank(code) && !StringUtils.isBlank(state)){//TODO
                String openidPointUnionid = qrUserInfoService.qrUserInfoSave(id,code,activation);//TODO
                if(openidPointUnionid == null || "".equals(openidPointUnionid)){
                    return new JsonResultData(false,"网络环境不好,请重新扫描!");
                }
                openid = openidPointUnionid.split(",")[0];
                //接入出版社用户中心
                userCenterService.userCenter(1, openidPointUnionid,request);
            }//TODO
            CoursewareDetailVO detailVO = this.bookQrService.coursewareDetail(id,activation,openid,code,state);

            if(detailVO == null){
                return new JsonResultData(false,"此二维码已被使用!");
            }else{
                //激活此序列号
                userCenterService.activeSerializedUuid(activation,openid);
                return new JsonResultData(detailVO);
            }
        }


    }

    /**
     * 判断刮刮卡二维码是否没有被绑定
     * @param activation  刮刮卡二维码序列号
     * @return
     * @throws Exception
     * @author cuihao
     * @date 2017.8.1
     */
    @RequestMapping(value = "/checkActivation", method = RequestMethod.GET)
    public JsonResultData checkActivation(String activation) throws Exception {
        QrUserInfo userInfo = new QrUserInfo();
        userInfo.setActivation(activation);
        //查看表中activation是否存在
        List<QrUserInfo> list = qrUserInfoMapper.select(userInfo);
        if(CollectionUtils.isEmpty(list)){
            return new JsonResultData(true, "没有被绑定!");
        }
        return new JsonResultData(false, "已被绑定!");
    }


    /**
     *设置课件二维码(已废弃1)
     * @param setlVO
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.4
     */
    @RequestMapping(value = "/set", method = RequestMethod.PUT)
    @RequiresPermissions("/qrcode/set")
    public JsonResult setDetail(@RequestBody CoursewareSetlVO setlVO) throws Exception {

        this.bookQrService.setDetail(setlVO);

        return new JsonResult(true, I18n.getMessage("success"));
    }

    /**
     *设置课件二维码(已废弃2)
     * @param vo
     * @return
     * @throws Exception
     * @author cuihao
     * @date 2017.5.15
     */
    @RequestMapping(value = "/sets", method = RequestMethod.PUT)
    public JsonResult sets(@RequestBody CoursewareSetNewVO vo) throws Exception {
        Assert.notNull(vo.getIds(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        Assert.isTrue(vo.getCheck()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "check值"));
        Assert.isTrue(vo.getSendCustomer()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "sendCustomer值"));

        this.bookQrService.sets(vo);

        return new JsonResult(true, I18n.getMessage("success"));
    }

    /**
     *设置课件二维码
     * @param vo
     * @return
     * @throws Exception
     * @author cuihao
     * @date 2017.5.15
     */
    @RequestMapping(value = "/setExamine", method = RequestMethod.PUT)
    public JsonResult sets(@RequestBody SetExamineVo vo) throws Exception {
        Assert.notNull(vo.getBookId(), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "图书id"));
        Assert.isTrue(vo.getFlag()>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "审核flag"));
        return bookQrService.setExamineVo(vo);
    }

    /**
     * desc: 校验教师手机号是否存在唯一性
     * date: 2019/6/6 0006
     * @author cuihao
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/checkMobile", method = RequestMethod.GET)
    public JsonResult checkMobile(String mobile) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(mobile), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "手机号"));

        //1.校验教师手机号是否存在唯一性(因为智学云那边手机号注册途径太多，所以王前进让取消了检查)
        //userCenterService.checkMobile(mobile);
        //2.发短信
        userCenterService.sendMess(mobile);

        return new JsonResult(true,"发送成功!");
    }


    /**
     * desc: 校验教师手机号验证码是否正确（人工审核）
     * date: 2019/6/10 0006
     * @author cuihao
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/checkMess", method = RequestMethod.GET)
    public JsonResult checkMess(String mobile,String code,String openid,String unionid) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(mobile), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "手机号"));
        Assert.isTrue(!StringUtils.isEmpty(code), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "验证码"));
        Assert.isTrue(!StringUtils.isEmpty(openid) || !StringUtils.isEmpty(unionid) , MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "openid和unionid两者不能全为空"));


        int result = userCenterService.checkMess(mobile,code,openid,unionid);

        return new JsonResultData<>(new CheckMessVo(result));
    }

    /**
     * desc: 校验教师手机号验证码是否正确（自动审核(已经废弃)）
     * date: 2019/6/10 0006
     * @author cuihao
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/checkMessForAuto", method = RequestMethod.GET)
    public JsonResult checkMessForAuto(String mobile,String code,String openid,String unionid,String eamil,String school,int qrcodeId) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(mobile), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "手机号"));
        Assert.isTrue(!StringUtils.isEmpty(code), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "验证码"));
        Assert.isTrue(qrcodeId>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "qrcodeId"));
        Assert.isTrue(!StringUtils.isEmpty(eamil), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "eamil"));
        Assert.isTrue(!StringUtils.isEmpty(school), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "school"));
        Assert.isTrue(!StringUtils.isEmpty(openid) || !StringUtils.isEmpty(unionid) , MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "openid和unionid两者不能全为空"));


        return userCenterService.checkMessForAuto(mobile,code,openid,unionid,eamil,school,qrcodeId);
    }

    /**
     * desc: 生成resourceDetail.html的sign(前端不再调用，改为自己生成)
     * date: 2019/6/10 0006
     * @author cuihao
     * @return
     */
    @RequestMapping(value = "/getHtmlSign", method = RequestMethod.GET)
    public JsonResult getHtmlSign() throws Exception {

        return bookQrService.getHtmlSign();
    }

    /**
     * desc: 生成resourceDetail.html的sign
     * date: 2019/6/10 0006
     * @author cuihao
     * @return
     */
    @RequestMapping(value = "/checkHtmlSign", method = RequestMethod.GET)
    public JsonResult checkHtmlSign(String sign) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(sign), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "sign"));


        return bookQrService.checkHtmlSign(sign);
    }

    /**
     * desc: 根据手机号获取用户信息
     * date: 2019/7/26 0026
     * @author cuihao
     * @param phone
     * @return
     */
    @RequestMapping(value = "/getClientInfo", method = RequestMethod.GET)
    public JsonResult getClientInfo(String phone){
        Assert.isTrue(!StringUtils.isEmpty(phone), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "phone"));

        return clientService.getClientInfo(phone);
    }

    /**
     * 更新客户信息
     * @param vo
     * @return
     */
    @RequestMapping(value = "/updateClientInfo", method = RequestMethod.PUT)
    public JsonResult updateClientInfo(@RequestBody UpdateClientInfoVo vo){
        return clientService.updateClientInfo(vo);
    }


    /**
     * 导出二维码文件
     *
     * @param ids 导出图书ids
     * @param request   request
     * @throws IOException
     */
    @RequestMapping(value = "/exportQrcode", method = RequestMethod.GET)
//    @RequiresPermissions("/qrcode/exportQrcode")
    public void exportQrcode(@RequestParam(name = "ids") List<Integer> ids,
                             @RequestParam(name = "bookId") Integer bookId,
                             HttpServletRequest request,HttpServletResponse response) throws Exception {
        //权限验证
        for (Integer id : ids) {
            //DataPermission.hasPermission(Book.class, Method.UPDATE, bookService, null, id);
        }
        File zip = this.bookQrService.exportQrcode(ids,bookId,request);
        if(zip.exists()) {
            int len = 0;
            String fileName = URLEncoder.encode(zip.getName() , "utf-8"); //为了解决中文名称乱码问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                fileName = new String(zip.getName().getBytes("UTF-8"),
                        "iso-8859-1");
            }
            FileInputStream  fis = new FileInputStream(zip);
            byte[] b = new byte[2048];
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition","attachment;filename=\"" + fileName + "\"");
            //获取响应报文输出流对象
            ServletOutputStream out =response.getOutputStream();
            //输出
            while ((len = fis.read(b)) > 0){
                out.write(b, 0, len);
            }
            out.flush();
            out.close();
        }
        // return new ResponseEntity<>(FileUtils.readFileToByteArray(zip), headers, HttpStatus.CREATED);
    }


    /**
     * 二维码下拉框
     * @param bookId
     * @author liukailong
     * @return
     */
    @RequestMapping(value = "/downList", method = RequestMethod.GET)
    public JsonResultList downList(@RequestParam(name = "bookId") Integer bookId) {
        Example ex = new Example(BookQr.class);
        ex.createCriteria().andEqualTo("bookId",bookId);
        List<BookQr> list = this.bookQrService.selectByExample(ex);
        return new JsonResultList(true, MessageFormat.format(ConsHint.SUCCESS, "二维码下拉框"), list);
    }


    /**
     * 获取用户对课件二维码的审核状态
     * @param openId
     * @param qrcodeId
     * @return
     * @throws Exception
     * @author liukailong
     * @date 2016.10.10 modifier 2016.10.12
     */
    @RequestMapping(value = "/userStatus", method = RequestMethod.GET)
    public JsonResultData userStatus(@RequestParam(name="openId") String openId,@RequestParam(name="mobile") String phone,@RequestParam(value="qrcodeId") String qrcodeId,
                                     @RequestParam(value="sendEmail",required = false) String sendEmail) throws Exception {
        Assert.notNull(qrcodeId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        Assert.notNull(phone, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "手机号"));

        //查询二维码实体
        BookQr bookQr = bookQrService.selectByPrimaryKey(Integer.parseInt(qrcodeId));
        Integer bookId = bookQr.getBookId();
        //1审核中，2人工审核已通过，3未通过,4未申请
        String status = "4";
        Client client = null;
        Example ep = new Example(Client.class);
        ep.setOrderByClause("id desc");
        ep.createCriteria().andEqualTo("openId", openId);
        List<Client> clientList = this.clientMapper.selectByExample(ep);
        if (CollectionUtils.isEmpty(clientList)) {
            log.info("userStatus openId " + openId + "用户不能存在!");
            //throw new ServiceException("userStatus方法错误,根据openId:" + openId + "无法找到用户!");
            Client newClient = new Client();
            newClient.setName("name");
            newClient.setPhone(phone);
            newClient.setOpenId(openId);
            clientMapper.insertSelective(newClient);

            client = clientMapper.selectByPrimaryKey(newClient.getId());
        }else{
            client = clientList.get(0);
        }

        Integer clientId = client.getId();
        //人工审核,先查看之前是否扫描过本书，如果扫描过，且状态为审核通过，返回上次审核状态
        Example qr = new Example(ClientAttr.class);
        qr.setOrderByClause("createTime desc");
        qr.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("type", Constant.ONE);
        List<ClientAttr> clientAttList = clientAttrMapper.selectByExample(qr);
        if (clientAttList != null && clientAttList.size() > 0) {
            int preStatus = clientAttList.get(0).getStatus();
            log.debug("---------------- 扫描过本书,上次状态为:" + preStatus + "----------------------");
            //插入审核队列 状态为通过或者未通过
            Example qrSub = new Example(ClientAttr.class);
            qrSub.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("qrId", qrcodeId).andEqualTo("type", Constant.ONE);
            List<ClientAttr> clientAttSubList = clientAttrMapper.selectByExample(qrSub);
            if (clientAttSubList == null || clientAttSubList.size() == 0) {
                ClientAttr clientAttr = new ClientAttr();
                clientAttr.setClientId(clientId);
                clientAttr.setEmail(client.getEmail());
                clientAttr.setBookId(bookId);
                clientAttr.setQrId(Integer.valueOf(qrcodeId));
                clientAttr.setStatus(preStatus);
                clientAttr.setType(bookQr.getQrType());
                clientAttr.setCreateTime(new Date());
                clientAttrMapper.insertSelective(clientAttr);
            }
            if(preStatus == 2){
                //上次审核通过，证明手机号是信任的，直接调智学云接口获取教师邮箱地址
                String mobile = client.getPhone();
                if(!StringUtils.isEmpty(mobile)){
                    log.debug("上次审核通过，证明手机号是信任的，直接调智学云接口获取教师邮箱地址");
                    CheckTeacherVo vo = userCenterService.checkIfTrusted(mobile);
                    log.debug("----------------调智学云接口获取教师邮箱地址结果:" + JSON.toJSONString(vo));
                    client.setEmail(vo.getEmail());
                }
            }
            status = preStatus + "";
            return new JsonResultData<>(true, I18n.getMessage("success"), new UserStatusVO(status,client.getEmail()));
        }

        return new JsonResultData<>(true, I18n.getMessage("success"), new UserStatusVO(status,client.getEmail()));
    }

    /**
     * 获取用户对课件二维码的审核状态
     * @param openId
     * @param qrcodeId
     * @return
     * @throws Exception
     * @author liukailong
     * @date 2016.10.10 modifier 2016.10.12
     */
    @RequestMapping(value = "/userStatusForAuto", method = RequestMethod.GET)
    public JsonResultData userStatusForAuto(@RequestParam(name="openId") String openId,@RequestParam(value="qrcodeId") String qrcodeId) throws Exception {
        Assert.notNull(openId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "openId"));
        Assert.notNull(qrcodeId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));

        BookQr bookQr = bookQrService.selectByPrimaryKey(Integer.parseInt(qrcodeId));
        Integer bookId = bookQr.getBookId();
        //1未扫过 2扫过
        int status = 1;
        Example ep = new Example(Client.class);
        ep.setOrderByClause("id desc");
        ep.createCriteria().andEqualTo("openId", openId);
        List<Client> clientList = this.clientMapper.selectByExample(ep);
        if (CollectionUtils.isEmpty(clientList)) {
            log.error("userStatusForAuto openId" + openId + "用户不能存在!");
            throw new ServiceException("userStatusForAuto方法错误,根据openId:" + openId + "无法找到用户!");
        }
        Client client = clientList.get(0);
        Integer clientId = client.getId();
        //人工审核,先查看之前是否扫描过本书
        Example example = new Example(ClientAttr.class);
        example.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("type", Constant.TWO);
        int count = clientAttrMapper.selectCountByExample(example);
        if (count> 0) {
            status = 2;
        }

        return new JsonResultData<>(true, I18n.getMessage("success"), new UserStatusForAutoVO(status));
    }

    /**
     * 书问教师校验
     * @param openId
     * @param qrcodeId
     * @return
     * @throws Exception
     * @author liukailong
     * @date 2016.10.10 modifier 2016.10.12
     */
    @RequestMapping(value = "/bookAskUserStatus", method = RequestMethod.GET)
    public JsonResultData bookAskUserStatus(@RequestParam(name="openId") String openId,@RequestParam(value="qrcodeId") String qrcodeId) throws Exception {
        Assert.notNull(qrcodeId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(Integer.parseInt(qrcodeId));
        Book book = bookMapper.selectByPrimaryKey(bookQr.getBookId());
        Example ep = new Example(Client.class);
        ep.createCriteria().andEqualTo("openId", openId);
        List<Client> clientList = this.clientMapper.selectByExample(ep);
        if (CollectionUtils.isEmpty(clientList)) {
            log.error("bookAskUserStatus openId"+openId+"用户不能存在!");
            throw new ServiceException("bookAskUserStatus方法错误,根据openId:"+openId +"无法找到用户!");
        }
        Client client = clientList.get(0);
        String phone = client.getPhone();
        //获取书问跳转路径
        String url = bookQrService.getBookAskToken(phone,book.getCode());
        //然后去智学云判断,如果手机号是信任的,那么直接放过
        CheckTeacherVo vo = userCenterService.checkIfTrusted(phone);
        log.debug("----------------智学云判断手机号结果:" + JSON.toJSONString(vo));
        //-1拒绝；0待审核；1通过；2不存在
        int status = vo.getStatus();

        return new JsonResultData<>(true, I18n.getMessage("success"), new BookAskUserStatusVO(status,url,vo.getEmail()));
    }

    /**
     * 修改用户邮箱，并记录邮件发送状态
     * @param openId
     * @param qrcodeId
     * @return
     * @throws Exception
     * @author liukailong
     * @date 2016.10.10 modifier 2016.10.12
     */
    @RequestMapping(value = "/updateEmailAndRecord", method = RequestMethod.GET)
    public JsonResult updateEmailAndRecord(String openId, String qrcodeId, String email) throws Exception {
        Assert.notNull(openId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "openId"));
        Assert.notNull(qrcodeId, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));
        Assert.notNull(email, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "邮箱"));
        //查询二维码实体
        BookQr bookQr = bookQrService.selectByPrimaryKey(Integer.parseInt(qrcodeId));
        Example ep = new Example(Client.class);
        ep.createCriteria().andEqualTo("openId", openId);
        List<Client> clientList = this.clientMapper.selectByExample(ep);
        if (CollectionUtils.isEmpty(clientList)) {
            log.error("updateEmailAndRecord openId"+openId+"用户不能存在!");
            throw new ServiceException("updateEmailAndRecord方法错误,根据openId:"+openId +"无法找到用户!");
        }
        Client client = clientList.get(0);
        //修改用户邮箱
        //userCenterService.updateUserEmail(openId,email);
        //给申请人发送邮件
        client.setEmail(email);
        bookQrService.sendEmailToClient(bookQr, client);
        //保存记录
        saveRecord(bookQr,client);

        return new JsonResult(true, "success!");
    }

    /**
     * desc:审核教师通过后，书问调用智学云
     * date: 2019/7/23 0023
     * @author cuihao
     * @param email
     * @param phone
     * @return
     */
    @RequestMapping(value = "/examTeacherDone", method = RequestMethod.GET)
    public JsonResult examTeacherDone(String source,String email,String phone) throws Exception {
        Assert.isTrue(!StringUtils.isEmpty(email), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "email"));
        Assert.isTrue(!StringUtils.isEmpty(phone), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "phone"));
        Assert.isTrue(!StringUtils.isEmpty(source), MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "source"));

        log.info("-----------参数source:"+source);
        //给申请人发送邮件
        Client clientEP = new Client();
        clientEP.setPhone(phone);
        List<Client> clientList = clientMapper.select(clientEP);
        if (!CollectionUtils.isEmpty(clientList)) {
            Client client = clientList.get(0);
            client.setEmail(email);
            log.info("-----------client:"+client);

            String[] arr_source = source.split("=");
            Integer qrId = Integer.valueOf(arr_source[1]);
            //查询二维码实体
            BookQr bookQr = bookQrService.selectByPrimaryKey(qrId);
            log.info("-----------bookQr:"+bookQr);
            if(bookQr != null){
                bookQrService.sendEmailToClient(bookQr, client);
            }
        }

        return new JsonResult(true,"发送成功!");
    }


    /**
     * 保存记录
     * @param bookQr
     * @param client
     */
    private void saveRecord(BookQr bookQr, Client client) {
        Integer clientId = client.getId();
        Integer bookId = bookQr.getBookId();
        Integer qrcodeId = bookQr.getId();
        Example qrSub = new Example(ClientAttr.class);
        qrSub.createCriteria().andEqualTo("clientId", clientId).andEqualTo("bookId", bookId).andEqualTo("qrId", qrcodeId).andEqualTo("type", Constant.ONE);
        List<ClientAttr> clientAttSubList = clientAttrMapper.selectByExample(qrSub);
        if (clientAttSubList == null || clientAttSubList.size() == 0) {
            ClientAttr clientAttr = new ClientAttr();
            clientAttr.setClientId(clientId);
            clientAttr.setEmail(client.getEmail());
            clientAttr.setBookId(bookId);
            clientAttr.setQrId(Integer.valueOf(qrcodeId));
            clientAttr.setStatus(2);
            clientAttr.setType(bookQr.getQrType());
            clientAttr.setCreateTime(new Date());
            clientAttrMapper.insertSelective(clientAttr);
        }
    }


    /**
     * 二维码资源批量压缩打包
     * @return
     */
    @RequestMapping(value = "/compressall", method = RequestMethod.GET)
    public JsonResult compress() {
        List<BookQr> list = bookQrMapper.getResNumThanTwo();
        for(BookQr bookQr:list){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("开始压缩   "+bookQr.toString());
            //压缩
            resService.compress(bookQr.getId());
        }

        return new JsonResult(true,"发送指令成功");
    }

    /**
     * 二维码资源压缩打包
     * @param id 二维码id
     * @return
     */
    @RequestMapping(value = "/compress", method = RequestMethod.GET)
    public JsonResult compress(int id) {
        Assert.isTrue(id>0, MessageFormat.format(ConsHint.ARG_VALIDATION_ERR, "二维码id"));

        return resService.compress(id);
    }

    /**
     * 调用智学云压缩完成后,回调接口
     * @param result
     * @return
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public JsonResult callback(@RequestBody ZhiZipResult result) {

        resService.callback(result);

        return new JsonResult(true,"ok");
    }
}
