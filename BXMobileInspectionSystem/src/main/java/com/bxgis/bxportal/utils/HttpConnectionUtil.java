package com.bxgis.bxportal.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by xiaozhu on 2017/12/5.
 */

public class HttpConnectionUtil {
    public static HttpConnectionUtil http = new HttpConnectionUtil();

    public static HttpConnectionUtil getHttp() {
        return http;
    }

    public String getRequset(final String url) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL requestUrl = new URL(url);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(80000);
                    connection.setReadTimeout(80000);
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        System.out.println(sb);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (connection != null) {
                        connection.disconnect();//断开连接，释放资源
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String getRequset1(final String url,final Map<String, String> map) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder(url);
                sb.append('?');
                for(Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
                }
                sb.deleteCharAt(sb.length()-1);

                try {
                    URL requestUrl = new URL(sb.toString());
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(80000);
                    connection.setReadTimeout(80000);
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        System.out.println(sb);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (connection != null) {
                        connection.disconnect();//断开连接，释放资源
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String postRequset(final String url, final Map<String, String> map) {
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL requestUrl = new URL(url);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(80000);//链接超时
                    connection.setReadTimeout(80000);//读取超时
                    //发送post请求必须设置
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    DataOutputStream out = new DataOutputStream(connection
                            .getOutputStream());
                    StringBuilder request = new StringBuilder();
                    for (String key : map.keySet()) {
                        request.append(key + "=" + URLEncoder.encode(map.get(key), "UTF-8") + "&");
                    }
                    out.writeBytes(request.toString());//写入请求参数
                    out.flush();
                    out.close();
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        System.out.println(sb);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();//关闭流
                    }
                    if (connection != null) {
                        connection.disconnect();//断开连接，释放资源
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}