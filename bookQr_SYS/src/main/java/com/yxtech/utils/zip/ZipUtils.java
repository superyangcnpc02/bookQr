package com.yxtech.utils.zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ZipUtils {
    private static final Logger log = LoggerFactory.getLogger(ZipUtils.class);

    private ZipUtils(){};
    /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String zipPath,List<String> deleteFile,Object... sourcePath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            for (Object s : sourcePath) {
                writeZip(new File(s.toString()), "", zos,deleteFile);
            }

        } catch (FileNotFoundException e) {
            log.error("创建ZIP文件失败",e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                log.error("创建ZIP文件失败",e);
            }

        }
    }

    private static void writeZip(File file, String parentPath, ZipOutputStream zos,List<String> deleteFile) throws IOException {
        if(file.exists()){
            //处理文件夹
            if(file.isDirectory()){
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                if (files.length == 0 ) {
                    ZipEntry ze = new ZipEntry(parentPath + "/");
                    zos.putNextEntry(ze);
                    zos.setEncoding("UTF-8");
                    zos.closeEntry();
                    return;
                }
                for(File f:files){
                    writeZip(f, parentPath, zos,deleteFile);
                }

            }else{
                FileInputStream fis=null;
                DataInputStream dis=null;
                try {


                    fis=new FileInputStream(file);
                    dis=new DataInputStream(new BufferedInputStream(fis));

                    for (String delete: deleteFile){
                        if (file.getPath().contains(delete)){
                            return;
                        }
                    }

                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    //添加编码，如果不添加，当文件以中文命名的情况下，会出现乱码
                    // ZipOutputStream的包一定是apache的ant.jar包。JDK也提供了打压缩包，但是不能设置编码
                    zos.setEncoding("UTF-8");
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }
                } catch (FileNotFoundException e) {
                    log.error("创建ZIP文件失败",e);
                } catch (IOException e) {
                    log.error("创建ZIP文件失败",e);
                }finally{
                    try {
                        if(dis!=null){
                            dis.close();
                        }
                    }catch(IOException e){
                        log.error("创建ZIP文件失败",e);
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        //测试把F盘下的所有文件打包压缩成sql.zip文件放在F盘根目录下
        List<String> list = new ArrayList<>();
        list.add("C:\\Users\\Administrator\\Desktop\\语法与词汇练习1500例-国伟-70427");
//        list.add("D:\\workspase\\bookQr_SYS\\target\\bookQr\\WEB-INF\\zip\\20161124\\f83d76a3-3e12-45e4-a518-5830db848697\\企业信息化建设-赵明-10704");

        List<String> delelist = new ArrayList<>();
//        delelist.add("f:\\testzip\\2\\Chrysant.png");


        ZipUtils.createZip("f:/sql.zip",delelist, list.toArray());

    }
}
