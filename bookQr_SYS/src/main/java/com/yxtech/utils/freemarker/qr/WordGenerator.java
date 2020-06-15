package com.yxtech.utils.freemarker.qr;

import freemarker.template.Configuration;
import freemarker.template.Template;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordGenerator {

    public static void createDoc(Map<?, ?> dataMap,File docFile) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        //configuration.setDirectoryForTemplateLoading(ftlPath);
        // 指定模板文件从何处加载的数据源，这里设置成一个文件目录。
        configuration.setDirectoryForTemplateLoading(new File(WordGenerator.class.getResource("/freemarker").getFile()));
        Template t = configuration.getTemplate("qr.ftl", "utf-8");
        try {
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(docFile), "utf-8");
            t.process(dataMap, w);
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //return f;
    }
    public static String getImageStr(String imgFile) {
        //String imgFile = "d:/aa.png";
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public static void main(String[] args) throws IOException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<QrGroup> list = new ArrayList<QrGroup>();
        QrGroup qg = new QrGroup("A", "b", "c", "d", "");
        qg.setImg1(WordGenerator.getImageStr("F:\\workSpace\\bookQr\\src\\main\\webapp\\WEB-INF\\books\\小飞侠历险记-小王\\小飞侠第2章.png"));
        qg.setImg2("F:\\workSpace\\bookQr\\src\\main\\webapp\\WEB-INF\\books\\小飞侠历险记-小王\\小飞侠第3章.png");
        qg.setImg3("F:\\workSpace\\bookQr\\src\\main\\webapp\\WEB-INF\\books\\小飞侠历险记-小王\\小飞侠第4章.png");
        qg.setImg4("F:\\workSpace\\bookQr\\src\\main\\webapp\\WEB-INF\\books\\小飞侠历险记-小王\\小飞侠第5章.png");
        list.add(qg);
//        QrGroup qg1 = new QrGroup("A", "", "", "", WordGenerator.getImageStr("D:\\abc2\\icon.jpg"));
//        list.add(qg1);
        dataMap.put("qrList", list);

        String name = "D:/temp" + (int) (Math.random() * 100000) + ".doc";
        File f = new File(name);
        WordGenerator.createDoc(dataMap, f);

    }

}