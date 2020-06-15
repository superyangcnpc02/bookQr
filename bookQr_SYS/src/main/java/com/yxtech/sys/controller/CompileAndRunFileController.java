package com.yxtech.sys.controller;


import com.yxtech.utils.runCode.Code2File;
import com.yxtech.utils.runCode.DynamicCompileJava;
import com.yxtech.utils.file.RandomUtils;
import com.yxtech.utils.runCode.RunCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chenxh on 2015/9/24.
 */

@RestController("CompileAndRunFileController")
@RequestMapping(value = "/run")
public class CompileAndRunFileController {

    public static final String JAVA_SOURCE_PATH = "D:" + File.separator + "tmp";
    public static final String PHP_SOURCE_PATH = "D:" + File.separator + "html";
    @Autowired
    public DynamicCompileJava dynamicCompileJava;
    @Autowired
    private RunCmd runCmd;
    @Autowired
    private Code2File code2File;


    @RequestMapping(value = "/java")
    public Map<String, Object> runJava(String code, HttpServletRequest req) {
        Map<String, Object> json = new HashMap<String, Object>();
        String className = null; //类名
        String classStr = null;
        BufferedWriter bw = null;
        try {
            //验证类名格式是否正确
            classStr = code.substring(code.indexOf("public class"), code.indexOf("{")).toString();//获取类名字符串
            String[] classStrArray = classStr.split("\\s{1,}");//按空格分开
            if (classStrArray.length != 3) {
                json.put("error", "编译失败：格式不符合规范，请检查类名是否正确(如：public class YouClassName{})");
            } else {
                //保存文件到硬盘
                className = classStrArray[classStrArray.length - 1];
                Boolean b= code2File.writeFile2Local(code, JAVA_SOURCE_PATH, className + ".java");
                if(!b){
                    json.put("error", "编译失败");
                }else{
                    List<String> results = new ArrayList<String>();
                    dynamicCompileJava.codeRun(JAVA_SOURCE_PATH, JAVA_SOURCE_PATH + File.separator + className + ".java", className, results,json);
                    for (String s : results) {
                        System.out.println(s);
                    }
                    json.put("results", results);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("msg", e.getMessage());
        }
        return json;
    }

    @RequestMapping(value = "/php")
    public Map<String, Object> runPhp(String code, HttpServletRequest req) throws IOException, InterruptedException {
        Map<String, Object> json = new HashMap<String, Object>();
        List<String> results = new ArrayList<String>();
        String fileName= RandomUtils.generateMixString(10)+".php";
        Boolean b= code2File.writeFile2Local(code, PHP_SOURCE_PATH, fileName);
        if(b){
            StringBuffer cmd = new StringBuffer();
            cmd.append("php ");
            cmd.append(PHP_SOURCE_PATH + File.separator+fileName);
            boolean runCmd_b=runCmd.cmd(cmd.toString(),results);
            if(runCmd_b){
                json.put("results", results);
            }else{
                json.put("error", "编译失败");
            }
        }else{
            json.put("error", "编译失败");
        }
        return json;
    }
}
