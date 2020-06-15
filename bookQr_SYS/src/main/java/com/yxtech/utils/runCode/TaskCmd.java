package com.yxtech.utils.runCode;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by Chenxh on 2015/9/28.
 */
@Component
public class TaskCmd {

    public static void testTask(ExecutorService exec, int timeout,String cmd,List<String> results,Map<String, Object> msg) {
        RunCmd runCmd=new RunCmd();
        runCmd.setCmd(cmd);
        runCmd.setResults(results);
        Future<Boolean> future = exec.submit(runCmd);
        Boolean taskResult = null;
        String failReason = null;
        try {
            // 等待计算结果，最长等待timeout秒，timeout秒后中止任务
            taskResult = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            failReason = "主线程在等待计算结果时被中断！";
            msg.put("error", "主线程在等待计算结果时被中断！");
        } catch (ExecutionException e) {
            failReason = "主线程等待计算结果，但计算抛出异常！";
            msg.put("error", "主线程等待计算结果，但计算抛出异常！");
        } catch (TimeoutException e) {
            failReason = "主线程等待计算结果超时，因此中断任务线程！";
            msg.put("error","主线程等待计算结果超时，因此中断任务线程！");
            exec.shutdownNow();
        }

        System.out.println("\ntaskResult : " + taskResult);
        System.out.println("failReason : " + failReason);
    }
}


