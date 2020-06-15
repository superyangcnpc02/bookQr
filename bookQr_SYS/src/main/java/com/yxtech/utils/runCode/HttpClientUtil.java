package com.yxtech.utils.runCode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.net.URI;

/*
 * 利用HttpClient进行post请求的工具类
 */
public class HttpClientUtil {
    public static String doPost(String strUrl, Map<String, String> map, String charset) throws Exception {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        httpClient = new DefaultHttpClient();
        URL url = new URL(strUrl);
        URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        httpPost = new HttpPost(uri);
        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> elem = (Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpClient.execute(httpPost);
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, charset);
            }
        }

        return result;
    }


    /**
     * @param url
     * @param Params
     * @return
     * @throws IOException
     * @作用 使用urlconnection
     */
    public static String sendPost(String url, String Params) throws IOException {
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        String response = "";
        URL httpUrl = null; //HTTP URL类 用这个类来创建连接
        //创建URL
        httpUrl = new URL(url);
        //建立连接
        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setUseCaches(false);//设置不要缓存
        conn.setInstanceFollowRedirects(true);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();
        //POST请求
        out = new OutputStreamWriter(
                conn.getOutputStream());
        out.write(Params);
        out.flush();
        //读取响应
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        String lines;
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            response += lines;
        }
        reader.close();
        // 断开连接
        conn.disconnect();

        //使用finally块来关闭输出流、输入流
        try {
            if (out != null) {
                out.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }
}
