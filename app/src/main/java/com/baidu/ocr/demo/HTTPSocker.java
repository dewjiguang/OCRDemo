/*
 * Copyright (C) 2022 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.ocr.demo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HTTPSocker {
    //MD5加密
    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
    public static String inputStream2StringNew(InputStream is) {
        try {
            ByteArrayOutputStream boa = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = is.read(buffer)) != -1) {
                boa.write(buffer, 0, len);
            }
            is.close();
            boa.close();
            byte[] result = boa.toByteArray();

            String temp = new String(result);

            // 识别编码
            if (temp.contains("utf-8")) {
                return new String(result, "utf-8");
            } else if (temp.contains("gb2312")) {
                return new String(result, "gb2312");
            } else {
                return new String(result, "utf-8");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向指定URL发送GET方法的请求
     * <p>
     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String word) {
        //word = "Welcome to beijing";
        String result = "";
        BufferedReader in = null;
        String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";
        String appId = "20220313001122760";
        String key = "j4bFzDXNdn1V9uWvbGOK";
        String salt = "1435660288";
        String s = appId + word + salt + key;
        String md5Str = getMD5Str(s);
        String realUrl = url + "?" + "q=" + word + "&from=en&to=zh&appid=" + appId + "&salt=" + salt + "&sign=" + md5Str;
        try {
                URL Url = new URL(realUrl);
                // 打开和URL之间的连接
                URLConnection connection = Url.openConnection();
                // 设置通用的请求属性
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                // 建立实际的连接
                connection.connect();
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"GB2312"));
                String line;

                //String test="{\"from\":\"en\",\"to\":\"zh\",\"trans_result\":[{\"src\":\"Welcome to beijing\",\"dst\":\"\\u6b22\\u8fce\\u6765\\u5230\\u5317\\u4eac\"}]}";
                //String s1 = test.substring(test.indexOf("dst\":\""), test.indexOf("\"}]}")).replaceAll("dst\":\"", "");
                //System.out.println(URLDecoder.decode(s1));
                //String str = new String(test.getBytes()," ");
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                String test = decodeUnicode(result);
               result=test.substring(test.indexOf("dst\":\""), test.indexOf("\"}]}")).replaceAll("dst\":\"", "");
            } catch (Exception e) {
                System.out.println("发送GET请求出现异常！" + e);
                e.printStackTrace();
            }
            // 使用finally块来关闭输入流
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return result;
        }

    }

