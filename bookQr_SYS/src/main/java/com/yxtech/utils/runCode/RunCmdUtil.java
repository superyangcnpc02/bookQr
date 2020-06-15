package com.yxtech.utils.runCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lichagnfeng on 2016/11/3.
 */
public class RunCmdUtil {

    private final static Logger log = LoggerFactory.getLogger(RunCmdUtil.class);

    private static class OutTexLog implements Runnable{
        private InputStream is;
        private String name;
        private StringBuffer pmsg;
        OutTexLog(InputStream is,String name){
            this.is = is;
            this.name = name;
        }
        OutTexLog(InputStream is,String name, StringBuffer pmsg){
            this.is = is;
            this.name = name;
            this.pmsg = pmsg;
        }
        @Override
        public void run() {
            BufferedInputStream in = null;
            BufferedReader inBr = null;
            try {
                in = new BufferedInputStream(this.is);
                inBr = new BufferedReader(new InputStreamReader(in));
                String lineStr = "";
                while ((lineStr = inBr.readLine()) != null) {
                    //获得命令执行后在控制台的输出信息
                    pmsg.append(lineStr);
                    log.debug(this.name+":"+lineStr);
                }
            }catch (Exception e){
                log.error("error in run "+this.name,e);
            }finally {
                try{
                    if (inBr != null) {
                        inBr.close();
                    }
                    if (in!=null){
                        in.close();
                    }
                    if (is!=null){
                        is.close();
                    }
                }catch (Exception e){
                    log.error("error",e);
                }
            }
        }
    }
    /**
     * 执行命令
     * @param cmd
     * @param dir
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean runCmd(String cmd,String dir) throws IOException, InterruptedException {
        log.debug("cmd:"+cmd);
        Runtime run = Runtime.getRuntime();
        Process p = null;
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        int exitValue = -1;//执行结果 0正常
        try {
            p = run.exec(cmd,null,new File(dir));// 启动另一个进程来执行命令
            Runnable runinfo = new OutTexLog(p.getInputStream(),"info");
            new Thread(runinfo).start();
            Runnable runerror = new OutTexLog(p.getErrorStream(),"error");
            new Thread(runerror).start();
            p.waitFor();
            exitValue = p.exitValue();
            log.debug("p exit value:"+exitValue);
        }catch (Exception e){
            throw e;
        }finally {
            if (inBr != null) {
                inBr.close();
            }
            if (in!=null){
                in.close();
            }
        }
        return exitValue==0;
    }

    /**
     * 执行命令
     * @param cmd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Map runCmdResMap(String cmd, String dir) throws IOException, InterruptedException {
        log.debug("cmd:"+cmd);
        Map returnMap = new HashMap<>();
        StringBuffer info = new StringBuffer();
        StringBuffer error = new StringBuffer();
        Runtime run = Runtime.getRuntime();
        Process p = null;
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        int exitValue = -1;//执行结果 0正常
        try {
            p = run.exec(cmd,null, new File(dir));// 启动另一个进程来执行命令
            Runnable runinfo = new OutTexLog(p.getInputStream(),"info",info);
            new Thread(runinfo).start();
            Runnable runerror = new OutTexLog(p.getErrorStream(),"error",error);
            new Thread(runerror).start();
            p.waitFor();
            exitValue = p.exitValue();
            log.debug("p exit value:"+exitValue);
            Thread.sleep(2000);
            returnMap.put("exitValue",exitValue);
            returnMap.put("info",info.toString());
            returnMap.put("error",error.toString());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (inBr != null) {
                inBr.close();
            }
            if (in!=null){
                in.close();
            }
        }
        return returnMap;
    }

}
