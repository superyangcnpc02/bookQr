package com.yxtech.utils.runCode;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Chenxh on 2015/9/25.
 */
@Component("code2File")
public class Code2File {

    /**
     *
     * @param code      文件要存储的内容
     * @param path      文件保存的路径
     * @param fileName  保存的文件名称
     * @return  正确生成文件返回 TRUE  ; 生成失败返回 FALSE;
     */
    public boolean writeFile2Local(String code, String path,String fileName) {
        BufferedWriter bw = null;
        FileWriter fr = null;
        try {
            File sourceFile = new File(path, fileName);//保存源代码
            //判断路径是否存在，不存在则创建
            if (!sourceFile.getParentFile().exists()) {
                sourceFile.getParentFile().mkdirs();
            }
            //判断文件是否存在，文件如果存在则删除
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
            //将code写入文件
            fr = new FileWriter(sourceFile);
            bw = new BufferedWriter(fr);
            bw.write(code);
            bw.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        File file = new File("D:/a/b/c/d", "www.txt");//保存源代码
        System.out.println(file.getPath());
        System.out.println(file.getParentFile());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

    }
}
