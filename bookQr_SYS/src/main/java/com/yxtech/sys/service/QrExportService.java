package com.yxtech.sys.service;

import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.CurrentUser;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultList;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.BookQrAuthMapper;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.dao.QrExportMapper;
import com.yxtech.sys.domain.*;
import com.yxtech.sys.vo.bookQr.BookQrStyleVo;
import com.yxtech.sys.vo.bookQr.CoursewareDetailVO;
import com.yxtech.sys.vo.bookQr.ExportRecordVo;
import com.yxtech.sys.vo.count.BookCountVo;
import com.yxtech.sys.vo.count.QrCountVo;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.DateUtil;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hesufang on 2015/10/16.
 */

@Service
public class QrExportService extends BaseService<QrExport> {

    @Autowired
    private QrExportMapper qrExportMapper;
    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private FileResMapper fileResMapper;
    @Autowired
    private BookQrService bookQrService;
    @Autowired
    private QrUserInfoService qrUserInfoService;
    @Autowired
    private UserCenterService userCenterService;

    public QrExportMapper getQrExportMapper() {
        return qrExportMapper;
    }

    @Resource(name = "qrExportMapper")
    public void setQrExportMapper(QrExportMapper qrExportMapper) {
        setMapper(qrExportMapper);
        this.qrExportMapper = qrExportMapper;
    }

    /**
     * 导出权限二维码记录
     * @param bookId
     * @return
     */
    public JsonResultList exportRecord(Integer bookId,int type) {
        BookQrAuth QrAuth = new BookQrAuth();
        QrAuth.setBookId(bookId);
        List<BookQrAuth> bookQrAuthList = bookQrAuthMapper.select(QrAuth);
        if(bookQrAuthList==null || bookQrAuthList.size()==0)
            throw new ServiceException("没有查询到此图书的权限二维码!");

        BookQrAuth bookQrAuth = bookQrAuthList.get(0);

        Example ex = new Example(QrExport.class);
        ex.setOrderByClause("createTime desc");
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("qrId",bookQrAuth.getId());
        criteria.andEqualTo("type",type);
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
     * 导出 图书下面权限二维码 EXCEL
     *
     * @param bookId 图书id
     * @param number 倒出二维码数量
     * @param request   request
     * @throws IOException
     */
    public void authNumExportExcel(Integer bookId, Integer number, String data, HttpServletRequest request, HttpServletResponse response) throws Exception{
        // 准备数据
        BookQrAuth QrAuth = new BookQrAuth();
        QrAuth.setBookId(bookId);
        List<BookQrAuth> bookQrAuthList = bookQrAuthMapper.select(QrAuth);
        if(bookQrAuthList==null || bookQrAuthList.size()==0)
            throw new ServiceException("没有查询到此图书的权限二维码!");

        BookQrAuth bookQrAuth = bookQrAuthList.get(0);
        //获取网站根目录
        String rootPath = PathUtil.getAppRootPath(request);
        // BookQrAuth转换为bookQr
        BookQr bookQr = new BookQr(bookQrAuth);
        Book book = bookMapper.selectByPrimaryKey(bookQr.getBookId());

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
            fileRes = this.fileResMapper.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }
        //先 生成number个权限二维码,带序列号
        List<String> uuidList = userCenterService.getSerializedUuid(1,number,book.getCode());
        if(uuidList.size() != number){
            throw new ServiceException("出版社序列号中心接口调用失败!");
        }
        for(int i=0;i<number;i++){
//            String uuid = UUID.randomUUID().toString();
            String uuid = uuidList.get(i);
            String contents = Constant.authHttp + bookQr.getId() + "&authkey=" + uuid;

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
        String excelName = "权限二维码列表_"+book.getCode().trim()+new SimpleDateFormat("_YYYYMMddHHmm").format(new Date());
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
        qrExport.setQrId(bookQrAuth.getId());
        qrExport.setCreatorId(CurrentUser.getUser().getId());
        qrExport.setCreatorAccount(CurrentUser.getUser().getEmail());
        qrExport.setCreateTime(new Date());
        qrExport.setType(2);
        qrExportMapper.insertSelective(qrExport);
    }

    /**
     *
     * @param bookId
     * @param number
     * @param data
     * @param request
     * @param response
     * @throws Exception
     */
    public void authNumExport(Integer bookId, Integer number, String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BookQrAuth QrAuth = new BookQrAuth();
        QrAuth.setBookId(bookId);
        List<BookQrAuth> bookQrAuthList = bookQrAuthMapper.select(QrAuth);
        if(bookQrAuthList==null || bookQrAuthList.size()==0)
            throw new ServiceException("没有查询到此图书的权限二维码!");

        BookQrAuth bookQrAuth = bookQrAuthList.get(0);
        int qrcodeId = bookQrAuth.getId();

        File zip = bookQrService.authNumExport(number, qrcodeId, data, request);
        if (zip.exists()) {
            int len = 0;
            String fileName = URLEncoder.encode(zip.getName(), "utf-8"); //为了解决中文名称乱码问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                fileName = new String(zip.getName().getBytes("UTF-8"),
                        "iso-8859-1");
            }
            FileInputStream fis = new FileInputStream(zip);
            byte[] b = new byte[2048];
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
            //获取响应报文输出流对象
            ServletOutputStream out = response.getOutputStream();
            //输出
            while ((len = fis.read(b)) > 0) {
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
            qrExport.setType(2);
            qrExportMapper.insertSelective(qrExport);
        }
    }

    /**
     * 获取权限二维码明细
     * @param id
     * @param authkey
     * @return
     * @author hesufang
     * @date 2015.11.3
     */
    public boolean scanning(int id, String authkey, String openid, String code, String state){
        System.out.println(" 4.查询此权限二维码是否被扫过,begin="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        //查找qrUserInfo
        Example example = new Example(QrUserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qrId", id);
        criteria.andEqualTo("authkey", authkey);
        List<QrUserInfo> qrUserInfoList = qrUserInfoService.selectByExample(example);
        System.out.println(" 4.查询此权限二维码是否被扫过,end="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        if (qrUserInfoList != null && qrUserInfoList.size() != 0) {
            QrUserInfo qrUserInfo = qrUserInfoList.get(0);
            //如果查出的openid和用户openid不相同,证明这个二维码已被扫过
            if (!openid.equals(qrUserInfo.getOpenid())) {
                return false;
            }
        }

        return true;
    }


}
