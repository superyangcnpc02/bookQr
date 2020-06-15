package com.yxtech.utils.runCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Chenxh on 2015/9/24.
 */
@Component("dynamicCompileJava")
public class DynamicCompileJava {
    @Autowired
    private RunCmd runCmd;

    public void codeRun(String classPath,String javaPath,String className,List<String> results,Map<String, Object> msg) throws IOException, InterruptedException {
        // 编译程序
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        int result = javaCompiler.run(null, null, null, "-d",classPath,javaPath);
        System.out.println( result == 0 ? "恭喜编译成功" : "对不起编译失败");
        if(result!=0){
            throw new RuntimeException("编译失败");
        }

        // 运行程序

        StringBuffer cmd = new StringBuffer();
        cmd.append("java -cp ");
        cmd.append(classPath);
        cmd.append(" " + className);
        //runCmd.cmd(cmd.toString(),msg);

        ExecutorService exec = Executors.newCachedThreadPool();
        TaskCmd.testTask(exec,5,cmd.toString(),results,msg);
        exec.shutdown();


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> results  = new ArrayList<String>();
//        new DynamicCompileJava().codeRun("D:/", "D:/test.java", "test", results );
//        for (String s : results ) {
//            System.out.println(s);
//        }
    }
}


