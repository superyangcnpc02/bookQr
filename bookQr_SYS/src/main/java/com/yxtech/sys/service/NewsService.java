package com.yxtech.sys.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ConsHint;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.dao.NewsMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.News;
import com.yxtech.sys.vo.NewsStyleVo;
import com.yxtech.sys.vo.bookQr.BookQrStyleVo;
import com.yxtech.utils.file.FileUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import com.yxtech.utils.i18n.I18n;
import com.yxtech.utils.qr.QREncode;
import jodd.datetime.JDateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;

/**
 * Created by zml on 2016/9/10.
 */
@Service
public class NewsService extends BaseService<News> {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private FileResMapper fileResMapper;

    @Resource(name = "newsMapper")
    public void setNewsMapper(NewsMapper newsMapper) {
        setMapper(newsMapper);
        this.newsMapper = newsMapper;
    }

    /**
     * 宣传列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     * @author zml
     * @date 2016/9/9
     */
    public JsonResultPage getNewsList(int pageNo, int pageSize) {
        Example example = new Example(News.class);
        RowBounds rowBounds = new RowBounds(pageNo, pageSize);
        example.setOrderByClause("id DESC");
        JsonResultPage jsonResultPage = selectByExampleAndRowBounds2JsonResultPage(example, rowBounds);
        List<News> newsList = jsonResultPage.getItems();
        if (newsList.size() == Constant.ZERO) {
            return jsonResultPage;
        }
        return new JsonResultPage(new Page(jsonResultPage.getTotal(), pageNo, pageSize, newsList));
    }

    /**
     * 删除宣传
     *
     * @param id
     * @return
     * @author zml
     * @date 2016/9/9
     */
    @Transactional
    public JsonResult deleteNews(int id, HttpServletRequest request) {
        // 查询UUID
        News news = newsMapper.selectByPrimaryKey(id);
        if (news == null) {
            return new JsonResult(true, "该数据不存在或已被删除！");
        }
        // 删除二维码文件
        String serverPath = PathUtil.getAppRootPath(request);
        Example example = new Example(FileRes.class);
        example.createCriteria().andEqualTo("uuid", news.getUrl());
        List<FileRes> fileResList = fileResMapper.selectByExample(example);
        fileResMapper.deleteByExample(example);
        newsMapper.deleteByPrimaryKey(id);
        if (fileResList != null && fileResList.size() != 0) {
            for (FileRes fr : fileResList) {
                String path = serverPath + fr.getPath();
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    // 删除已生成的二维码的文件！
                    file.delete();
                }
                String folderPath = path.substring(0, path.lastIndexOf(File.separator));
                // 判断是否是空文件夹
                File fileFolder = new File(folderPath);
                if (fileFolder.isDirectory()) {
                    String[] files = fileFolder.list();
                    if (files.length > 0) {
                        System.out.println("目录 " + file.getPath() + " 不为空！");
                    } else {
                        fileFolder.deleteOnExit();
                    }
                }
            }
        }
        return new JsonResult(true, ConsHint.DELETE_SUCCESS);
    }

    /**
     * 新增宣传 需生成二维码
     *
     * @param news
     * @return
     * @author zml
     * @date 2016/9/14
     */
    @Transactional
    public JsonResult addNews(News news, HttpServletRequest request) {
        // 生成上传文件的路径信息，按天生成
        String pathUri = "WEB-INF" + File.separator + "books" + File.separator + "news" + File.separator + new JDateTime().toString("YYYYMMDD");
        String pathUrl = PathUtil.getAppRootPath(request) + pathUri;
        // 验证路径是否存在，不存在则创建目录
        File path = new File(pathUrl);
        if (!path.exists()) {
            path.mkdirs();
        }
        // 生成宣传的二维码
        String uuid = UUID.randomUUID().toString();
        int width = 240; // 二维码图片宽度 300
        int height = 240; // 二维码图片高度300
        String format = "png";// 二维码的图片格式 gif
        String outUrl = pathUrl + File.separator + uuid + ".png";
        File outputFile = new File(outUrl);//指定输出路径
        try {
            // 保存宣传数据
            news.setUrl(uuid);
            news.setCreateTime(new Date());
            newsMapper.insertSelective(news);
            // 生成二维码
            QREncode.encode_QR_CODE(Constant.NEWS_QR_HTTP + news.getId(), width, height, format, outputFile, null, request, "");
            // 保存文件详细
            FileRes fileRes = new FileRes();
            fileRes.setPath(pathUri + File.separator + uuid + ".png");
            fileRes.setName(news.getTitle());
            fileRes.setSuffix("png");
            fileRes.setUuid(uuid);
            File file = new File(outUrl);
            if (file.exists() && file.isFile()) {
                fileRes.setSize(Integer.parseInt(String.valueOf(file.length())));
            }
            fileResMapper.insertSelective(fileRes);
            System.out.println(news.getId());
        } catch (Exception e) {
            File file = new File(outUrl);
            if (file.exists() && file.isFile()) {
                // 删除已生成的二维码的文件！
                file.delete();
            }
            e.printStackTrace();
            return new JsonResult(false, ConsHint.ADD_FAILURE);
        }
        return new JsonResult(true, ConsHint.ADD_SUCCESS);
    }

    /**
     * 编辑宣传
     *
     * @param news
     * @return
     * @author zml
     * @date 2016/9/9
     */
    public JsonResult editNews(News news) {
        newsMapper.updateByPrimaryKeySelective(news);
        return new JsonResult(true, ConsHint.EDIT_SUCCESS);
    }


    /**
     * @param id
     * @return
     */
    public JsonResultData newsDetail(int id) {
        News news = newsMapper.selectByPrimaryKey(id);
        return new JsonResultData(true, "查询明细成功", news);
    }

    /**
     * 导出宣传二维码
     *
     * @param id
     * @param request
     * @param response
     * @autor zml
     */
    public void exportQrCode(int id, HttpServletRequest request, HttpServletResponse response) {
        // 查询UUID以及拼接文件地址
        String qrPath = "";             // 获取二维码位置
        String serverPath = PathUtil.getAppRootPath(request);
        News news = newsMapper.selectByPrimaryKey(id);
        if (news != null && news.getUrl() != null) {
            FileRes fr = new FileRes();
            fr.setUuid(news.getUrl());
            FileRes fileRes = fileResMapper.selectOne(fr);
            if (fileRes != null && fileRes.getUuid() != null) {
                qrPath = serverPath + fileRes.getPath();
            }
        }
        // 以ID命名创建文件夹
        File fileFolder = new File(serverPath + "WEB-INF" + File.separator + "books" + File.separator + "news" + File.separator + "cache" + File.separator + id);
        if (!fileFolder.exists() && !fileFolder.isDirectory()) {
            fileFolder.mkdirs();
        }
        try {
            String pdfName = news.getTitle();
            createPdf(qrPath, fileFolder.toString() + File.separator + pdfName + ".pdf", pdfName);
            downloadFile(new File(fileFolder.toString() + File.separator + pdfName + ".pdf"), request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.deleteDir(fileFolder);
        }
    }

    /**
     * 生成二维码
     *
     * @param qrPath  二维码的路径，pdf的内容
     * @param pdfPath 生成的pdf文件路径
     * @param pdfName 生成的pdf名称
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    private String createPdf(String qrPath, String pdfPath, String pdfName) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        // 处理中文
        FontSelector selector = new FontSelector();
        selector.addFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 12));
        selector.addFont(FontFactory.getFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
        document.add(selector.process(pdfName));
        Image image = Image.getInstance(qrPath);
        image.scaleAbsolute(200f, 200f);
        document.add(image);
        document.close();
        return pdfPath;
    }

    /**
     * 下载文件
     *
     * @param file
     * @param response
     * @autor zml
     */
    public static void downloadFile(File file, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (file.exists()) {
                int len = 0;
                String fileName = URLEncoder.encode(file.getName(), "UTF-8");
                if ("FF".equals(WebUtil.getBrowser(request))) {
                    // 针对火狐浏览器处理方式不一样了
                    fileName = new String(file.getName().getBytes("UTF-8"),
                            "iso-8859-1");
                }
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[2048];
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
                //获取响应报文输出流对象
                ServletOutputStream out = response.getOutputStream();
                //输出
                while ((len = fis.read(b)) > 0) {
                    out.write(b, 0, len);
                }
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 更新二维码样式
     *
     * @param vo 二维码样式VO
     * @return 消息
     */
    public Map<String, Object> editStyle(NewsStyleVo vo, HttpServletRequest request) throws Exception {

        Map<String, Object> map = new HashMap<>();
        //获取网址基础信息
        String rootPath = PathUtil.getAppRootPath(request);
        //获取临时的宽度高度信息
        int width = Integer.parseInt(vo.getWidth());
        //获取logo，默认是null
        File logo = null;
        FileRes fileRes = null;
        if (!StringUtils.isBlank(vo.getFileId())) {
            fileRes = new FileRes();
            fileRes.setUuid(vo.getFileId());
            fileRes = this.fileResMapper.selectOne(fileRes);
            logo = new File(rootPath + fileRes.getPath());
            if (!logo.exists() || !logo.isFile()) {
                logo = null;
            }
        }

        for(int id:vo.getIds()){
            //创建二维码
            String contents = Constant.NEWS_QR_HTTP + id;
            String fart = "png";

            // 生成上传文件的路径信息，按天生成
            String pathUri = "WEB-INF" + File.separator + "books" + File.separator + "news" + File.separator + new JDateTime().toString("YYYYMMDD");
            String pathUrl = PathUtil.getAppRootPath(request) + pathUri;
            File path = new File(pathUrl);
            if (!path.exists()) {
                path.mkdirs();
            }
            //指定输出路径
            String uuid = UUID.randomUUID().toString();
            String outUrl = pathUrl + File.separator + uuid + ".png";
            File outputFile = new File(outUrl);

            //生成二维码
            QREncode.encode_QR_CODE(contents, width, width, fart, outputFile, logo, request, vo.getText());

            //修改news
            News news = new News();
            news.setId(id);
            news.setUrl(uuid);
            newsMapper.updateByPrimaryKeySelective(news);
            //创建FILERES
            FileRes file = new FileRes();
            file.setUuid(uuid);
            file.setName("宣传二维码");
            file.setPath(pathUri+ File.separator + uuid + ".png");
            file.setSuffix(fart);
            file.setSize(0);
            this.fileResMapper.insert(file);
        }

        map.put("message", I18n.getMessage("success"));
        map.put("status", true);
        return map;
        }

}
