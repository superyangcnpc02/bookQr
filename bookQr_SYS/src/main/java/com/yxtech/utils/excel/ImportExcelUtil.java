package com.yxtech.utils.excel;

import com.yxtech.common.advice.ExcelException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanfei on 2015/11/4.
 */
public class ImportExcelUtil {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class); //日志记录器

    /**
     * Excel数据转为List
     * @param in 文件输入流
     * @param entityClass 实体类
     * @param fieldMap 映射类
     * @param <T> 泛型
     * @return List
     *
     * @author yanfei
     * @since 2015-11-14
     */
    public static <T> List<T> excelToList(InputStream in, Class<T> entityClass,
                                          LinkedHashMap<String, String> fieldMap) {
        //定义返回的List
        List<T> result = new ArrayList<>();

        try {
            //生成工作簿
            Workbook workbook = WorkbookFactory.create(in);
            //获取工作表sheet
            Sheet sheet = workbook.getSheetAt(0);

            //获取sheet的第一行
            Row firstRow = sheet.getRow(0);
            //获取第一行中的标题名
            if (null == firstRow) {
                log.debug("标题行不正确:null == firstRow");
                throw new ExcelException("第一个sheet标题行不正确");
            }

            //读取标题行的所有标题名称
            List<String> titleNames = new ArrayList<>(0);
            log.debug("循环sheet标题行");
            for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                titleNames.add(convertCellStr(firstRow.getCell(i)));
            }

            //对比需要的属性字段是否与Excel中的标题吻合
            log.debug("循环标题-属性的映射map的key");
            for (String cnName : fieldMap.keySet()){
                if(!titleNames.contains(cnName)){
                    log.debug("excel中存在不需要的字段:!titleNames.contains(cnName)");

                    throw new ExcelException("Excel文件中缺少"+cnName+"或"+cnName+"不正确");
                }
            }

            //sheet转为List
            log.debug("循环sheet表中的记录");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { //跳过标题行
                //新建要转换的对象
                T entity = entityClass.newInstance();

                //获取当前行
                Row currentRow = sheet.getRow(i);

                if(currentRow == null){
                    // 如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动
                    sheet.shiftRows(i+1, sheet.getLastRowNum(), -1);
                    continue;
                }

                // 验证单元格
//                validateCell(currentRow, i);

                //设置对象的属性
                log.debug("循环映射map");
                for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                    //获取中文字段名称
                    String cnFieldName = entry.getKey();
                    //获取属性英文名称
                    String enPropertyName = entry.getValue();

                    //获取具体字段值
                    Cell cell = currentRow.getCell(titleNames.indexOf(cnFieldName));

                    //获取到字段值
                    if(null != cell){
                        //获取当前单元格中的内容
                        String cellContent = convertCellStr(currentRow.getCell(titleNames.indexOf(cnFieldName)));

                        //赋值对象属性
                        setFieldValueByName(enPropertyName, cellContent, entity);
                    }
                }

                result.add(entity);
            }
        } catch (IOException | InvalidFormatException e) {
            log.debug("创建工作簿异常");

            log.warn("创建工作簿异常", e);
            throw new ExcelException();
        } catch (InstantiationException | IllegalAccessException e) {
            log.debug("泛型对象初始化异常");

            log.warn("泛型对象初始化异常", e);
            throw new ExcelException();
        } finally {
            if (in != null) {
                log.debug("输入流 is not null");

                try {
                    in.close();
                } catch (IOException e) {
                    log.debug("关闭输入流异常");

                    log.warn("关闭输入流异常", e);
                    throw new ExcelException();
                }
            }
        }

        log.debug("返回Excel转换后的List");
        return result;
    }
    /**
     * 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段类
     *
     * @author lyj
     * @since 2015-8-14
     */
    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        //拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        log.debug("循环指定类的属性集合");
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                log.debug("属性的名称等于指定的值:field.getName().equals(fieldName)");

                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            log.debug("指定类存在父类且不为Object:superClazz != null && superClazz != Object.class");

            return getFieldByName(fieldName, superClazz);
        }

        log.debug("return null");
        //如果本类和父类都没有，则返回空
        return null;
    }

    /**
     * 根据属性名称设置属性值
     * @param fieldName 属性名称
     * @param fieldValue 属性值
     * @param o 类
     *
     * @author lyj
     * @since 2015-8-14
     */
    private static void setFieldValueByName(String fieldName, Object fieldValue, Object o) {
        Field field = getFieldByName(fieldName, o.getClass());

        if (field == null) {
            log.debug("获取不到指定类的属性:field == null");

            log.warn(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
            throw new ExcelException();
        }

        field.setAccessible(true);
        //获取字段类型
        Class<?> fieldType = field.getType();

        try {
            //如果是值是空或""，跳过
            if (fieldValue == null || fieldValue.toString().isEmpty()) {
                log.debug("属性值 is empty:fieldValue == null || fieldValue.toString().isEmpty()");

                return;
            }

            //根据字段类型给字段赋值
            if (String.class == fieldType) {
                log.debug("属性类型 is String");

                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                log.debug("属性类型 is Integer");

                //由于Excel读取数字均为浮点型，所以统一使用Double转换值
                field.set(o, Double.valueOf(fieldValue.toString()).intValue());
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                log.debug("属性类型 is Long");

                field.set(o, Double.valueOf(fieldValue.toString()).longValue());
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                log.debug("属性类型 is Float");

                field.set(o, Double.valueOf(fieldValue.toString()).floatValue());
            } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                log.debug("属性类型 is Short");

                field.set(o, Double.valueOf(fieldValue.toString()).shortValue());
            } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                log.debug("属性类型 is Double");

                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Date.class == fieldType) {
                log.debug("属性类型 is Date");

                field.set(o, Date.valueOf(fieldValue.toString()));
            } else if (Timestamp.class == fieldType) {
                log.debug("属性类型 is Timestamp");

                field.set(o, Timestamp.valueOf(fieldValue.toString()));
            } else {
                log.debug("属性类型 is other");

                field.set(o, fieldValue);
            }
        } catch (IllegalAccessException ex) {
            log.debug("指定类的属性赋值异常");

            log.warn(o.getClass().getSimpleName() + "类属性赋值异常", ex);
            throw new ExcelException();
        }
    }

    /**
     * 把单元格内的类型转换至String类型
     */
    private static String convertCellStr(Cell cell) {
        String cellStr = "";

        if (cell == null) {
            log.debug("单元格为null:cell == null");
            return cellStr;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                log.debug("cell type is string");

                // 读取String
                cellStr = cell.getStringCellValue().toString();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                log.debug("cell type is boolean");

                // 得到Boolean对象的方法
                cellStr = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                log.debug("cell type is numeric or date");

                // 先看是否是日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    log.debug("cell type is date:DateUtil.isCellDateFormatted(cell)");

                    // 读取日期格式
                    cellStr = DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");
                } else {
                    log.debug("cell type is numeric");

                    // 读取数字
                    cellStr = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                log.debug("cell type is formula");

                // 读取公式
                cellStr = cell.getCellFormula().toString();
                break;
        }

        log.debug("return cellStr");
        return cellStr.trim();
    }

    /**
     * 检验单元格是否为空或空字符串
     * @param row
     * @param i
     * @author yanfei
     * @date 2015.11.14
     */
    private static void validateCell(Row row, int i){
        for(int j=0; j < row.getLastCellNum(); j++)
        {
            Cell cell = row.getCell(j);
            if(j!=1){
                if(cell == null)
                {
                    throw new ExcelException("单元格第" + (i + 1) + "行" + (j + 1) + "列为空");
                }else if(StringUtils.isEmpty(cell.getStringCellValue().trim())){
                    throw new ExcelException("单元格第" + (i + 1) + "行" + (j + 1) + "列为空字符串");
                }
            }
        }
    }
}
