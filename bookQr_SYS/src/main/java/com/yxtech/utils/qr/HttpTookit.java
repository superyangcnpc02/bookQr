package com.yxtech.utils.qr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP工具箱
 *
 * @author leizhimin 2009-6-19 16:36:18
 */
public final class HttpTookit {
    private static Log log = LogFactory.getLog(HttpTookit.class);


    /**
     * 执行一个HTTP GET请求，返回请求响应的HTML
     *
     * @param url                 请求的URL地址
     * @param queryString 请求的查询参数,可以为null
     * @param charset         字符集
     * @param pretty            是否美化
     * @return 返回请求响应的HTML
     */
    public static String doGet(String url, String queryString, String charset, boolean pretty) {
        StringBuffer response = new StringBuffer();
        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        try {
            if (StringUtils.isNotBlank(queryString))
                //对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
                method.setQueryString(URIUtil.encodeQuery(queryString));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty)
                        response.append(line).append(System.getProperty("line.separator"));
                    else
                        response.append(line);
                }
                reader.close();
            }
        } catch (URIException e) {
            log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
        } catch (IOException e) {
            log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url        请求的URL地址
     * @param params 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, Map<String, String> params) {
        String response = null;
        HttpClient client = new HttpClient();
        HttpMethod method = new PostMethod(url);
        method.addRequestHeader("Content-Type","application/json");
        //设置Http Post数据
        if (params != null) {
            HttpMethodParams p = new HttpMethodParams();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                p.setParameter(entry.getKey(), entry.getValue());
            }
            method.setParams(p);
        }
        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                response = method.getResponseBodyAsString();
            }
        } catch (IOException e) {
            log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
        } finally {
            method.releaseConnection();
        }

        return response;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url        请求的URL地址
     * @param params 请求的查询参数,可以为null
     * @return 返回请求响应的HTML
     */
    public static String doPostJson(String url, Map<String, String> params) {
        String response="";
        try {
            String requestStr = JSON.toJSONString(params);
            RequestEntity entity = new StringRequestEntity(requestStr,"application/json","UTF-8");
            PostMethod method = new PostMethod(url);
            method.setRequestEntity(entity);
            method.setRequestHeader("Content-Type","application/json;charset=UTF-8");
            new HttpClient().executeMethod(method);
            response=method.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    public static void main(String[] args) {
//        String y = doGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx91127260dd5e0e88&secret=2a13b49c5dffe557935e555e577c1b5b&code=031txrmc0YMxBH15pooc0sSomc0txrmu&grant_type=authorization_code", null, "utf-8", true);
        String aa = "{\"access_token\": \"WBvAxxPXba9lft5sHgX4RnwW39nj877uzaZOoSDsIVt7dWGjiryKFHk075USUY-bQXAXx75TPMpbyaQG4X2aQ7NzFlj9PEbIv61wkeQaRrc\",\"expires_in\": 7200,\"refresh_token\": \"MQyn6NmLF8-SUNZf78tUfAmZMHXSe3InosPdC5LMeBPvat10PwbicpCgcPlDDoHFOZB8xJ451ksdzXub1h9DCVaaAXaphBxtJA4fVKBV_ec\", \"openid\": \"oUgl9wVGX_7DxiJ7_44fPLOuQ5bE\", \"scope\": \"snsapi_userinfo\"}";
        JSONObject tokenObj = JSONObject.fromObject(aa);
        System.out.println(tokenObj.has("errcode"));;
        System.out.println(tokenObj.getString("errcode"));
    }
}
