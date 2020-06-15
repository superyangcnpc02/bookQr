package com.yxtech.utils.qr;

import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.log.SysoLogger;
import com.yxtech.utils.file.PathUtil;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.awt.*;

/**
 * 二维码的生成需要借助MatrixToImageWriter类，该类是由Google提供的，可以将该类直接拷贝到源码中使用，当然你也可以自己写个
 * 生产条形码的基类
 */
public class MatrixToImageWriter {
    private static final int BLACK = 0xFF000000;//用于设置图案的颜色
    private static final int WHITE = 0xFFFFFFFF; //用于背景色
    private  final String path = getClass().getResource("").getPath();

    private MatrixToImageWriter() {
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y,  (matrix.get(x, y) ? BLACK : WHITE));
//				image.setRGB(x, y,  (matrix.get(x, y) ? Color.YELLOW.getRGB() : Color.CYAN.getRGB()));
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, File qrFile, File logoFile, HttpServletRequest request,String text) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        //设置logo图标
        //LogoConfig logoConfig = new LogoConfig();
        if (null!=logoFile){
            image = LogoConfig.LogoMatrix(image,logoFile);
        }else{
            if("".equals(text)){
                String path = PathUtil.getAppRootPath(request)+"icon"+File.separator+"logoIcon.png";
                image = LogoConfig.LogoMatrix(image,new File(path));
            }else{
                //不为空
                String path = PathUtil.getAppRootPath(request)+"icon"+File.separator+"blank.png";
                image = LogoConfig.LogoMatrix(image,new File(path));

                //添加文字
                Graphics2D g = image.createGraphics();
                g.drawImage(image, 0, 0, matrix.getWidth(), matrix.getWidth(), null);
                //Font font = new Font("Courier New", Font.PLAIN, 12);
                Font font = new Font("宋体", Font.PLAIN, 14);
                g.setColor(Color.black); //根据图片的背景设置水印颜色

                int length = getWordCount(text);

                double rate = 21.0/6;
                double xD = rate*length;
                int x = (int)xD;
                g.setFont(font);
                g.drawString(text, matrix.getWidth()/2-x, matrix.getWidth()/2+6);
                g.dispose();
            }
        }

        if (!ImageIO.write(image, format, qrFile)) {
            throw new IOException("Could not write an image of format " + format + " to " + qrFile);
        }else{
            System.out.println("图片生成成功！");
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        //设置logo图标
        LogoConfig logoConfig = new LogoConfig();
        image = logoConfig.LogoMatrix(image);

        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    public static void writeToFile(BitMatrix bitMatrix, String format, File outputFile) {
    }

    public static int getWordCount(String s)
    {

        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }
    public static void main(String args[])throws Exception {
        String path = new MatrixToImageWriter().path;
        System.out.println(path.indexOf("bookQr"));
        path = path.replaceFirst("/","").substring(0,path.indexOf("target")+6);
        System.out.println(path);
    }

}