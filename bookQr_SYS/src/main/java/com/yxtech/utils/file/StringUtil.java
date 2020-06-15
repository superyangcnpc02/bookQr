package com.yxtech.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * Created by yanfei on 2015/6/12.
 */
public class StringUtil {
    private static final Logger log = LoggerFactory.getLogger(StringUtil.class); //日志记录器

    public static final String UTF8 = "utf-8";
    private static final String MYSQL_LIKE_MORE = "%";
    private static final String MYSQL_LIKE_SINGLE = "_";
    private static final String ESCAPE_CHAR = "\\";

    /**
     * 处理get中文参数乱码
     * @param arg 参数
     * @param code 指定编码
     * @return String
     *
     * @author yanfei
     * @since 2015/6/12
     */
    public static String queryParamEncode(String arg, String code) {
        if (arg == null || arg.isEmpty()) {
            return "";
        }

        try {
            return new String(arg.getBytes("ISO-8859-1"), code);
        } catch (UnsupportedEncodingException ex) {
            log.error("无效字符集:{}", code);

            return "";
        }
    }

    /**
     * 转义SQL中LIKE语句的特殊字符
     * @param arg
     * @return
     *
     * @author yanfei
     * @since 2015/6/12
     */
    public static String escape4Like(String arg) {
        if (arg != null && !arg.isEmpty()) {
            if (arg.contains(MYSQL_LIKE_MORE) || arg.contains(MYSQL_LIKE_SINGLE)) {
                //转义“%”字符
                arg = arg.replace(MYSQL_LIKE_MORE, ESCAPE_CHAR + MYSQL_LIKE_MORE);
                //转义“_”字符
                arg = arg.replace(MYSQL_LIKE_SINGLE, ESCAPE_CHAR + MYSQL_LIKE_SINGLE);
            }
        }

        return arg;
    }

    /**
     * 检验字符串中是否包含汉字
     * @param string
     * @return
     * @author yanfei
     * @date 2015.11.19
     */
    public static boolean validateGBK(String string){
        if (StringUtils.isEmpty(string)){
            return false;
        }

        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(string);
        if (m.find()) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 检验字符串中是否是邮箱格式
     * @param email
     * @return
     * @author yanfei
     * @date 2015.11.19
     */
    public static boolean validateEmail(String email){
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * list转成逗号分隔的字符串
     * @param list
     * @return
     */
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(",");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
