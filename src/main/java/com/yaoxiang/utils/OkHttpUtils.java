package com.yaoxiang.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author maxiaoguang
 */
@Slf4j
public class OkHttpUtils {
    private static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送GET请求
     * @param url 请求URL
     * @return 响应字符串
     * @throws IOException 请求失败时抛出异常
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送GET请求
     * @param url 请求URL
     * @return 响应字符串
     * @throws IOException 请求失败时抛出异常
     */
    public static String get(String url,Map<String, String> headers) throws IOException {

        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request =  builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送POST请求
     * @param url 请求URL
     * @param json 请求体中的JSON字符串
     * @return 响应字符串
     * @throws IOException 请求失败时抛出异常
     */
    public static String post(String url, String json)  {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                                     .url(url)
                                     .post(body)
                                     .build();

        try (Response response = client.newCall(request).execute()) {
            //log.info("请求返回信息:{}", JSONUtil.toJsonStr(response));
            if (!response.isSuccessful()) {
                throw new IOException("POST 请求接口发生异常: " + response.body());
            }
            return response.body() != null ? response.body().string() : null;
        }catch (IOException e){
            log.info("调用接口发生异常:{}",e.getMessage());
            return null;
        }
    }

    public static String post(String url, String json,Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));


        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request =  builder.post(body).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("POST 请求接口发生异常: " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }

    /**
     * 发送带表单参数的POST请求
     * @param url 请求URL
     * @param params 请求参数Map
     * @return 响应字符串
     * @throws IOException 请求失败时抛出异常
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody body = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body() != null ? response.body().string() : null;
        }
    }
}

