package com.yxtech.sys.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.sys.dao.FileResMapper;
import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.FileResCopy;
import com.yxtech.utils.qr.HttpTookit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cuihao
 * @create 2017-04-11-17:07
 */
@RestController
@RequestMapping(value = "/aliyun")
public class AliYunController {
    public final static Logger log = LoggerFactory.getLogger(AliYunController.class);
    @Autowired
    private FileResMapper fileResMapper;


    /**
     * 乐视云的视频转到阿里云,数据库记录更改(只用一次)
     * @return request
     */
    @RequestMapping(value = "/convert", method = RequestMethod.GET)
    public JsonResult convert(HttpServletRequest request) {
        String token = "Jiyuan";//服务器端固定token

        List<Object> list = Arrays.asList(new Object[]{"avi","flv","m4v","mp4","rmvb","wmv"});

        Example exampleList = new Example(FileRes.class);
        Example.Criteria criteriaList = exampleList.createCriteria();
        criteriaList.andIn("suffix",list);
        criteriaList.andIsNull("alivideoid");

        List<FileRes> fileResList = fileResMapper.selectByExample(exampleList);
        for (FileRes fileRes : fileResList) {
            String viewurl = fileRes.getViewurl();
            if(!StringUtils.isEmpty(viewurl)){
                if(viewurl.contains("&vu=")){
                    String temStr = viewurl.substring(viewurl.indexOf("&vu=")+4,viewurl.length());
                    String[] arr = temStr.split("&");
                    String vu="";
                    if(arr.length>1) {
                        vu = arr[0];
                    }else{
                        vu = temStr;
                    }

                    System.out.println("#########################vu==" + vu + "############################");

                    if(!"".equals(vu)){
                        // 获取阿里云视频ID
                        String alivideoid = "";
                        final String getIdUrl = "http://api.izhixue.cn/Aliyun/GetVideoByLeShi?vu=" + vu + "&ssotoken=" + token;
                        try {
                            alivideoid = HttpTookit.doGet(getIdUrl, null, "utf-8", true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("获取阿里云视频ID失败!");
                        }

                        //更新数据库
                        if (!org.apache.commons.lang.StringUtils.isBlank(alivideoid)) {
                            alivideoid = alivideoid.replace("\n", "").replace("\"", "");
                            fileRes.setAlivideoid(alivideoid);
                        }

                        fileResMapper.updateByPrimaryKeySelective(fileRes);
                    }

                }
            }


        }

        return new JsonResult(true, "");
    }


}
