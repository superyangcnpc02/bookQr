package com.yxtech.utils.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yxtech.common.Constant;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码生成
 */
public class
        QREncode {
    /**
     * @param contents   二维码的内容
     * @param width      二维码的宽度
     * @param height     二维码的高度
     * @param format     生成的文件后缀
     * @param outputFile 二维码保存的路径
     * @param logoFile   logo存储的路径信息
     * @return true or fasle ；true生成成功，false生成失败
     */
    public static boolean encode_QR_CODE(String contents, int width, int height, String format, File outputFile, File logoFile, HttpServletRequest request,String text) {
        try {
            if(StringUtils.isBlank(format)){
                format="png";
            }
            if(width<240){
                width=240;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType,Object>();
            // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 内容所使用字符集编码
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //		hints.put(EncodeHintType.MAX_SIZE, 350);//设置图片的最大值
            //	    hints.put(EncodeHintType.MIN_SIZE, 100);//设置图片的最小值
            hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的空度，非负数
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,//要编码的内容
                    //编码类型，目前zxing支持：Aztec 2D,CODABAR 1D format,Code 39 1D,Code 93 1D ,Code 128 1D,
                    //Data Matrix 2D , EAN-8 1D,EAN-13 1D,ITF (Interleaved Two of Five) 1D,
                    //MaxiCode 2D barcode,PDF417,QR Code 2D,RSS 14,RSS EXPANDED,UPC-A 1D,UPC-E 1D,UPC/EAN extension,UPC_EAN_EXTENSION
                    BarcodeFormat.QR_CODE,
                    width, //条形码的宽度
                    width, //条形码的高度
                    hints);//生成条形码时的一些配置,此项可选

            // 生成二维码


            MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile, logoFile, request,text);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (WriterException w) {
            w.printStackTrace();
            return false;
        }

    }

    public static void main(String[] args) throws Exception {

        String contents = "ZXing 二维码内容1234!"; // 二维码内容
        int width = 500; // 二维码图片宽度 300
        int height = 500; // 二维码图片高度300
        String format = "png";// 二维码的图片格式 gif
        File outputFile = new File("d:" + File.separator + "xiaohong.png");//指定输出路径
        File logoFile = new File("d:" + File.separator + "abccc"+File.separator+"icon.jpg");

//        boolean b = encode_QR_CODE(contents, width, height, format, outputFile, logoFile);



    }

}

