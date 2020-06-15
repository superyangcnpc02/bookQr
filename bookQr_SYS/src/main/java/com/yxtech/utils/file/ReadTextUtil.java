package com.yxtech.utils.file;

import com.yxtech.common.advice.ServiceException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 读取文本内容
 * Created by lyj on 2015/10/19.
 */
public class ReadTextUtil {

    /**
     * 读取text文档内容
     * @param filePath 文档路径
     * @return 文档内容
     * @author lyj
     * @since 2015-10-19
     */
    public static String readText(String filePath) {
        String reText = null;
        try {
            String encoding = "GBK";
            File file = new File(filePath);

            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                //考虑到编码格式
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;

                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (reText == null) {
                        reText = lineTxt;
                    } else {
                        reText = reText + lineTxt;
                    }
                }

                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            throw new ServiceException("读取txt文档错误");
        }

        return reText;
    }

}
