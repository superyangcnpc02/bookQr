package com.yxtech.utils.excel;
import com.yxtech.sys.domain.Book;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hesufang on 2015/10/17.
 */


public class ExcelUtil {
    private static final String DEFAULT_SHEET_NAME = "sheet";

    /**
     * 导出无动态表头的Excel文件
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destOutputStream
     * @param templateInputStream
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(OutputStream destOutputStream,
                                               InputStream templateInputStream,
                                               List data, String dataKey,
                                               int maxRowPerSheet) throws Exception {
        generateExcelByTemplate(destOutputStream,
                templateInputStream,
                null, null,
                data, dataKey,
                maxRowPerSheet);
    }

    /**
     * 通过Excel模版生成Excel文件
     * <p>
     * 创建Excel模版，变量类似JSP tag风格。
     * 例如：
     * <ul>
     * <li>无动态表头
     * <pre>
     * 序号   名称  规格  创建时间    价格
     * &lt;jx:forEach items="${vms}" var="vm"&gt;
     * ${vm.id} ${vm.name} ${vm.scale} ${vm.created} ${vm.price}
     * &lt;/jx:forEach&gt;
     * </pre>
     * </li>
     * <li>有动态表头
     * <pre>
     * 项目/数量/时间    &lt;jx:forEach items="${dates}" var="date"&gt;    ${date} &lt;/jx:forEach&gt;
     * &lt;jx:forEach items="${itemsx}" var="item"&gt;
     * ${item.name}    &lt;jx:forEach items="${item.counts}" var="count"&gt; ${count}    &lt;/jx:forEach&gt;
     * &lt;/jx:forEach&gt;
     * </pre>
     * </li>
     * </ul>
     * 调用该方法则生成对应的Excel文件。
     * </p>
     * <p>
     * 注意：dataKey不能是items, items是保留字，如果用items则会提示：Collection is null并抛出NullPointerException
     * </p>
     * @param destOutputStream Excel输出流
     * @param templateInputStream Excel模版输入流
     * @param header 动态表头
     * @param headerKey 表头的变量
     * @param data 数据项
     * @param dataKey 数据项变量
     * @param maxRowPerSheet 每个sheet最多行数
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(OutputStream destOutputStream,
                                               InputStream templateInputStream,
                                               List header, String headerKey,
                                               List data, String dataKey,
                                               int maxRowPerSheet) throws Exception {

        List<List> splitData = null;
        @SuppressWarnings("unchecked")
        Map<String, List> beanMap = new HashMap();
        List<String> sheetNames = new ArrayList<String>();
        if (data.size() > maxRowPerSheet) {
            splitData = splitList(data, maxRowPerSheet);
            sheetNames = new ArrayList<String>(splitData.size());
            for (int i = 0; i < splitData.size(); ++i) {
                sheetNames.add(DEFAULT_SHEET_NAME  + i);
            }
        } else {
            splitData = new ArrayList<List>();
            sheetNames.add(DEFAULT_SHEET_NAME + 0);
            splitData.add(data);
        }
        if (null != header) {
            beanMap.put(headerKey, header);
        }
        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformMultipleSheetsList(
                templateInputStream, splitData, sheetNames, dataKey, beanMap, 0);
        workbook.write(destOutputStream);
        destOutputStream.flush();
    }

    /**
     * 只为权限二维码
     * @param destOutputStream
     * @param templateInputStream
     * @param header
     * @param headerKey
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplateAuth(Book book,OutputStream destOutputStream,
                                                   InputStream templateInputStream,
                                                   List header, String headerKey,
                                                   List data, String dataKey,
                                                   int maxRowPerSheet) throws Exception {

        List<List> splitData = null;
        @SuppressWarnings("unchecked")
        Map<String, List> beanMap = new HashMap();
        List<String> sheetNames = new ArrayList<String>();
        if (data.size() > maxRowPerSheet) {
            splitData = splitList(data, maxRowPerSheet);
            sheetNames = new ArrayList<String>(splitData.size());
            for (int i = 0; i < splitData.size(); ++i) {
                sheetNames.add(DEFAULT_SHEET_NAME  + i);
            }
        } else {
            splitData = new ArrayList<List>();
            sheetNames.add(DEFAULT_SHEET_NAME + 0);
            splitData.add(data);
        }
        if (null != header) {
            beanMap.put(headerKey, header);
        }

        //sheetNames清空
        sheetNames.clear();
        sheetNames.add("链接列表");

        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformMultipleSheetsList(
                templateInputStream, splitData, sheetNames, dataKey, beanMap, 1);
        //添加book信息
        Sheet sheet1 = workbook.getSheet("图书基础信息");

        Row row = sheet1.getRow(1);
        //设置书名
        Cell cell0 = row.getCell(0);
        cell0.setCellValue(book.getName());
        //设置ISBN
        Cell cell1 = row.getCell(1);
        String isbn = book.getIsbn();
        cell1.setCellValue(isbn);
        //设置作者
        Cell cell2 = row.getCell(2);
        cell2.setCellValue(book.getAuthor());
        //设置logo
        Cell cell3 = row.getCell(3);
        if(!StringUtils.isEmpty(isbn)){
            isbn = isbn.replace("ISBN ","").replace("-","");
            if(isbn.contains("9787302")){
                int begin = isbn.indexOf("9787302")+7;
                isbn = isbn.substring(begin,begin+5);
                cell3.setCellValue(isbn);
            }
        }

        //设置单元格格式
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);            //横向对齐
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER); //纵向对齐
        cell0.setCellStyle(style);
        cell1.setCellStyle(style);
        cell2.setCellStyle(style);
        cell3.setCellStyle(style);

        workbook.write(destOutputStream);
        destOutputStream.flush();
    }

    /**
     * 通过Excel模版生成Excel文件
     * @param destOutputStream Excel输出流
     * @param templateInputStream Excel模版输入流
     * @param header 动态表头
     * @param headerKey 表头的变量
     * @param data 数据项
     * @param dataKey 数据项变量
     * @param maxRowPerSheet 每个sheet最多行数
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplateMergedRegion(OutputStream destOutputStream,
                                               InputStream templateInputStream,
                                               List header, String headerKey,
                                               List data, String dataKey,
                                               int maxRowPerSheet) throws Exception {

        List<List> splitData = null;
        @SuppressWarnings("unchecked")
        Map<String, List> beanMap = new HashMap();
        List<String> sheetNames = new ArrayList<String>();
        if (data.size() > maxRowPerSheet) {
            splitData = splitList(data, maxRowPerSheet);
            sheetNames = new ArrayList<String>(splitData.size());
            for (int i = 0; i < splitData.size(); ++i) {
                sheetNames.add(DEFAULT_SHEET_NAME  + i);
            }
        } else {
            splitData = new ArrayList<List>();
            sheetNames.add(DEFAULT_SHEET_NAME + 0);
            splitData.add(data);
        }
        if (null != header) {
            beanMap.put(headerKey, header);
        }
        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformMultipleSheetsList(
                templateInputStream, splitData, sheetNames, dataKey, beanMap, 0);

        //合并单元格
        Sheet sheet1 = workbook.getSheet("sheet0");
        sheet1.addMergedRegion(new CellRangeAddress(1, 4, 1, 1));

        Row row = sheet1.getRow(1);
        Cell cell = row.getCell(1);
        cell.setCellValue("lalalal");

        //设置单元格格式
        CellStyle style02 = workbook.createCellStyle();
        style02.setAlignment(CellStyle.ALIGN_CENTER);            //横向对齐
        style02.setVerticalAlignment(CellStyle.VERTICAL_CENTER); //纵向对齐
        cell.setCellStyle(style02);

        workbook.write(destOutputStream);
        destOutputStream.flush();
    }


    /**
     * 导出无动态表头的Excel文件，目标文件和模版文件均为文件路径
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destFilePath
     * @param templateFilePath
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(String destFilePath,
                                               String templateFilePath,
                                               List data, String dataKey, int maxRowPerSheet) throws Exception {
        generateExcelByTemplate(destFilePath, templateFilePath, null, null, data, dataKey, maxRowPerSheet);
    }

    /**
     * 导出有动态表头的Excel文件，目标文件和模版文件均为文件路径
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destFilePath
     * @param templateFilePath
     * @param header
     * @param headerKey
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(String destFilePath,
                                               String templateFilePath,
                                               List header, String headerKey,
                                               List data, String dataKey, int maxRowPerSheet) throws Exception {

        File outputFile = new File(destFilePath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        generateExcelByTemplate(new FileOutputStream(destFilePath),
                new FileInputStream(templateFilePath),
                header, headerKey,
                data, dataKey, maxRowPerSheet);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<List> splitList(List data, int maxRowPerSheet) {
        List<List> splitData = new ArrayList<List>();
        List sdata = null;
        for (int i = 0; i < data.size(); ++i) {
            if (0 == i % maxRowPerSheet) {
                if (null != sdata) {
                    splitData.add(sdata);
                }
                sdata = new ArrayList(maxRowPerSheet);
            }
            sdata.add(data.get(i));
        }
        if (0 != maxRowPerSheet % data.size()) {
            splitData.add(sdata);
        }

        return splitData;
    }
}