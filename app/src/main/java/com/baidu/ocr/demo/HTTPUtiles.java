/*
 * Copyright (C) 2022 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.ocr.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPUtiles {

    private static String URL_PATH="http://api.fanyi.baidu.com/api/trans/vip/translate?q=Welcome to beijing&from=en&to=zh&appid=20220313001122760&salt=1435660288&sign=57b5f97ba0a21e46c2f6bbb5edb76f3a";
    private static HttpURLConnection httpURLConnection = null;
    public HTTPUtiles(){

    }

    public static String shuchu() {
        InputStream inputStream = getInputStream();
        String result="";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = "";
            try {
                while ((line = reader.readLine()) != null) {
                    result = result + line;
                }
                return result;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(result);
            httpURLConnection.disconnect();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取服务端的数据，以InputStream返回
     * @return
     */
    public static InputStream getInputStream(){
        InputStream inputStream = null;

        try {
            URL url = new URL(URL_PATH);
            if(url != null){
                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    //超时时间
                    httpURLConnection.setConnectTimeout(3000);
                    //表示设置本次http请求使用GET方式
                    httpURLConnection.setRequestMethod("GET");
                    int responsecode = httpURLConnection.getResponseCode();

                    if(responsecode == HttpURLConnection.HTTP_OK){
                        inputStream = httpURLConnection.getInputStream();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inputStream;
    }

}