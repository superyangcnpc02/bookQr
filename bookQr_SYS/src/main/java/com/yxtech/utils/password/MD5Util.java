package com.yxtech.utils.password;
import com.yxtech.common.advice.UtilException;

import java.security.MessageDigest;

/**
 * MD5加密
 * Created by yanfei on 2015/6/11.
 */
public class MD5Util {

    /**
     * 二进制转十六进制
     * @param bytes
     * @return
     * @author yanfei
     * @since 2015.06.09
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

    /**
     * 生成md5
     * @param message
     * @return
     * @author yanfei
     * @since 2015.06.09
     */
    public static String getMD5(String message) {
        String md5str = "";
        if (message != null && message.length() > 0){
            try {
                //1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
                MessageDigest md = MessageDigest.getInstance("MD5");

                //2 将消息变成byte数组
                byte[] input = message.getBytes();

                //3 计算后获得字节数组,这就是那128位了
                byte[] buff = md.digest(input);

                //4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
                md5str = bytesToHex(buff);

                //5 转换小写
                md5str = md5str.toLowerCase();

            } catch (Exception e) {
                throw new UtilException(10002, "MD5加密失败");
            }
        }else{
            throw new UtilException(10003, "加密字符串不能为“”或NULL");
        }
        return md5str;
    }
}
