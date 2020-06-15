package com.yxtech.utils.runCode;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Chenxh on 2015/9/25.
 */

@Component
public class RunCmd implements Callable<Boolean> {
    public String cmd;
    public List<String> results;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public boolean cmd(String cmd,List<String> results) throws IOException, InterruptedException {

        // 运行程序
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);

            InputStream stderr = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                //System.out.println(line);
                results.add(line);
            }
           // proc.destroyForcibly()
            proc.waitFor();
        }catch (IOException io){
            io.printStackTrace();
            return false;
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
            return false;
        }
        return true;


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RunCmd run=new RunCmd();
        List<String> results = new ArrayList<String>();
        String cmd="php D:\\html\\c2pcDC7sow.php";
        run.cmd(cmd, results);
        for (String result : results) {
            System.out.println(result);
        }
    }

    @Override
    public Boolean call() throws Exception {
        // 运行程序
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);

            InputStream stderr = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                //System.out.println(line);
                results.add(line);
            }
            // proc.destroyForcibly()
            proc.waitFor();
        }catch (IOException io){
            io.printStackTrace();
            return Boolean.FALSE;
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
