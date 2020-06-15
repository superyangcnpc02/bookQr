package com.yxtech.utils.file;

import com.yxtech.common.advice.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/10/16.
 */
public class FileUtil {
    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static void main1(String[] args) throws IOException {
        File src=new File("D:/abc2");
        File dest=new File("D:/abc");
        System.out.print(src.getParent());
        //copyFolder(src, dest);
    }

    //ansi格式的txt转Unicode(utf-8)
    public static void change(File file,String enCoding) throws UnsupportedEncodingException, IOException{
        BufferedReader buf = null;
        OutputStreamWriter pw=null;
        String str = null;
        String allstr="";

        //用于输入换行符的字节码
        byte[] c=new byte[2];
        c[0]=0x0d;
        c[1]=0x0a;
        String t=new String(c);

        buf=new BufferedReader(new InputStreamReader(new FileInputStream(file), enCoding));
        while((str = buf.readLine()) != null){
            allstr=allstr+str+t;
        }

        buf.close();

        pw =new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),"Unicode");
        pw.write(allstr);
        pw.close();
    }
    //获取txt文本编码格式
    public static String getCharset(File file) throws IOException{

        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = (bin.read() << 8) + bin.read();

        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }
    //文件大小转换成可显示的Mb,Gb和kb方法
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
    //下载文件
    public static HttpServletResponse download(String path, HttpServletRequest request, HttpServletResponse response) {
        // path是指欲下载的文件的路径。
        File file = new File(path);
        // 取得文件名。
        String fileName = file.getName();

        try {
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0){
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//firefox浏览器
            }else if(request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0){
                    fileName = URLEncoder.encode(fileName, "UTF-8");//IE浏览器
            }else{
                fileName = URLEncoder.encode(file.getName() , "utf-8");
            }
            response.setContentType("text/plain");
            response.setHeader("Location",fileName);
            response.reset();
            response.setHeader("Cache-Control", "max-age=0" );
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            OutputStream fos = null;
            InputStream fis = null;
            fis = new FileInputStream(path);
            bis = new BufferedInputStream(fis);
            fos = response.getOutputStream();
            bos = new BufferedOutputStream(fos);
            int bytesRead = 0;
            byte[] buffer = new byte[5 * 1024];
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);// 将文件发送到客户端
            }
            bos.close();
            bis.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 删除空目录
     * @param dir 将要删除的目录路径
     * @return
     */
    public static boolean doDeleteEmptyDir(String dir) {
        return (new File(dir)).delete();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static void copyFile(String fromUrl, String toUrl,String fileName,String suffix) throws Exception{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(fromUrl);
            fos = new FileOutputStream(toUrl);

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("关闭失败");
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("关闭失败");
            }
            //处理中文名称
            File f=new File(toUrl);
            String c=f.getParent();
            String newFileName = URLDecoder.decode(fileName,"utf-8");
            File mm=new File(c + File.separator + newFileName+"."+suffix);
            if(f.renameTo(mm))
            {
                System.out.println("修改成功!");
            }
            else
            {
                throw new ServiceException("拷贝失败！");
            }
        }
    }

    /**
     * 检查是否是支持的视频格式
     * @param suffix
     * @return
     */
    public static boolean isSupportVideo(String suffix){
        boolean support = false;
        String[] suffixs = new String[]{"flv","mp4","wmv","avi","dat","asf","rm","rmvb","ram","mpg","mpeg","mov","m4v","mkv","vob","qt","divx","cpk","fli","flc","mod","dvix","dv","ts"};
        List<String> list = Arrays.asList(suffixs);
        if(list.contains(suffix)){
            support = true;
        }

        return support;
    }
}
