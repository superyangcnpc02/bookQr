package com.yxtech.common;

import org.springframework.stereotype.Component;

/**
 * Created by Chenxh on 2015/10/13.
 */
@Component
public class Constant {

    public static String emailReportHttp;

    public String getEmailReportHttp() {
        return emailReportHttp;
    }

    public void setEmailReportHttp(String emailReportHttp) {
        this.emailReportHttp = emailReportHttp;
    }

    public static String emailExamineQrHttp;

    public String getEmailExamineQrHttp() {
        return emailExamineQrHttp;
    }

    public void setEmailExamineQrHttp(String emailExamineQrHttp) {
        Constant.emailExamineQrHttp = emailExamineQrHttp;
    }

    public static String NEWS_QR_HTTP;

    public String getNewsQrHttp() {
        return NEWS_QR_HTTP;
    }

    public void setNewsQrHttp(String newsQrHttp) {
        NEWS_QR_HTTP = newsQrHttp;
    }

    public static  String  BASE_QR_HTTP ;

    public  String getBaseQrHttp() {
        return BASE_QR_HTTP;
    }

    public  void setBaseQrHttp(String baseQrHttp) {
        BASE_QR_HTTP = baseQrHttp;
    }

    public static String COURSE_QR_HTTP;

    public  String getCourseQrHttp() {
        return COURSE_QR_HTTP;
    }

    public  void setCourseQrHttp(String courseQrHttp) {
        COURSE_QR_HTTP = courseQrHttp;
    }

    public static String GUA_QR_HTTP;

    public String getGuaQrHttp() {
        return GUA_QR_HTTP;
    }

    public void setGuaQrHttp(String guaQrHttp) {
        GUA_QR_HTTP = guaQrHttp;
    }

    public static String authHttp;

    public static String getAuthHttp() {
        return authHttp;
    }

    public static void setAuthHttp(String authHttp) {
        Constant.authHttp = authHttp;
    }

    public static String WEIXIN_APPID;

    public static String getWeixinSecret() {
        return WEIXIN_SECRET;
    }

    public void setWeixinSecret(String weixinSecret) {
        WEIXIN_SECRET = weixinSecret;
    }

    public static String WEIXIN_SECRET;


    public static String getWeixinAppid() {
        return WEIXIN_APPID;
    }

    public void setWeixinAppid(String weixinAppid) {
        WEIXIN_APPID = weixinAppid;
    }

    /**
     * 管理员

     */
    public static Integer ADMIN = 0;
    /**
     * 编辑员
     */
    public static Integer EDITOR = 1;
    /**
     * 总编
     */
    public static Integer EDITOR_CHIEF = 2;
    /**
     * 审核员
     */
    public static Integer EXAMINER = 3;

    //  WEB-INF/files/
    //public static String FILE_PATH = "WEB-INF" + File.separator + "files";
    //webapp/
    public static String FILE_PATH = "files";

    /**
     * 默认重置密码123456
     */
    public static String PASSWORD = "123456";

    /**
     * 文本
     */
    public static String TXT = "txt";
    public static int TXT_VALUE = 1;

    /**
     * 图片
     */
    public static String PICTURE = "gif,jpg,jpeg,bmp,png";
    public static int PICTURE_VALUE = 2;

    /**
     * 音频
     */
    public static String AUDIO = "wma,mp3";
    public static int AUDIO_VALUE = 3;

    /**
     * 视频
     */
    public static String VEDIO = "mp4,flv";
    public static int VEDIO_VALUE = 4;

    /**
     * 其他
     */
    public static String OTHER = "ppt,pptx,xls,xlsx,doc,docx,rtf,htm,html,xml,pdf,rar,zip,swf";
    public static int OTHER_VALUE = 4;

    /**
     * 保密
     */
    public static int SECRECY = 2;

    /**
     * 不保密
     */
    public static int NOT_SECRECY = 1;

    /**
     * 导出excel 每页最大条数
     */
    public static int EXCEL_MAX_INDEX = 1000;

    /**
     * 0
     */
    public static final int ZERO = 0;

    /**
     * 1
     */
    public static final int ONE = 1;

    /**
     * 2
     */
    public static final int TWO = 2;


    /**
     * 新增方法
     */
    public static final int METHOD_ADD = 1;

    /**
     * 删除方法
     */
    public static final int METHOD_DELETE = 2;

    /**
     * 修改方法
     */
    public static final int METHOD_UPDATE =3;

    /**
     * 查询方法
     */
    public static final int METHOD_GET = 4;

    /**
     * 上传文件mime类型:excel 2003以下
     */
    public static final String FILE_MIME_XLS = "application/vnd.ms-excel";

    /**
     * 上传文件mime类型:excel 2007以上
     */
    public static final String FILE_MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * 新增数据
     */
    public static final int ADD_TYPE = 0;

    /**
     * 导入数据
     */
    public static final int IMPORT_TYPE = 1;

    /**
     * 生成随机码长度
     */
    public static  final  int PASS_LENGTH = 6;

    /**
     * 最大长度，512
     */
    public static final int LENGTH_512_MAX = 512;

    /**
     * 更大长度，1000
     */
    public static final int LENGTH_1000_MAX = 1000;


    /**
     * operation 图书 1
     */
    public static final int BOOK = 1;

    /**
     * operation 二维码 2
     */
    public static final int BOOKQR = 2;

    /**
     * operation 资源 3
     */
    public static final int RES = 3;



}
