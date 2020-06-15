package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.BaseService;
import com.yxtech.common.BeanMapper;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.*;
import com.yxtech.sys.dao.*;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.bookQr.BookQRAddVO;
import com.yxtech.sys.vo.bookQr.BookQRFile;
import com.yxtech.sys.vo.client.ClientBookDetailVo;
import com.yxtech.sys.vo.client.ClientBookVO;
import com.yxtech.sys.vo.client.ClientDetailVO;
import com.yxtech.sys.vo.client.ClientExportVO;
import com.yxtech.sys.vo.count.ResCountVo;
import com.yxtech.sys.vo.resource.FilesVo;
import com.yxtech.sys.vo.resource.ResIndexVo;
import com.yxtech.sys.vo.resource.ResourceMailVo;
import com.yxtech.sys.vo.resource.ResourceVO;
import com.yxtech.sys.vo.yun.ZhiZipResult;
import com.yxtech.sys.vo.yun.ZipPostVo;
import com.yxtech.utils.excel.DocumentEmailHandler;
import com.yxtech.utils.excel.DocumentHandler;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.*;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.mail.MailSender;
import com.yxtech.utils.qr.HttpTookit;
import com.yxtech.utils.runCode.HttpClientUtil;
import com.yxtech.utils.zip.ZipCompressor;
import com.yxtech.utils.zip.ZipUtils;
import jodd.datetime.JDateTime;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * 资源管理业务逻辑类
 * Created by lyj on 2015/10/17.
 */
@Service
public class ResService extends BaseService<Res> {
    @Autowired
    private BookService bookService;
    @Autowired
    private FileResService fileResService;
    @Autowired
    private FileResMapper fileResMapper;
    @Autowired
    private ClientAttrService clientAttrService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientBookService clientBookService;
    @Autowired
    private BeanMapper mapper;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private MailService mailService;
    @Autowired
    private QrDownRankService qrDownRankService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ClientAttrMapper clientAttrMapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private OperationService operationService;
    @Autowired
    private BookQrMapper bookQrMapper;
    @Autowired
    private ZipOrderMapper zipOrderMapper;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ZhixueTokenService zhixueTokenService;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private ResMapper resMapper;
    @Value("#{configProperties['zhixueyun.host']}")
    private String host;
    @Value("#{configProperties['zhixueyun.userId']}")
    private String userId;

    public ResMapper getResMapper() {
        return resMapper;
    }

    @Resource(name = "resMapper")
    public void setResMapper(ResMapper resMapper) {
        setMapper(resMapper);
        this.resMapper = resMapper;
    }

    /**
     * 新建二维码并绑定资源和二维码的关系
     * @param files 资源对象集合 书籍ID 资源类型 1：课件资源；2：扩展资源
     * @param bookId 书籍ID
     * @param qrType 资源类型 1：课件资源；2：扩展资源
     * @param request
     * @return Integer 二维码ID
     * @author lyj   修改hesufang 修改zml
     * @since 2015-10-17
     */
    @Transactional
    public Map<String, Object> saveResource(List<FilesVo> files, Integer bookId, int qrType, HttpServletRequest request) throws Exception {
        Map<String, Object> reMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        Book book = bookService.selectByPrimaryKey(bookId);
        boolean bool = true;
        for (FilesVo file : files) {
            // 生成 UUID 对应关系
            String uuid = UUID.randomUUID().toString();
            Res res = new Res();                                        // 初始化Resource对象
            res.setSecrecy(Constant.SECRECY);                           // 设置资源保密 1：不保密 2：保密 默认是1
            res.setOnwer(book.getOnwer());             // 设置拥有者
            res.setFileUuid(uuid);                                      // 资源key UUID
            res.setDownLoad(true);//默认为可以下载
            FileRes fileRes = convertFileRes(file);                   // 根据文件UUID获取文件信息
            fileRes.setUuid(uuid);
            fileRes.setStatus(30);                                    //正在处理
            fileResMapper.insertSelective(fileRes);
            file.setFileName(file.getFileName().substring(0, file.getFileName().lastIndexOf(".")));
            if (null != fileRes) {
                reMap.put("suffix", fileRes.getSuffix());             // 设置文件后缀名
                reMap.put("fileName", fileRes.getName());             // 设置文件名称
            }
            if (bookId != 0) {
                if (bool) {
                    if (qrType == 3) {   // 课件资源 || 刮刮乐资源
                        // 根据类型判断该书有没有二维码
                        Example example = new Example(BookQr.class);
                        example.createCriteria().andEqualTo("bookId", bookId).andEqualTo("qrType", qrType);
                        List<BookQr> bookQrList = bookQrService.selectByExample(example);
                        if (bookQrList != null && bookQrList.size() != 0) {
                            map.put("id", bookQrList.get(0).getId());
                        } else {
                            if (bool) {
                                //生成二维码，获取二维码ID  生成课件资源二维码
                                map = bookQrService.saveBookQR(new BookQRAddVO(bookId, fileRes.getName() + "." + fileRes.getSuffix(), qrType), request);
                            }
                        }

                    }
                    if (qrType == 1 || qrType == 2) {   // 扩展资源
                        //生成二维码，获取二维码ID  生成扩展资源二维码
                        map = bookQrService.saveBookQR(new BookQRAddVO(bookId, fileRes.getName() + "." + fileRes.getSuffix(), qrType), request);
                    }
                    bool = false;
                }
                //二维码ID
                res.setQrId((int) map.get("id"));
                reMap.put("qrId", map.get("id"));
            }
            res.setFileType(getFileTypeBySuffix(fileRes.getSuffix()));
            if (res.getFileType() == 1){
                //提取文本内容
                res.setText(ReadTextUtil.readText(PathUtil.getAppRootPath(request) + fileRes.getPath()));
            }
            //设置index值
            int maxIndex = getMaxIndex(res.getQrId());
            res.setIndexs(maxIndex+1);
            // 绑定资源和二维码的关系
            super.insertSelective(res);
            /**
             * 把图书资源关系传给智学云
             */
            userCenterService.bindBookRes(bookId,qrType,res.getId());
            // 资源ID
            reMap.put("id", res.getId());
            // 文本内容
            reMap.put("text", res.getText());
        }

        if(qrType!=3){
            //主动触发云端压缩
            this.compress((int)reMap.get("qrId"));
        }

        return reMap;
    }

    public FileRes convertFileRes(FilesVo file){
        FileRes fileRes = new FileRes();
        fileRes.setCreateTime(new Date());
        fileRes.setName(file.getFileName().substring(0, file.getFileName().lastIndexOf(".")));
        fileRes.setSize(file.getSize().isEmpty() ? 0 : Integer.parseInt(file.getSize()));
        fileRes.setSuffix(file.getFileName().substring(file.getFileName().lastIndexOf(".")+1));
        if (StringUtils.isEmpty(file.getResourceIDL())){
            fileRes.setZhixueid(file.getResourceID().isEmpty() ? 0 : Long.valueOf(file.getResourceID()));                  // 不管有没有值 这里都有值
            fileRes.setQizhixueid(file.getResourceIDL().isEmpty() ? 0 : Long.valueOf(file.getResourceIDL()));               // 如果有值 代表是视频
            // 为空不是视频
            fileRes.setPath(file.getResourceID());
            fileRes.setViewurl(file.getResourceID());
        }else{
            fileRes.setZhixueid(file.getResourceIDL().isEmpty() ? 0 : Long.valueOf(file.getResourceIDL()));                  // 不管有没有值 这里都有值
            fileRes.setQizhixueid(file.getResourceID().isEmpty() ? 0 : Long.valueOf(file.getResourceID()));               // 如果有值 代表是视频
            // 是视频文件
            //fileRes.setPath("http://yuntv.letv.com/bcloud.html?uu="+ file.getPath().substring(0, file.getPath().indexOf("/")) + "&vu="+ file.getPath().substring(file.getPath().indexOf("/")+1));
            //fileRes.setViewurl("http://yuntv.letv.com/bcloud.html?uu="+ file.getPath().substring(0, file.getPath().indexOf("/")) + "&vu="+ file.getPath().substring(file.getPath().indexOf("/")+1));
            if(org.apache.commons.lang.StringUtils.isBlank(file.getPath())){
                System.out.println("上传的视频文件,path参数不能为空!");
                throw new ServiceException("上传出错,请重新上传!");
            }
            fileRes.setPath(file.getPath());
            fileRes.setViewurl(file.getPath());
            fileRes.setAlivideoid(file.getPath());
        }
        return fileRes;
    }
    /**
     * 获取二维码下面资源列表中index值最大的,如果二维码下面没有资源则返回0
     * @param qrId
     * @return
     */
    private int getMaxIndex(int qrId){
        Example example = new Example(Res.class);
        example.setOrderByClause("indexs desc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qrId",qrId);

        List<Res> list = resMapper.selectByExample(example);

        if(list==null || list.size()==0){
            return 0;
        }else{
            return list.get(0).getIndexs();
        }

    }

    /**
     * 删除资源
     *
     * @param resIds 资源ID列表
     * @param qrId   二维码ID
     * @param tag    1课件资源 2扩展资源         \
     * @author lyj
     * @since 2015-10-19
     */
    @Transactional
    public void deleteResource(List<Integer> resIds, int qrId, int tag, HttpServletRequest request) throws Exception {
        BookQr bq = bookQrMapper.selectByPrimaryKey(qrId);
        if(bq==null)
            return;

        for (Integer resId : resIds) {
            //根据资源ID获取资源对应的地址
            if (resId == 0) {
                continue;
            }
            Res res = super.selectByPrimaryKey(resId);
            Example ep = new Example(FileRes.class);
            ep.createCriteria().andEqualTo("uuid", res.getFileUuid());
            List<FileRes> fileResList = this.fileResService.selectByExample(ep);
            if (null != fileResList && fileResList.size() > 0) {
                for (FileRes fileRes : fileResList) {
                    if (null != fileRes) {
                        //根据资源地址删除对应的资源。
                        File file = new File(PathUtil.getAppRootPath(request) + fileRes.getPath());
                        //判断资源是否存在
                        if (file.exists()) {
                            file.delete();
                        }
                        fileResService.deleteByPrimaryKey(fileRes.getId());
                    }
                    //删除资源信息
                    Example example = new Example(Res.class);
                    example.createCriteria().andEqualTo("id", resId);
                    example.createCriteria().andEqualTo("onwer", CurrentUser.getUser().getOrgId());
                    super.deleteByExample(example);
                }
            }
            /**
             * 向出版社删除图书和资源关系
             */
            userCenterService.deleteBookRes(resId);
            //删除举报列表
            Example example = new Example(Report.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("fileId", resId);
            reportService.deleteByExample(example);

            if (tag == 2 || tag == 1) {
                // 根据qrId查询Res表中是否还有资源数据
                Example ex = new Example(Res.class);
                ex.createCriteria().andEqualTo("qrId", qrId);
                List<Res> resList = resMapper.selectByExample(ex);
                if (resList.size() == 0) {
                    //记录操作行为
                    List<Integer> qrIdList = new ArrayList<>();
                    qrIdList.add(qrId);
                    operationService.record(qrIdList,Constant.BOOKQR,request);
                    // 删除文件及文件数据
                    BookQr bookQr = bookQrService.selectByPrimaryKey(qrId);
                    // 删除qr数据
                    bookQrService.deleteByPrimaryKey(qrId);
                    if (bookQr != null) {
                        Example ex2 = new Example(FileRes.class);
                        ex2.createCriteria().andEqualTo("uuid", bookQr.getUrl());
                        List<FileRes> frList = fileResMapper.selectByExample(ex2);
                        for (FileRes fr : frList) {
                            File file = new File(PathUtil.getAppRootPath(request) + fr.getPath());
                            //判断资源是否存在
                            if (file.exists()) {
                                file.delete();
                            }
                            fileResService.deleteByPrimaryKey(fr.getId());
                        }
                    }
                }
            }

        }

        BookQr bookQr = bookQrMapper.selectByPrimaryKey(qrId);
        if(bookQr!=null){
            //主动触发云端压缩
            this.compress(qrId);
        }
    }


    /**
     * 验证密码是否正确
     *
     * @param id       二维码id
     * @param email
     * @param password
     * @return
     * @author hesufang
     * @date 2015.11.4
     */
    public boolean visitPassword(Integer id, String email, String password) throws Exception {
        //1.根据课件二维码id 查找图书id
        BookQr bookQr = bookQrService.selectByPrimaryKey(id);
        if (null == bookQr) {
            log.warn("课件不存在");
            throw new ServiceException("课件不存在");
        }

        //2。判断密码是否正确
        return clientAttrService.existPass(email, bookQr.getBookId(), password);
    }


    /**
     * 根据课件二维码  如果打包将对应的课件资源打包压缩
     *
     * @param id       课件二维码id
     * @param email    客户邮箱
     * @param password 客户密码
     * @param request
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.3
     */
    public File export(Integer id, String email, String password, Integer resId, HttpServletRequest request) throws Exception {

        //1.判断该资源是否存在
        Example example1 = new Example(Res.class);
        example1.createCriteria().andEqualTo("qrId", id);
        List<Res> resList = this.resMapper.selectByExample(example1);
        if (resList == null || resList.size() < 1) {
            throw new ServiceException("课件资源不存在，或已删除");
        }

        //判断该资源是否共享
        if (Constant.SECRECY == resList.get(0).getSecrecy()) {
            //验证密码是否正确
            if (!visitPassword(id, email, password)) {
                throw new ServiceException("密码错误");
            }
        }
        if (email != null && !"".equals(email.trim())) {
            //添加下载次数
            mailService.addMailAndCount(bookQrService.selectByPrimaryKey(id), email, 2);
        }
        //打包下载所有的
        if (resId == 0) {
            return exportZIP(id, request);
        } else {
            return getFileResByResId(resId, request);
        }


    }


    /**
     * 根据资源id  返回课件文件
     *
     * @param resId
     * @param request
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.3
     */
    private File getFileResByResId(Integer resId, HttpServletRequest request) throws Exception {

        Res res = resMapper.selectByPrimaryKey(resId);
        if (res == null) {
            log.warn("该资源不存在resId：" + resId);
            throw new ServiceException("资源不存在");
        }
        FileRes fileRes = new FileRes();
        fileRes.setUuid(res.getFileUuid());
        fileRes = fileResService.selectOne(fileRes);
        File file = null;
        if (null != fileRes) {
            file = new File(PathUtil.getAppRootPath(request) + fileRes.getPath());
        }

        if (!file.exists()) {
            log.warn("该资源对应的文件不存在resId：" + resId);
            throw new ServiceException("资源不存在");
        }
        return file;
    }


    /**
     * 课件文件打包
     *
     * @param id
     * @param request
     * @return
     * @throws Exception
     * @author hesufang
     * @date 2015.11.4
     */
    private File exportZIP(Integer id, HttpServletRequest request) throws Exception {

        Book book = bookService.selectByPrimaryKey(bookQrService.selectByPrimaryKey(id).getBookId());
        //获取该课件二维码下所有课件资源列表
        List<FileRes> fileReses = this.getFileResesByQrId(id);

        //生成zip压缩路径
        String uuid = UUID.randomUUID().toString();
        JDateTime jDateTime = new JDateTime();
        String filePath = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "zip" + File.separator + jDateTime.toString("YYYYMMDD") + File.separator + uuid;
        File zip = new File(filePath);
        //创建文件夹
        boolean flag = true;
        if (!zip.exists())
            flag = zip.mkdirs();
        if (!flag) {
            throw new Exception(I18n.getMessage("error"));
        }

        //递归文件  设置需要压缩文件目录列表
        //递归文件  设置需要压缩文件目录列表
        // List<String> srcFiles = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        int i = 1;
        for (FileRes fileRes : fileReses) {
            if (null == fileRes)
                continue;
            String name = fileRes.getName() + "." + fileRes.getSuffix();
            // srcFiles.add(PathUtil.getAppRootPath(request)+fileRes.getPath());
            if (map.containsValue(name)) {
                name = fileRes.getName() + "(" + i + ")" + "." + fileRes.getSuffix();
                i++;
            }
            map.put(PathUtil.getAppRootPath(request) + fileRes.getPath(), name);
        }


        //压缩成zip
        String destZipPath = zip.getPath() + File.separator + book.getName() + ".zip";
        ZipCompressor zipCompressor = new ZipCompressor(destZipPath);
        zipCompressor.compressByFileName(map);
        File destZip = new File(destZipPath);
        if (destZip.exists()) {
            return destZip;
        }
        return null;

    }

    /**
     * 根据课件二维码 查询所有课件资源列表
     *
     * @param qrId
     * @return
     * @author hesufang
     * @date 2015.11.3
     */
    public List<FileRes> getFileResesByQrId(int qrId) {

        //1.查询出该课件二维码下所有课件uuid
        Example example1 = new Example(Res.class);
        example1.createCriteria().andEqualTo("qrId", qrId);
        List<Res> resList = this.resMapper.selectByExample(example1);
        if (resList == null || resList.size() < 1) {
            throw new ServiceException("课件资源不存在，或已删除");
        }

        //2.根据uuid 查询所有资源
        List<Object> fileUUidList = new ArrayList<>();
        for (Res res : resList) {
            fileUUidList.add(res.getFileUuid());
        }
        Example example2 = new Example(FileRes.class);
        example2.createCriteria().andIn("uuid", fileUUidList);
        List<FileRes> fileReses = this.fileResService.selectByExample(example2);

        return fileReses;

    }

    /**
     * 保存或更新申请信息
     *
     * @param client
     * @param clientBookVOList
     * @return 1代表手机号被信任，可以直接查看资源；0走原来流程
     * @author yanfei
     * @date 2015.11.6
     */
    @Transactional
    public void mergeClientDetail(Client client, List<ClientBookVO> clientBookVOList, Integer qrId) throws Exception {
        //第一步 处理client
        List<ClientBook> clientBookList = new ArrayList<>();
        Client oldClient = null;// 老客户信息
        String openId = client.getOpenId();// 唯一的openId
        Example clientExample = new Example(Client.class);
        clientExample.createCriteria().andEqualTo("openId", openId);
        List<Client> oldClientList = clientService.selectByExample(clientExample);
        if (oldClientList != null && oldClientList.size() > 0) {
            oldClient = oldClientList.get(Constant.ZERO);
        }
        if (oldClient != null) {
            int clientId = oldClient.getId();
            // 1、更新client
            client.setId(clientId);// 赋值id
            clientService.updateByPrimaryKeySelective(client);
            // 2、清空clientBook
            Example deleteExample = new Example(ClientBook.class);
            deleteExample.createCriteria().andEqualTo("clientId", clientId);
            clientBookService.deleteByExample(deleteExample);
            // 3、重新绑定，更新clientBook
            if (clientBookVOList != null && clientBookVOList.size() > 0) {
                for (ClientBookVO clientBookVO : clientBookVOList) {
                    clientBookVO.setClientId(clientId);// 赋值clientId
                    clientBookList.add(mapper.map(clientBookVO, ClientBook.class));
                }
                clientBookService.insertClientBookForList(clientBookList);
            }
        } else {
            // 保存
            clientService.insertSelective(client);
            if (clientBookVOList != null && clientBookVOList.size() > 0) {
                for (ClientBookVO clientBookVO : clientBookVOList) {
                    clientBookVO.setClientId(client.getId());// 赋值clientId
                    clientBookList.add(mapper.map(clientBookVO, ClientBook.class));
                }
                clientBookService.insertClientBookForList(clientBookList);
            }
        }

        //第二步 构造邮件
        BookQr bookQr = bookQrService.selectByPrimaryKey(qrId);//书名和课件信息
        int bookId = bookQr.getBookId();
        Book book = bookService.selectByPrimaryKey(bookId);

        List<BookQRFile> bqFileList = new ArrayList<BookQRFile>();
        List<FileRes> resList = new ArrayList<>();
        BookQRFile bookQRFile = null;
//        BookQr bQr = null;
//
//        List<BookQr> bqList = bookQrService.getListByBookId(bookId);
//        if (bqList != null && bqList.size() > 0) {
//            bQr = bqList.get(0);
//        }
        if (StringUtils.isEmpty(bookQr.getNetUrl())) {
            //非网站链接二维码,其下面肯定有资源
            resList = this.getFileResesByQrId(qrId);
        }
        for (FileRes fileRes : resList) {
            bookQRFile = new BookQRFile();
            bookQRFile.setName(fileRes.getName());
            bookQRFile.setSize(FileUtil.convertFileSize(fileRes.getSize()));
            bqFileList.add(bookQRFile);
        }
        // 客户信息生成内容模版
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("book", book);
        dataMap.put("bqList", bqFileList);
        dataMap.put("client", client);
        dataMap.put("books", clientBookVOList);
        //预览地址
        if(StringUtils.isEmpty(bookQr.getNetUrl())){
            dataMap.put("url", Constant.COURSE_QR_HTTP.replaceAll("coursewareOne", "coursewareThr") + bookQr.getId() + "&authentication=" + client.getEmail() + "&openId=oUg14laiwefnv_lualhvla_89fPlIqPvNp");
        }else{
            dataMap.put("url", bookQr.getNetUrl());
        }
//        String context =  mdoc.createHTML(dataMap) ;
        String subject = "文泉云盘移动阅读平台审核信息";
        String[] toMail = new String[1];
        //判断是否直接发送客户
        Example example1 = new Example(Res.class);
        example1.createCriteria().andEqualTo("qrId", bookQr.getId());
        List<Res> reses = selectByExample(example1);
        //直接发送客户
        if (bookQr.getIsSend() == 2) {
            toMail = new String[]{client.getEmail()};
        } else {
            try {
                toMail = new String[]{bookService.getEmail(bookId).getAuditorEmail()};
            } catch (NullPointerException e) {
                log.error("未设置审核信息，联系管理员设置");
                throw new ServiceException("发送审核信息失败");
            }
        }

        //添加数据库记录(有就修改，没有新增)
        ClientAttr clientAttr = new ClientAttr();
        clientAttr.setClientId(client.getId());
        clientAttr.setType(Constant.ONE);

        clientAttr.setBookId(bookId);
        clientAttr.setQrId(qrId);
        List<ClientAttr> attrs = clientAttrService.select(clientAttr);

        clientAttr.setEmail(client.getEmail());
        clientAttr.setTarget(client.getObjective());
        clientAttr.setPassword("");

        if (attrs == null || attrs.size() == 0) {
            //如果不是直接发送客户,则需要插入审核列表
            if ((bookQr.getIsSend() != 2)) {
                //置为待审核
                clientAttr.setStatus(Constant.ONE);
                clientAttr.setCreateTime(new Date());
                clientAttrService.insertSelective(clientAttr);
            }else{
                //置为自动审核通过
                clientAttr.setStatus(4);
                clientAttr.setCreateTime(new Date());
                clientAttrService.insertSelective(clientAttr);
            }
        } else {
            if ((bookQr.getIsSend() != 2)) {
                //置为待审核
                clientAttr.setStatus(Constant.ONE);
            }else{
                //置为自动审核通过
                clientAttr.setStatus(4);
            }
            clientAttr.setCreateTime(new Date());
            Example example = new Example(ClientAttr.class);
            example.createCriteria().andEqualTo("clientId", clientAttr.getClientId()).andEqualTo("bookId", clientAttr.getBookId()).andEqualTo("qrId",clientAttr.getQrId());
            clientAttrService.updateByExampleSelective(clientAttr, example);
            clientAttr = attrs.get(0);
        }

        //如果不是直接发送客户,则需要添加审核按钮
        if ((bookQr.getIsSend() != 2)) {
            dataMap.put("passUrl", Constant.emailExamineQrHttp + clientAttr.getId());
        }
        String context = "";
        //直接发送客户
        if (bookQr.getIsSend() == 2) {
            //预览地址,网站链接类型二维码直接显示链接,否则显示资源列表
            if(StringUtils.isEmpty(bookQr.getNetUrl())){
                dataMap.put("url",Constant.COURSE_QR_HTTP.replaceAll("coursewareOne","coursewareThr")+bookQr.getId()+"&email="+client.getEmail()+"&openId="+client.getOpenId());
            }else{
                dataMap.put("url", bookQr.getNetUrl());
            }
            DocumentEmailHandler mdoc = new DocumentEmailHandler();
            context = mdoc.createHTML(dataMap);
        }else{
            DocumentHandler mdoc = new DocumentHandler();
            context = mdoc.createHTML(dataMap);
        }
        //发送邮件
        mailSender.sendRichTextMail(context, subject, toMail);
    }

    /**
     * 替换二维码
     *
     * @param resourceVO
     * @param request
     * @return
     * @author yanfei
     * @date 2015.11.7
     */
    @Transactional
    public void replaceResource(ResourceVO resourceVO, HttpServletRequest request) {
        // 二维码ID
        int qrId = resourceVO.getId();
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(qrId);
        // 获取UUID集合

        List<FilesVo> newFiles = resourceVO.getNewFiles();// 新资源UUID
        // 获取新资源ids
        List<Object> oldIds = resourceVO.getOldIds();// 旧资源ID
        // 删除老数据和老文件
        Example example = new Example(Res.class);
        example.selectProperties("fileUuid,downLoad");
        example.createCriteria().andIn("id", oldIds);
        List<Res> resList = selectByExample(example);
        // 1、删除旧资源记录、文件
        if (resList != null && resList.size() > 0) {
            for (Res res : resList) {
                String uuid = res.getFileUuid();// uuid
                fileResService.deleteForFileByUUID(uuid, request);
                Example ep = new Example(FileRes.class);
                ep.createCriteria().andEqualTo("uuid", uuid);
                fileResMapper.deleteByExample(ep);
                resMapper.delete(res);
                //删除与资源绑定的反馈
                Report report = new Report();
                report.setFileId(res.getId());
                reportService.delete(report);
                /**
                 * 向出版社删除图书和资源关系
                 */
                userCenterService.deleteBookRes(res.getId());
            }
        }

        // 查询新资源详细

        for (FilesVo file : newFiles) {
            String uuid = UUID.randomUUID().toString();
            Res res = new Res();
            res.setQrId(qrId);

            res.setOnwer(CurrentUser.getOrgId());
            res.setSecrecy(2);

            res.setFileUuid(uuid);
            res.setIsSend(1);
            res.setNum(0);
            //替换的时候  是否可下载属性
            res.setDownLoad(resList.get(0).getDownLoad());


            FileRes fileRes = convertFileRes(file);                   // 根据文件UUID获取文件信息
            fileRes.setUuid(uuid);
            fileResMapper.insertSelective(fileRes);

            res.setFileType(getFileTypeBySuffix(fileRes.getSuffix()));
            if (res.getFileType() == 1){
                //提取文本内容
                res.setText(ReadTextUtil.readText(PathUtil.getAppRootPath(request) + fileRes.getPath()));
            }
            resMapper.insertSelective(res);
            /**
             * 把图书资源关系传给智学云
             */
            if(bookQr != null){
                userCenterService.bindBookRes(bookQr.getBookId(),bookQr.getQrType(),res.getId());
            }
        }

        //主动触发云端压缩
        this.compress(qrId);

    }

    /**
     * 根据申请人查询对应的申请书名ID
     *
     * @param clientIdList
     * @return
     * @author yanfei
     * @date 2015.12.9
     */
    private Map<Integer, Integer> getBookIdMap(List<Object> clientIdList) {
        Map<Integer, Integer> bookIdMap = new HashMap<>();// 申请人对应的书ID
        Example attrExample = new Example(ClientAttr.class);
        attrExample.createCriteria().andIn("clientId", clientIdList);
        List<ClientAttr> clientAttrList = clientAttrService.selectByExample(attrExample);
        for (ClientAttr attr : clientAttrList) {
            bookIdMap.put(attr.getClientId(), attr.getBookId());
        }
        return bookIdMap;
    }

    /**
     * 客户管理列表
     *
     * @param keyword 申请人邮箱或者申请人姓名
     * @param status
     * @param bookId
     * @param page
     * @param pageSize
     * @return
     * @author zml
     */
    public JsonResultPage applyList(String keyword,int status, int bookId, int page, int pageSize) {
        Example example = new Example(ClientAttr.class);
        example.setOrderByClause("create_Time desc");
        example.selectProperties("clientId");
        example.setDistinct(true);
        Example.Criteria criteria = example.createCriteria();
        Example.Criteria criteria2 = example.or();
        if (!StringUtils.isEmpty(keyword)) {
            //匹配邮箱
            criteria.andEqualTo("email",keyword);
            //匹配姓名
            List list = new ArrayList<>();
            Example exampleC = new Example(Client.class);
            Example.Criteria criteriaC = exampleC.createCriteria();
            criteriaC.andLike("name", "%" + keyword + "%");
            List<Client> clientList = clientMapper.selectByExample(exampleC);
            if(clientList!=null && clientList.size()!=0){
                for(Client client:clientList){
                    list.add(client.getId());
                }
                criteria2.andIn("clientId",list);
            }
        }
        if (status != 0) {
            criteria.andEqualTo("status", status);
        }
        if (bookId != 0) {
            criteria.andEqualTo("bookId", bookId);
        }
        criteria.andEqualTo("type", 1);
        RowBounds rowBounds = new RowBounds(page, pageSize);
        JsonResultPage jsonResultPage = clientAttrService.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<ClientAttr> clientAttrList = jsonResultPage.getItems();
        if (clientAttrList.size() == 0) {
            return jsonResultPage;
        }
        List<ClientDetailVO> voList = new ArrayList<>();
        for (ClientAttr ca : clientAttrList) {
            ClientDetailVO cdv = new ClientDetailVO();
            // 根据客户clientId查询用户信息
            Client c = clientMapper.selectByPrimaryKey(ca.getClientId());
            if (c != null) {
                cdv.setTeach(c.getTeach());
                cdv.setName(c.getName());
                cdv.setEmail(c.getEmail());
                cdv.setMajor(c.getMajor());
                cdv.setSchool(c.getSchool());
                cdv.setJob(c.getJob());
                cdv.setPhone(c.getPhone());
                cdv.setAge(c.getAge());
                cdv.setSeat(c.getSeat());
                cdv.setDepart(c.getDepart());
                cdv.setSex(c.getSex());
                // 查询客户所申请的图书集合
                Example caEx = new Example(ClientAttr.class);
                Example.Criteria caCr = caEx.createCriteria();
                caCr.andEqualTo("clientId", ca.getClientId());
                if (status != 0) {
                    caCr.andEqualTo("status", status);
                }
                List<ClientAttr> caList = clientAttrMapper.selectByExample(caEx);
                if (caList!=null && caList.size() != 0){
                    List<ClientBookDetailVo> books = new ArrayList<>();
                    for (ClientAttr clientAttr : caList) {
                        Book book = bookMapper.selectByPrimaryKey(clientAttr.getBookId());
                        ClientBookDetailVo vo = new ClientBookDetailVo(book);
                        vo.setStatus(clientAttr.getStatus());
                        vo.setCreateTime(clientAttr.getCreateTime());
                        //查询qrName
                        BookQr bookQr = bookQrMapper.selectByPrimaryKey(clientAttr.getQrId());
                        if(bookQr != null){
                            vo.setQrName(bookQr.getName());
                        }else{
                            /*
                             *因为t_client_attribute中的qr_Id字段是后来加的,所以在加字段之前,表中的数据这个字段的值都为0；
                             * 而id为0的二维码在数据库中不存在,但是前端需要返回qrName,所以只好查出id为bookId的图书下面，任意一个二维码
                             */
                            BookQr bookQrDemo = new BookQr();
                            bookQrDemo.setBookId(clientAttr.getBookId());
                            List<BookQr> bookQrList = bookQrMapper.select(bookQrDemo);
                            if(bookQrList!=null && bookQrList.size()!=0){
                                BookQr bq = bookQrList.get(0);
                                vo.setQrName(bq.getName());
                            }else{
                                vo.setQrName("此二维码已被删除!");
                            }
                        }
                        books.add(vo);
                    }
                    cdv.setBookItems(books);
                }
            }
            voList.add(cdv);
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), page, pageSize, voList));
    }


    /**
     * 申请信息列表
     *
     * @param bookId
     * @param page
     * @param pageSize
     * @return
     * @author yanfei zml修改
     * @date 2015.12.9
     */

    public JsonResultPage applyList2(int status, int bookId, int page, int pageSize) {
        Page<ClientDetailVO> voPage = null;
        List<ClientDetailVO> voList = null;
        Example example = new Example(Client.class);
        example.setOrderByClause("id DESC");

        Example ex = new Example(ClientAttr.class);
        Example.Criteria criteria = ex.createCriteria();
        ex.selectProperties("clientId");
        if (bookId != 0) {
            criteria.andEqualTo("bookId", bookId);
        }
        if (status != 0) {
            criteria.andEqualTo("status", status);
        }
        criteria.andEqualTo("type", 1);
        List<Object> ids = new ArrayList<>();
        List<ClientAttr> clientAttrList = clientAttrService.selectByExample(ex);
        if (clientAttrList.size() == 0) {
            return new JsonResultPage(new Page(0, page, pageSize, clientAttrList));
        }
        for (ClientAttr ca : clientAttrList) {
            ids.add(ca.getClientId());
        }
        example.createCriteria().andIn("id", ids);
        RowBounds rowBounds = new RowBounds(page, pageSize);
        JsonResultPage clientPage = clientService.selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        if (clientPage != null) {
            List<Client> clientList = (List<Client>) clientPage.getItems();
            if (clientList != null && clientList.size() > 0) {
                voList = mapper.mapList(clientList, ClientDetailVO.class);
                List<Object> voIdList = new ArrayList<>();// 申请人ID
                for (ClientDetailVO vo : voList) {
                    voIdList.add(vo.getId());
                }
                Map<Integer, Integer> bookIdMap = getBookIdMap(voIdList);
                for (ClientDetailVO vo : voList) {
                    Integer bid = bookIdMap.get(vo.getId());
                    if (bid != null) {
                        Example bookExample = new Example(Book.class);
                        bookExample.createCriteria().andEqualTo("id", bid);
                        List<Book> bookList = bookService.selectByExample(bookExample);
                        if (bid != null && bookList != null && bookList.size() > 0) {
                            vo.setApplyBookName(bookList.get(Constant.ZERO).getName());
                        }
                    }
                }
                voPage = new Page<>(clientPage.getTotal(), clientPage.getNum(), clientPage.getSize(), voList);
            }
        }
        return new JsonResultPage(voPage);
    }

    /**
     * @param qrId
     * @return
     */
    public int getSecrecyByqrId(Integer qrId) {
        Integer i = 0;
        List<Object> list = resMapper.getSecrecyByqrId(qrId);
        if (list != null && list.size() > 0) {
            i = (Integer) list.get(0);
        }
        return i.intValue();
    }

    /**
     * 资源下载排行
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @author liukailong
     * @date 2016.9.10
     */
    public JsonResultPage downLoadRank2(String keyword, int page, int pageSize, int bookId, int qrcodeId) {
        List<ResCountVo> resVOList = new ArrayList<>();
        Page<ResCountVo> resVOPage = null;
        List<Object> qrIdList = new ArrayList<Object>();
        Example qrExample = new Example(BookQr.class);
        Example.Criteria qrCriteria = qrExample.createCriteria();

        if (bookId != 0) {
            qrCriteria.andEqualTo("bookId", bookId);
        }

        if (qrcodeId != 0) {
            qrCriteria.andEqualTo("id", qrcodeId);
        }
        List<BookQr> bookQrList = bookQrService.selectByExample(qrExample);
        if (bookQrList != null && bookQrList.size() > 0) {
            for (BookQr qr : bookQrList) {
                qrIdList.add(qr.getId());
            }
        } else {
            return new JsonResultPage(new Page());
        }

        RowBounds rowBounds = null;
        JsonResultPage resPage = null;
        Example resExample = new Example(Res.class);
        Example.Criteria resCriteria = resExample.createCriteria();
        List<Object> uuidList = new ArrayList<Object>();
        if (!StringUtils.isEmpty(keyword)) {
            //文件
            Example fileExample = new Example(FileRes.class);
            Example.Criteria fileCriteria = fileExample.createCriteria();
            fileCriteria.andLike("name", "%" + keyword + "%");
            List<FileRes> list = fileResService.selectByExample(fileExample);
            if (list != null && list.size() > 0) {
                for (FileRes res : list) {
                    uuidList.add(res.getUuid());
                }
            }
            resCriteria.andIn("fileUuid", uuidList);
        }

        resCriteria.andIn("qrId", qrIdList);
        resExample.setOrderByClause("num DESC");
        rowBounds = new RowBounds(page, pageSize);
        resPage = selectByExampleAndRowBounds2JsonResultPage(resExample, rowBounds);
        if (resPage != null) {
            List<Res> resList = (List<Res>) resPage.getItems();
            for (Res res : resList) {
                ResCountVo vo = new ResCountVo();
                BookQr bookQr = bookQrService.selectByPrimaryKey(res.getQrId());
                vo.setQrId(bookQr.getId());
                vo.setQrName(bookQr.getName());
                Book book = bookService.selectByPrimaryKey(bookQr.getBookId());
                vo.setBookId(book.getId());
                vo.setBookName(book.getName());
                vo.setResourceId(res.getId());
                vo.setResourceId(res.getId());
                vo.setNum(res.getNum());
                //根据文件ID获取文件信息
                FileRes fileRes = new FileRes();
                fileRes.setUuid(res.getFileUuid());
                FileRes reFileRes = fileResService.selectOne(fileRes);
                vo.setResourceName(reFileRes.getName());
                resVOList.add(vo);
            }
        }
        resVOPage = new Page<>(resPage.getTotal(), resPage.getNum(), resPage.getSize(), resVOList);
        return new JsonResultPage(resVOPage);
    }

    /**
     * 资源下载排行
     *
     * vo.setEditor(book.getEditor());
     * vo.setCode(book.getCode());
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     * @author zml
     * @date 2016.9.10
     */
    public JsonResultPage downLoadRank(String beginTime, String endTime, Integer flag, String keyword, int page, int pageSize, int bookId, int qrcodeId, int pressId) throws Exception {
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;

        Map<String, Object> map = new HashMap<>();
        map.put("createBegin", beginTime);
        map.put("createEnd", endTime);
        map.put("formal", formal);
        map.put("page", (page-1)*pageSize);
        map.put("pageSize", pageSize);
        map.put("bookId", bookId);
        map.put("qrcodeId", qrcodeId);
        map.put("pressId", pressId);
        map.put("keyword", keyword);
        Long count = resMapper.downLoadRankCount(map);
        if (count != null && count != 0) {
            List<ResCountVo> resVOList = resMapper.downLoadRank(map);
            return new JsonResultPage(new Page(count, page, pageSize, resVOList));
        }
        return new JsonResultPage(new Page(0, page, pageSize, new ArrayList<ResCountVo>()));
    }

    /**
     * 资源下载导出
     *
     * vo.setEditor(book.getEditor());
     * vo.setCode(book.getCode());
     *
     * @param bookId
     * @param qrcodeId
     * @return
     * @author zml
     * @date 2016.9.10
     */
    public void exportDownRank(String beginTime, String endTime, Integer flag, String keyword, int bookId, int qrcodeId, int pressId, HttpServletRequest request, HttpServletResponse response) throws Exception{
        int formal=0; //0：正式出版物 1：非正式出版物
        formal = flag==1?0:1;

        Map<String, Object> map = new HashMap<>();
        map.put("createBegin", beginTime);
        map.put("createEnd", endTime);
        map.put("formal", formal);
        map.put("bookId", bookId);
        map.put("qrcodeId", qrcodeId);
        map.put("pressId", pressId);
        map.put("keyword", keyword);
        List<ResCountVo> resVOList = resMapper.exportDownRank(map);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + (resVOList.size() != 0 ? "freemarker/bookDownLoadTemplate.xls" : "freemarker/bookDownLoadNoDataTemplate.xls") ;
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("下载统计.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("下载统计.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, resVOList, "resVOList", Constant.EXCEL_MAX_INDEX);
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
     * 资源下载导出
     *
     * @param bookId
     * @param qrcodeId
     * @return
     * @author liukailong
     * @date 2016.9.10
     */
    public void exportDownRank2(int pressId,int bookId, int qrcodeId, HttpServletRequest request, HttpServletResponse response) throws IOException{
        //准备数据
        List<ResCountVo> resVOList = new ArrayList<>();
        List<Object> qrIdList = new ArrayList<Object>();
        Example qrExample = new Example(BookQr.class);
        Example.Criteria qrCriteria = qrExample.createCriteria();

        if (pressId != 0) {
            List list = new ArrayList<>();

            Book bookExample = new Book();
            bookExample.setPressId(pressId);
            List<Book> bookList = bookService.select(bookExample);
            if(bookList!=null && bookList.size()!=0){
                for(Book book:bookList){
                    list.add(book.getId());
                }
                qrCriteria.andIn("bookId", list);
            }
        }

        if (bookId != 0) {
            qrCriteria.andEqualTo("bookId", bookId);
        }

        if (qrcodeId != 0) {
            qrCriteria.andEqualTo("id", qrcodeId);
        }
        List<BookQr> bookQrList = bookQrService.selectByExample(qrExample);
        if (bookQrList != null && bookQrList.size() > 0) {
            for (BookQr qr : bookQrList) {
                qrIdList.add(qr.getId());
            }
        } else {
            return ;
        }

        Example resExample = new Example(Res.class);
        Example.Criteria resCriteria = resExample.createCriteria();

        resCriteria.andIn("qrId", qrIdList);
        resExample.setOrderByClause("num DESC");
        List<Res> resList = resMapper.selectByExample(resExample);
        if(resList==null || resList.size()==0)
            return;

        for (Res res : resList) {
            ResCountVo vo = new ResCountVo();
            BookQr bookQr = bookQrService.selectByPrimaryKey(res.getQrId());
            vo.setQrId(bookQr.getId());
            vo.setQrName(bookQr.getName());
            Book book = bookService.selectByPrimaryKey(bookQr.getBookId());
            vo.setBookId(book.getId());
            vo.setBookName(book.getName());
            vo.setResourceId(res.getId());
            vo.setResourceId(res.getId());
            vo.setNum(res.getNum());
            //根据文件ID获取文件信息
            FileRes fileRes = new FileRes();
            fileRes.setUuid(res.getFileUuid());
            FileRes reFileRes = fileResService.selectOne(fileRes);
            vo.setResourceName(reFileRes.getName());
            resVOList.add(vo);
        }

        //列表为空
        if (resVOList == null || resVOList.size() == 0) {
            throw new ServiceException("无数据");
        }
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("resVOList", resVOList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/bookDownLoadTemplate.xls";
        InputStream in = null;
        OutputStream out = null;
        String filename= URLEncoder.encode("下载统计.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("下载统计.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, resVOList, "resVOList", Constant.EXCEL_MAX_INDEX);
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
     * 扩展资源新增
     *

     * @param filesVos
     * @param qrcodeId
     * @return
     */
    @Transactional

    public JsonResult resPush(List<FilesVo> filesVos, int qrcodeId, HttpServletRequest request) {
        BookQr bookQr = bookQrMapper.selectByPrimaryKey(qrcodeId);
        for (FilesVo file : filesVos) {
            String uuid = UUID.randomUUID().toString();
            // 根据UUID查询文件详细
            FileRes fileRes = convertFileRes(file);
            fileRes.setUuid(uuid);
            fileResMapper.insertSelective(fileRes);
            Res res = new Res();
            res.setFileName(fileRes.getName());
            res.setOnwer(CurrentUser.getOrgId());

            res.setFileType(getFileTypeBySuffix(fileRes.getSuffix()));
            if (res.getFileType() == 1){
                //提取文本内容
                res.setText(ReadTextUtil.readText(PathUtil.getAppRootPath(request) + fileRes.getPath()));
            }
            res.setFileId(fileRes.getFileId());
            res.setIsSend(1);
            res.setQrId(qrcodeId);
            res.setNum(0);
            res.setFileUuid(uuid);
            //默认为能下载
            res.setDownLoad(true);
            //设置index值
            int maxIndex = getMaxIndex(res.getQrId());
            res.setIndexs(maxIndex+1);
            resMapper.insertSelective(res);
            /**
             * 把图书资源关系传给智学云
             */
            if(bookQr != null){
                userCenterService.bindBookRes(bookQr.getBookId(),bookQr.getQrType(),res.getId());
            }
        }

        //主动触发云端压缩
        this.compress(qrcodeId);

        return new JsonResult(true, ConsHint.ADD_SUCCESS);
    }


    /**
     * 根据后缀名称获取文件类型
     * @param suffix
     * @return
     */
    public int getFileTypeBySuffix(String suffix){
        // 根据后缀名判断文件类型
        if (Constant.TXT.contains(suffix)) {
            return Constant.TXT_VALUE;
        } else if (Constant.PICTURE.contains(suffix)) {
            return Constant.PICTURE_VALUE;
        } else if (Constant.AUDIO.contains(suffix)) {
            return Constant.AUDIO_VALUE;
        } else if (Constant.VEDIO.contains(suffix)) {
            return Constant.VEDIO_VALUE;
        } else {
            return Constant.OTHER_VALUE;
        }
    }

    /**
     * 导出客户列表
     *
     * @param bookId
     * @param status
     * @param request
     * @param response
     * @autor zml
     */
    public void exportClient(int bookId, int pressId, int status, String keyword, HttpServletRequest request, HttpServletResponse response) throws IOException{
        List<ClientExportVO> cevList = clientAttrMapper.exportClient(bookId, pressId, status, keyword);
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("clientList", cevList);
        //模版配置文件路径
        String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + "freemarker/clientTemplate.xls";
        InputStream in = null;
        OutputStream out = null;

        String filename= URLEncoder.encode("读者信息列表.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
        if ("FF".equals(WebUtil.getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filename = new String(("读者信息列表.xls").getBytes("UTF-8"), "iso-8859-1");
        }
        try {
            response.setHeader("Content-disposition","attachment;filename=\"" + filename + "\"");
            response.setContentType("application/vnd.ms-excel");
            in = new BufferedInputStream(new FileInputStream(templateFileName));
            out = response.getOutputStream();
            ExcelUtil.generateExcelByTemplate(out, in, null, null, cevList, "clientList", Constant.EXCEL_MAX_INDEX);
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
     * 智学云打包下载二维码
     *
     * @param id
     */
    public JsonResultString yunZipDownload(int id,String openId, HttpServletRequest request, HttpServletResponse response){
        BookQr bookQr = bookQrService.selectByPrimaryKey(id);
        if(bookQr==null)
            return new JsonResultString(false,"二维码不存在或者已经被删除!");

        int status = bookQr.getZipStatus(); //1正在压缩2压缩成功;3压缩失败

        if(status==0){
            //主动触发压缩
            this.compress(id);
            return new JsonResultString(false,"二维码下面的资源正在云端压缩,请稍后再试!");
        }else if(status==1){
            if(bookQr.getZipZhixueid()==null){
                return new JsonResultString(false,"二维码下面的资源正在云端压缩,请稍后再试!");
            }else{
                return new JsonResultString(true,"获取成功!",bookQr.getZipZhixueid().toString());
            }

        }else if(status==2){
            return new JsonResultString(true,"获取成功!",bookQr.getZipZhixueid().toString());
        }else{
            //主动触发压缩
            this.compress(id);
            return new JsonResultString(false,"二维码下面的资源正在云端压缩,请稍后再试!");
        }

    }

    /**
     * 智学云打包下载二维码
     *
     * @param id
     */
    public JsonResultString downNum(int id, String openId, HttpServletRequest request, HttpServletResponse response) {
        //多文件下载分别统计下载量
        qrDownRankService.multipleFileDown(id, openId);
        return new JsonResultString(true, "获取成功!");
    }

    /**
     * 打包下载二维码
     *
     * @param id
     */
    public void zipDownload(int id, HttpServletRequest request, HttpServletResponse response) throws Exception{
        // ZIP生成的位置
        String rootPath = PathUtil.getAppRootPath(request);
        String path = rootPath + "files" + File.separator + "cache" + File.separator + id;
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        // 获取 图书名称
        String outZipPath = path + File.separator + "资源打包.zip";
        // 查询二维码的UUID
        Example example = new Example(Res.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qrId", id);
        criteria.andEqualTo("downLoad", true);
        List<Res> resList = resMapper.selectByExample(example);
        List<Object> uuIds = new ArrayList<>();
        for (Res r : resList) {
            uuIds.add(r.getFileUuid());
            Res res = selectByPrimaryKey(r.getId());
            res.setNum(res.getNum()+1);
            updateByPrimaryKeySelective(res);
        }
        Example ex = new Example(FileRes.class);
        Example.Criteria cri = ex.createCriteria();
        cri.andIn("uuid", uuIds);
        List<FileRes> fileResList = fileResMapper.selectByExample(ex);
        List<String> fileUrl = new ArrayList<>();
        String basepath = path+ File.separator+UUID.randomUUID().toString();
        File tempFile = new File(basepath);
        if (!tempFile.exists() && !tempFile.isDirectory()) {
            tempFile.mkdirs();
        }
        for (FileRes fr : fileResList) {
//            fileUrl.add(rootPath + fr.getPath());
            String fromUrl = rootPath + fr.getPath();
            String toUrl = basepath + File.separator + URLEncoder.encode(fr.getName(), "utf-8")+"."+fr.getSuffix();
            FileUtil.copyFile(fromUrl,toUrl,URLEncoder.encode(fr.getName(), "utf-8"),fr.getSuffix());
            String toUrl_decode = basepath + File.separator + URLDecoder.decode(fr.getName(),"utf-8")+"."+fr.getSuffix();
            fileUrl.add(toUrl_decode);
        }
        ZipUtils.createZip(outZipPath, new ArrayList<>(), fileUrl.toArray());
        FileUtil.download(outZipPath, request, response);
        // 删除打包文件夹
//        FileUtil.deleteDir(file);
    }

    /**
     * @param id
     * @return
     */
    public JsonResultData resDetail(int id) {
        Res res = resMapper.selectByPrimaryKey(id);
        Example example = new Example(FileRes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid", res.getFileUuid());
        List<FileRes> fileResList = fileResService.selectByExample(example);
        if (fileResList != null && fileResList.size() != 0) {
            FileRes fileRes = fileResList.get(0);
            res.setFileId(res.getFileUuid());
            res.setSuffix(fileRes.getSuffix());
            res.setFileName(fileRes.getName());
            res.setStatus(fileRes.getStatus());

            if(FileUtil.isSupportVideo(fileRes.getSuffix())){
                //视频下载要用七牛云id
                res.setDownUrl(fileRes.getQizhixueid().toString());
            }else{
                String viewurl = fileRes.getViewurl();
                if(viewurl.contains("http://cms.izhixue.cn")){
                    String arr[] = viewurl.split("=");
                    viewurl = arr[1];
                }
                res.setDownUrl(viewurl);
            }
            //预览用阿里云id
            res.setPath(fileRes.getZhixueid().toString());

        }
        return new JsonResultData(true, "查询明细成功", res);
    }

    /**
     * @param resId 资源id
     * @return
     */
    public ResourceMailVo getByResId(int resId) {
        Res res = selectByPrimaryKey(resId);
        String fileUuid = res.getFileUuid();
        int qrId = res.getQrId();

        BookQr bookQr = bookQrService.selectByPrimaryKey(qrId);
        Book book = bookService.selectByPrimaryKey(bookQr.getBookId());

        Example example = new Example(FileRes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("uuid", fileUuid);
        List<FileRes> fileResList = fileResService.selectByExample(example);
        if (fileResList != null && fileResList.size() != 0) {
            FileRes fileRes = fileResList.get(0);
            ResourceMailVo mailVo = new ResourceMailVo(res);
            mailVo.setResName(fileRes.getName());
            mailVo.setSize(fileRes.getSize());
            mailVo.setSuffix(fileRes.getSuffix());
            mailVo.setStatus(fileRes.getStatus());

            mailVo.setQrName(bookQr.getName());
            mailVo.setBookName(book.getName());

            return mailVo;
        }

        return null;
    }

    /**
     * 二维码资源压缩打包
     * @param id 二维码id
     * @return
     */
    @Transactional
    public JsonResult compress(int id) {
        BookQr bookqr = bookQrService.selectByPrimaryKey(id);
        if(bookqr==null)
            return new JsonResult(false,"二维码不存在!");

        int zipStatus = bookqr.getZipStatus(); //0初始状态1正在压缩2压缩成功;3压缩失败
        // 1.获取token
        String token = "";
        try {
            token = this.zhixueTokenService.getToken();
        } catch (Exception e) {
            log.error("获取智学云token失败！",e);
        }
        //2. 查询二维码资源的 zhixue
        String zhixueids="";

        Example example = new Example(Res.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qrId", id);
        criteria.andEqualTo("downLoad", true);
        List<Res> resList = resMapper.selectByExample(example);

        //二维码下面没有资源或者只有1个资源，不能压缩(资源数必须>=2才能压缩),这时把状态设置为0
        if(resList==null || resList.size()==0 || resList.size()==1){

            //设置打包状态为初始状态0
            bookqr.setZipStatus(0);
            bookQrService.updateByPrimaryKeySelective(bookqr);

            System.err.println("compress:二维码下面的资源数目少于2个,不能压缩!");
            return new JsonResult(false,"二维码下面的资源数目少于2个,不能压缩!");
        }

        List<Object> uuIds = new ArrayList<>();
        for (Res r : resList) {
            uuIds.add(r.getFileUuid());
            Res res = selectByPrimaryKey(r.getId());
            res.setNum(res.getNum()+1);
            updateByPrimaryKeySelective(res);
        }
        Example ex = new Example(FileRes.class);
        Example.Criteria cri = ex.createCriteria();
        cri.andIn("uuid", uuIds);
        List<FileRes> fileResList = fileResMapper.selectByExample(ex);

        //二维码下面没有资源或者只有1个资源，不能压缩(资源数必须>=2)
        if(fileResList==null || fileResList.size()==0 || fileResList.size()==1){
            System.err.println("compress:二维码下面的资源对应的文件数目少于2个,不能压缩!");
            return new JsonResult(false,"二维码下面的资源对应的文件数目少于2个,不能压缩!");
        }

        List<ZipPostVo> list =new ArrayList<>();
        for (FileRes fileRes : fileResList) {
            ZipPostVo vo = new ZipPostVo();

            String name = fileRes.getName();
            String suffix = fileRes.getSuffix();
            long zhixueid = fileRes.getZhixueid();
            vo.setRname(name+"."+suffix);

            if(FileUtil.isSupportVideo(suffix)){
                long qizhixueid = fileRes.getQizhixueid();//当资源是视频才会引用
                vo.setResourceId(String.valueOf(qizhixueid));
            }else{
                vo.setResourceId(String.valueOf(zhixueid));
            }
            list.add(vo);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("zipFiles", list);
        params.put("userId", userId);

        String strjson = new com.alibaba.fastjson.JSONObject(params).toString();
        System.out.println("strjson=="+strjson);

        String strResponse="";
        // 2.向云端发送压缩指令
//        String url = "http://api.izhixue.cn/FileManagement/ZipFile?ssotoken="+token;
        String url = host + "/resource/zip?token="+token;
        System.out.println("url=="+url);

        try {
            strResponse= HttpClientUtil.sendPost(url,strjson);
            if(StringUtils.isEmpty(strResponse)){
                throw new ServiceException("调用智学云接口返回空!");
            }
            System.out.println("strResponse:"+strResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        com.alibaba.fastjson.JSONObject object = JSON.parseObject(strResponse);
        String code = object.getString("code");
        if(!"0".equals(code)){
            throw new ServiceException("调用智学云压缩命令失败,状态码:"+code);
        }
        JSONObject data = object.getJSONObject("data");
        String PersistentId = data.getString("property");
        System.out.println("code=="+code+" PersistentId="+PersistentId);

        BookQr bookQr = new BookQr();
        bookQr.setId(id);
        bookQr.setZipStatus(1);//设置状态为正在压缩
        bookQr.setZipPersistentId(PersistentId);
        bookQrService.updateByPrimaryKeySelective(bookQr);

        //记录命令发送顺序
        ZipOrder zipOrder = new ZipOrder();
        zipOrder.setQrId(id);
        zipOrder.setZipPersistentid(PersistentId);
        zipOrderMapper.insertSelective(zipOrder);

        return new JsonResult(true,"开始压缩中...");
    }

    /**
     * 调用智学云压缩完成后,回调接口
     * @param result
     * @return
     */
    public void callback(ZhiZipResult result) {
        String callbackResult = JSON.toJSONString(result);
        System.out.println("callback result:"+callbackResult);
        this.saveZipCallbackResult(result);

        String code = result.getCode();//状态码 0：成功，1：等待处理，2：正在处理，3：处理失败，4：成功但通知失败。
        String desc = result.getDesc();//与状态码相对应的详细描述。
        String zipPersistentid = result.getId();

        BookQr bookQrExample = new BookQr();
        bookQrExample.setZipPersistentId(zipPersistentid);
        List<BookQr> bookQrList = bookQrMapper.select(bookQrExample);
        if(bookQrList==null || bookQrList.size()==0){
            return;
        }

        //1.查询回调的zipPersistentid是不是最后发出的，如果是才去修改数据库
        int bookQrId = bookQrList.get(0).getId();
        Example exampleZipOrder = new Example(ZipOrder.class);
        exampleZipOrder.setOrderByClause("id desc");
        Example.Criteria criteriaZipOrder = exampleZipOrder.createCriteria();
        criteriaZipOrder.andEqualTo("qrId",bookQrId);
        List<ZipOrder> zipOrderList = zipOrderMapper.selectByExample(exampleZipOrder);
        if(zipOrderList==null || zipOrderList.size()==0){
            return;
        }

        ZipOrder zipOrderExample = new ZipOrder();
        zipOrderExample.setZipPersistentid(zipPersistentid);
        zipOrderExample.setQrId(bookQrId);
        List<ZipOrder> zipOrders = zipOrderMapper.select(zipOrderExample);
        if(zipOrders==null || zipOrders.size()==0){
            return;
        }
        //回调的id不是最后发出的id,不去修改数据库
        int id1 = zipOrderList.get(0).getId();
        int id2 = zipOrders.get(0).getId();
        System.out.println("id1="+id1+" id2="+id2);
        if(zipOrderList.get(0).getId().intValue()!=zipOrders.get(0).getId().intValue()){
            System.err.println("回调的id不是最后发出的id,不去修改数据库");
            return;
        }

        //2.查询此二维码下面资源数是否小于2,小于2就把二维码状态置为初始化(0)
        boolean canZip = true;
        Example exampleRes = new Example(Res.class);
        Example.Criteria criteriaRes = exampleRes.createCriteria();
        criteriaRes.andEqualTo("qrId", bookQrId);
        criteriaRes.andEqualTo("downLoad", true);
        List<Res> resList = resMapper.selectByExample(exampleRes);

        //二维码下面没有资源或者只有1个资源，不能压缩(资源数必须>=2才能压缩),这时把状态设置为0
        if(resList==null || resList.size()==0 || resList.size()==1){
            canZip = false;
        }

        BookQr bookQr = new BookQr();
        if("0".equals(code)){
            if(result.getResourceId() != 0){
                bookQr.setZipPersistentId(zipPersistentid);
                bookQr.setZipStatus(canZip?2:0);                   //1正在压缩2压缩成功;3压缩失败
                bookQr.setZipZhixueid(result.getResourceId());

                Example example = new Example(BookQr.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("zipPersistentId",zipPersistentid);
                bookQrMapper.updateByExampleSelective(bookQr,example);
            }else{
                throw new ServiceException("code和resourceId都为0了!");
            }
        }else if("3".equals(code)){
            bookQr.setZipPersistentId(zipPersistentid);
            bookQr.setZipStatus(canZip?3:0);                //1正在压缩2压缩成功;3压缩失败
            Example example = new Example(BookQr.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("zipPersistentId",zipPersistentid);
            bookQrMapper.updateByExampleSelective(bookQr,example);

            log.error("二维码压缩持久化处理的进程ID="+zipPersistentid+"压缩失败"+" desc=="+desc);
        }

    }

    /**
     * 报错回调记录
     * @param result
     */
    private void saveZipCallbackResult(ZhiZipResult result){
        String zipPersistentid = result.getId();
        BookQr bookQr = new BookQr();
        bookQr.setZipPersistentId(zipPersistentid);
        bookQr.setZipCallbackContent(JSON.toJSONString(result));


        Example example = new Example(BookQr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("zipPersistentId", zipPersistentid);
        bookQrMapper.updateByExampleSelective(bookQr, example);
    }

    /**
     * 获取资源状态
     * @param resId
     * @return
     */
    public JsonResultId status(int resId){
        Res res = resMapper.selectByPrimaryKey(resId);
        if(res==null){
            return new JsonResultId(false,"资源不存在!");
        }

        FileRes fileRes = new FileRes();
        fileRes.setUuid(res.getFileUuid());
        List<FileRes> list = fileResMapper.select(fileRes);
        if(list == null || list.size()==0){
            return new JsonResultId(false,"资源对应的文件不存在!");
        }

        FileRes file = list.get(0);
        if(FileUtil.isSupportVideo(file.getSuffix())){
            if(file.getStatus().intValue()==30){
                int status = this.queryAliVideoStatus(file.getAlivideoid());
                if(status!=0 && status!=30){
                    file.setStatus(status);
                    fileResMapper.updateByPrimaryKeySelective(file);

                    return new JsonResultId(true,"",status);
                }else{
                    return new JsonResultId(true,"",file.getStatus());
                }
            }else{
                return new JsonResultId(true,"",file.getStatus());
            }
        }

        return new JsonResultId(false,"非mp4文件,没有状态!");
    }

    /**
     * 调用智学云接口,获取阿里云视频状态
     * 10表示可以正常播放；
     * 20表示转码失败；
     * 21表示审核失败；
     * 30表示正在处理过程中；
     * 31表示正在审核过程中；
     * @param videoid
     * @return
     */
    public int queryAliVideoStatus(String videoid){
        int resultCode = 0;
        // 1.get请求，获取token
        String token = "";
        try {
            token = this.zhixueTokenService.getToken();
        } catch (Exception e) {
            log.error("获取智学云token失败！",e);
        }

        // 2.获取视频状态
        final String url = host + "/resource/aliyun/video?videoId="+videoid+"&token=" + token;
        String resResult = HttpTookit.doGet(url, null, "utf-8", true);
        if(StringUtils.isEmpty(resResult)){
            throw new ServiceException("查询智学云接口无返回!");
        }
        log.debug("==resResult:"+resResult);
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(resResult);
        String code = jsonObject.getString("code");
        if("0".equals(code)){
            JSONObject data = jsonObject.getJSONObject("data");
            //Uploading、TranscodeFail、Normal
            String Status = data.getString("Status");
            //10表示可以正常播放； 20表示转码失败；
            if("TranscodeFail".equals(Status)){
                resultCode = 20;
            }else if("Normal".equals(Status)){
                resultCode =  10;
            }else if("Transcoding".equals(Status)){
                resultCode =  30;
            }
        }else if("401".equals(code)){
            resultCode =  30;
            //throw new ServiceException("暂无视频信息!");
        }else{
            throw new ServiceException("查询智学云接口出错!");
        }

        return resultCode;
    }


    /**
     * 资源限制下载
     * @param downLoad
     * @param ids
     * @return
     */
    public JsonResult updateFlag(boolean downLoad, String ids) {
        String[] id = ids.split(",");
        for (String idd : id) {
            if (!StringUtils.isEmpty(idd)){
                Res res = new Res();
                res.setDownLoad(downLoad);
                res.setId(Integer.valueOf(idd));
                resMapper.updateByPrimaryKeySelective(res);
                Res resource = resMapper.selectByPrimaryKey(res.getId());
                BookQr bookQr = bookQrService.selectByPrimaryKey(resource.getQrId());
                //主动触发云端压缩
                this.compress(bookQr.getId());
            }
        }
        return new JsonResult(true, "下载权限设置成功!");
    }

    /**
     * 二维码列表资源调整书序,index按照从小到大排序,小的再最上面
     * @param vo
     * @return
     */
    @Transactional
    public JsonResult setIndex(ResIndexVo vo) {

        int targetQrId = getQrIdByFlag(vo);
        if(targetQrId==0){
            return new JsonResult(false,"已经是最前面或者最后面,无法调整了");
        }

        Res oldRes = resMapper.selectByPrimaryKey(vo.getResId());
        Res newRes = resMapper.selectByPrimaryKey(targetQrId);
        //交换两者的index值
        int oldIndex = oldRes.getIndexs();
        oldRes.setIndexs(newRes.getIndexs());
        newRes.setIndexs(oldIndex);

        resMapper.updateByPrimaryKeySelective(oldRes);
        resMapper.updateByPrimaryKeySelective(newRes);

        return new JsonResult(true,"设置成功");
    }

    /**
     * 获取二维码下面资源次序(vo.getFlag 1上调2下调),如果非法返回0
     * @param vo
     * @return
     */
    private int getQrIdByFlag(ResIndexVo vo){
        Res res = resMapper.selectByPrimaryKey(vo.getResId());
        int flag = vo.getFlag();

        Example example = new Example(Res.class);
        if(flag==1){
            example.setOrderByClause("indexs desc");
        }else{
            example.setOrderByClause("indexs asc");
        }
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qrId",vo.getQrId());
        if(flag==1){
            criteria.andLessThan("indexs",res.getIndexs());
        }else{
            criteria.andGreaterThan("indexs",res.getIndexs());
        }

        List<Res> list = resMapper.selectByExample(example);

        if(list==null || list.size()==0){
            return 0;
        }else{
            return list.get(0).getId();
        }

    }

    /**
     * 二维码列表资源历史记录初始化
     * @return
     */
    public JsonResult initdata() {
        List<Integer> qrIdList = resMapper.getResNumGreaterThanOne();

        for(int qrId : qrIdList){
            Example example = new Example(Res.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("qrId",qrId);
            List<Res> resList = resMapper.selectByExample(example);
            if(resList!=null && resList.size()>1){
                for(int i=0;i<resList.size();i++){
                    Res res = resList.get(i);
                    res.setIndexs(i+1);
                    resMapper.updateByPrimaryKeySelective(res);
                }
            }
        }

        return new JsonResult(true,"执行成功");
    }

    /**
     * 下载资源接口
     * @return
     */
    public String downloadUrl(Long resourceId) {
        String token = userCenterService.getToken();
        String url = host + "/resource/download?token=" + token + "&resourceId=" + resourceId;
        return url;
    }

}
