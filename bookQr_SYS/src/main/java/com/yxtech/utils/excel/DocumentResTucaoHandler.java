package com.yxtech.utils.excel;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by lsn
 */
public class DocumentResTucaoHandler {

    private static Configuration configuration = null;

    private static Template template = null;



    public  String createHTML(Map<String, Object> data) throws Exception {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(new DocumentResTucaoHandler().getClass(), "/freemarker");

            template = configuration.getTemplate("clientResTucaoTemplate.ftl");
            template.setEncoding("UTF-8");


        StringWriter sw = new StringWriter();
        Writer out = new BufferedWriter(sw);
        // 处理模版 map数据 ,输出流
        template.process(data, out);
        out.flush();
        out.close();
        StringBuffer sb = sw.getBuffer();
        return sb.toString();
    }
}

