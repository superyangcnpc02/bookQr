package com.yxtech.utils.password;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
/**
 * desc:  <br>
 * date: 2019/5/23 0023
 *
 * @author cuihao
 */
public class PayEncrypt
{
    private static String Algorithm = "desede/CBC/PKCS5Padding";
    private static String iv = "01234567";
    private static String encoding = "utf-8";

    /**
     * 针对字符串进行加密
     * @param secretKey 加密密钥
     * @param plainText 要加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encryptMode(String secretKey, String plainText)
            throws Exception
    {
        Key deskey = null;
        try
        {
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance(Algorithm);
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(1, deskey, ips);
            byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
            return Base64.encode(encryptData);
        }
        catch (Exception e)
        {
//      throw new Exception(1, "PayEncrypt", "encryptMode", "加密错误", null);
            throw new Exception("加密错误");
        }
    }

    /**
     * 针对加密的字符串进行解密
     * @param secretKey 解密密钥
     * @param encryptText 要进行解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decryptMode(String secretKey, String encryptText)
            throws Exception
    {
        Key deskey = null;
        try
        {
            DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance(Algorithm);
            IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
            cipher.init(2, deskey, ips);
            byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
            return new String(decryptData, encoding);
        }
        catch (Exception e)
        {
//      throw new Exception(1, "PayEncrypt", "encryptMode", "解密错误", null);
            throw new Exception("解密错误");
        }
    }

    /**
     * 针对map进行加密，只加密key值对应的value
     * @param secretKey 加密密钥
     * @param encryptMap 要加密的map
     * @return 对value加密后的map
     * @throws Exception
     */
    public static Map<String, String> encryptMap(String secretKey, Map<String, String> encryptMap) throws Exception {
        Map<String, String> encryptData = new HashMap<String, String>();
        for (String k : encryptMap.keySet()) {
            String value = encryptMap.get(k);
            String res = PayEncrypt.encryptMode(secretKey, value);
            encryptData.put(k, res);
        }

        return encryptData;
    }

    /**
     * 针对加密value后的map进行解密
     * @param secretKey 解密密钥
     * @param decryptMap 要进行解密的map
     * @return 对value解密后的map
     * @throws Exception
     */
    public static Map<String, String> decryptMap(String secretKey, Map<String, String> decryptMap) throws Exception {
        Map<String, String> decryptData = new HashMap<String, String>();
        for (String k : decryptMap.keySet()) {
            String value = decryptMap.get(k);

            String res = PayEncrypt.decryptMode(secretKey, value);
            decryptData.put(k, res);
        }

        return decryptData;
    }

    /**
     * 测试demo
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /** 加密解密的密钥 */
        String key = "C234e017F54c6E848763B8fF43C330bD";
        /** 要进行加密的JSON字符串 */
        String text = "{\"mobile\":\"15901289416\",\"source\":\"yunpan\",\"returnUrl\":\"http://www.baidu.com/api/pay/return_url\"}";
        /** 要进行加密的Map数据 */
        Map<String, String> sendData = new HashMap<String, String>();
        sendData.put("mobile", "15901289416");
        sendData.put("source", "yunpan");
        sendData.put("returnUrl", "http://www.baidu.com/api/pay/return_url");

        /** map加密解密 */
        Map<String, String> encryptData = PayEncrypt.encryptMap(key, sendData);
        System.out.println("加密结果：" + encryptData);
        System.out.println("解密结果：" + PayEncrypt.decryptMap(key, encryptData));

        /** 字符串加密解密 */
        String encryptText = PayEncrypt.encryptMode(key, text);
        System.out.println("加密结果：" + encryptText);
        System.out.println("解密结果：" + PayEncrypt.decryptMode(key, encryptText));

    }
}
