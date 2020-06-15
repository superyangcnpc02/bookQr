package com.yxtech.sys.service;


import com.yxtech.common.BaseService;
import com.yxtech.common.Constant;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.common.json.JsonResultPage;
import com.yxtech.common.json.Page;
import com.yxtech.sys.dao.BookMapper;
import com.yxtech.sys.dao.BookQrAuthMapper;
import com.yxtech.sys.dao.BookQrMapper;
import com.yxtech.sys.dao.QrExportMapper;
import com.yxtech.sys.domain.Book;
import com.yxtech.sys.domain.BookQr;
import com.yxtech.sys.domain.QrExport;
import com.yxtech.sys.vo.PowerQrVo;
import com.yxtech.utils.excel.ExcelUtil;
import com.yxtech.utils.file.PathUtil;
import com.yxtech.utils.file.WebUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lsn on 2018/1/9.
 */
@Service
public class ScratchService extends BaseService<Object> {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookQrAuthMapper bookQrAuthMapper;

    @Autowired
    private QrExportMapper qrExportMapper;

    @Autowired
    private BookQrMapper bookQrMapper;
    /**
     * 刮刮卡二维码列表
     * @param editor
     * @param isbns
     * @param keyword
     * @param orderTime
     * @param pressId
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    public JsonResultPage getList(Integer editor, String isbns, String keyword, String orderTime, Integer pressId, int type,int categoryId,int categorysuperId, int pageNo, int pageSize){
        Map<String,Object> map=new HashMap<>();
        map.put("editor",editor);
        map.put("keyword",keyword);
        map.put("orderTime",orderTime);
        map.put("pressId",pressId);
        map.put("type",type);
        map.put("pageNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        map.put("isbns",isbns);
        map.put("categoryId",categoryId);
        map.put("categorysuperId",categorysuperId);
        //验证isbns
        if(isbns!=null &&!"".equals(isbns)){
            if(!StringUtils.isEmpty(isbns)) {
                String[] codes = isbns.split(",");
                List<String> codesList = new ArrayList<>();
                for (String code : codes) {
                    if (!StringUtils.isEmpty(code)){
                        codesList.add(code);
                    }
                }
                map.put("size", codesList.size());
                if (codesList.size() == 1){
                    map.put("codeOne", codesList.get(0));
                }else if (codesList.size() != 0 && codesList.size() !=1){
                    map.put("codeSize", codesList);
                }
            }
        }
        int count = bookMapper.getBookCountByMap(map);
        if (count != 0) {
            List<Book> bookList=bookMapper.getList(map);
            return new JsonResultPage(new Page(count,pageNo,pageSize,bookList));
        }
        return new JsonResultPage(new Page(count,pageNo,pageSize,new ArrayList<Book>()));
    }

    /**
     * 导出刮刮卡二维码生成记录
     * @param editor
     * @param isbns
     * @param keyword
     * @param pressId
     * @param type
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportList(Integer editor, String isbns, String keyword, Integer pressId, int type,int categoryId,int categorysuperId, HttpServletRequest request, HttpServletResponse response)throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("editor", editor);
        map.put("keyword", keyword);
        map.put("pressId", pressId);
        map.put("type", type);
        map.put("isbns", isbns);
        map.put("categoryId",categoryId);
        map.put("categorysuperId",categorysuperId);
        //验证isbns
        if (isbns != null && !"".equals(isbns)) {
            if (!StringUtils.isEmpty(isbns)) {
                String[] codes = isbns.split(",");
                List<String> codesList = new ArrayList<>();
                for (String code : codes) {
                    if (!StringUtils.isEmpty(code)){
                        codesList.add(code);
                    }
                }
                map.put("size", codesList.size());
                if (codesList.size() == 1) {
                    map.put("codeOne", codesList.get(0));
                } else if (codesList.size()!=0 && codesList.size()!=1){
                    map.put("codeSize", codesList);
                }
            }
        }
        List<PowerQrVo> powerQrVoList = new ArrayList<>();
        List<Book> bookList = bookMapper.exportList(map);
        for (Book book : bookList) {
            BookQr bookQr = new BookQr();
            bookQr.setBookId(book.getId());
            bookQr.setQrType(3);
            List<BookQr> bookQrList = bookQrMapper.select(bookQr);
            if (bookQrList == null || bookQrList.size() == 0)
                throw new ServiceException("没有查到此图书刮刮卡权限二维码！");

            for (BookQr qr : bookQrList) {
                Example ex = new Example(QrExport.class);
                ex.setOrderByClause("createTime desc");
                Example.Criteria Qrcriteria = ex.createCriteria();
                Qrcriteria.andEqualTo("qrId", qr.getId());
                Qrcriteria.andEqualTo("type", 1);
                List<QrExport> list = this.qrExportMapper.selectByExample(ex);
                if (list == null || list.size() == 0) {
                    PowerQrVo powerQrVo = new PowerQrVo();
                    powerQrVo.setBookId(book.getId());
                    powerQrVo.setAuthor(book.getAuthor());
                    powerQrVo.setEditor(book.getEditor());
                    powerQrVo.setName(book.getName());
                    powerQrVo.setCategory(book.getCategory());
                    powerQrVo.setCode(book.getCode());
                    powerQrVo.setPress(book.getPress());
                    powerQrVo.setQuoteId("是");

                    powerQrVoList.add(powerQrVo);
                }else {
                    for (QrExport qrExport : list) {
                        PowerQrVo powerQrVo = new PowerQrVo();
                        powerQrVo.setBookId(book.getId());
                        powerQrVo.setAuthor(book.getAuthor());
                        powerQrVo.setEditor(book.getEditor());
                        powerQrVo.setName(book.getName());
                        powerQrVo.setCategory(book.getCategory());
                        powerQrVo.setCode(book.getCode());
                        powerQrVo.setPress(book.getPress());
                        powerQrVo.setQuoteId("是");
                        powerQrVo.setOprator(qrExport.getCreatorAccount());
                        powerQrVo.setNum(qrExport.getNum());
                        powerQrVo.setCreateTime(qrExport.getCreateTime());

                        powerQrVoList.add(powerQrVo);
                    }
                }
            }
        }
            Map<String, Object> beans = new HashMap<String, Object>();
            beans.put("powerQrVoList", powerQrVoList);
            //模版配置文件路径
            String templateFileName = PathUtil.getAppRootPath(request) + "WEB-INF" + File.separator + "classes" + File.separator + (powerQrVoList.size() != 0 ? "freemarker/bookqrListScan.xls" : "freemarker/bookListScanNoData.xls");
            InputStream in = null;
            OutputStream out = null;
            String filename = URLEncoder.encode("刮刮卡二维码.xls", "UTF-8"); //解决中文文件名下载后乱码的问题
            if ("FF".equals(WebUtil.getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                filename = new String(("刮刮卡二维码.xls").getBytes("UTF-8"), "iso-8859-1");
            }
            try {
                response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");
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
}
















