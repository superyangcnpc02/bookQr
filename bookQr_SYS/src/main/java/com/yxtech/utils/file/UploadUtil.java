package com.yxtech.utils.file;

import com.yxtech.common.advice.UploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 上传工具类
 * Created by yanfei on 2015.8.7
 */
public class UploadUtil {

    private static Logger logger = LoggerFactory.getLogger(UploadUtil.class); //日志记录器

    /**
     * 校验上传文件
     * @param file 文件表单项
     * @param fileTypes 上传类型白名单
     *
     *  @author cx
     *  @since 2014-11-27
     */
    public static void validateFile(MultipartFile file, List<String> fileTypes) {
        //校验上传的文件表单内容是否为null
        if (file == null || file.isEmpty()) {
            logger.debug("上传文件为null:file == null || file.isEmpty()");

            logger.warn("上传文件为null");
            throw new UploadException("读取上传文件失败");
        }

        //判断上传文件类型
        if (fileTypes != null && !fileTypes.isEmpty()) {
            logger.debug("fileTypes is useful:fileTypes != null && !fileTypes.isEmpty()");

            if (!fileTypes.contains(file.getContentType())) {
                logger.debug("fileType is not allowed:!fileTypes.contains(file.getContentType())");

                logger.warn("文件的mime类型是:" + file.getContentType());
                throw new UploadException("上传文件类型不正确");
            }
        }

        //判断文件大小是否为0
        if (file.getSize() == 0) {
            logger.debug("The size of file is 0:file.getSize() == 0");

            throw new UploadException("文件的大小是0");
        }
    }


    /**
     * 生成随机文件名
     * @param fileExt 上传文件后缀名
     * @return String
     *
     * @author yanfei
     * @since 2015.8.7
     */
    public static String generateFileName(final String fileExt) {
        Random random = new Random();
        return random.nextInt(10000) + System.currentTimeMillis() + fileExt;
    }

    /**
     * 根据日期生成目录
     * @return String 目录名称
     *
     * @author yanfei
     * @since 2015.8.7
     */
    public static String generateFolderByDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return String
     *
     * @author yanfei
     * @since 2015.8.7
     */
    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 校验上传文件
     * @param file 文件表单项
     * @param fileTypes 上传类型白名单
     *
     *  @author cx
     *  @since 2014-11-27
     */
    public static void validateFileByExt(MultipartFile file, List<String> fileTypes) {
        //校验上传的文件表单内容是否为null
        if (file == null || file.isEmpty()) {
            logger.debug("上传文件为null:file == null || file.isEmpty()");

            logger.warn("上传文件为null");
            throw new UploadException("读取上传文件失败");
        }

        //判断上传文件类型
        if (fileTypes != null && !fileTypes.isEmpty()) {
            logger.debug("fileTypes is useful:fileTypes != null && !fileTypes.isEmpty()");

            //根据上传文件的扩展名判断
            String fileExt = getFileExt(file.getOriginalFilename());
            if (!fileTypes.contains(fileExt)) {
                logger.debug("fileType is not allowed:!fileTypes.contains(file.getContentType())");

                logger.warn("文件的扩展名是:" + fileExt);

                throw new UploadException("上传文件类型不正确");
            }
        }

        //判断文件大小是否为0
        if (file.getSize() == 0) {
            logger.debug("The size of file is 0:file.getSize() == 0");

            throw new UploadException("文件的大小是0");
        }
    }

}
