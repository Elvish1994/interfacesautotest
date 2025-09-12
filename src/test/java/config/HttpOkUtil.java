package config;

import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpOkUtil {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

    // 全局共用头部信息
    private static Map<String, String> commonHeaders;

    /**
     * 设置全局共用头部信息
     * @param headers 共用头部信息
     */
    public static void setCommonHeaders(Map<String, String> headers) {
        commonHeaders = headers;
    }

    /**
     * GET请求 - 带参数
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String getQuery(String url, Map<String, String> params) throws IOException {
        return getQuery(url, params, null);
    }

    /**
     * GET请求 - 带参数和集合参数
     * @param url 请求地址
     * @param params 单值参数
     * @param collectionParams 集合参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String getQuery(String url, Map<String, String> params, Map<String, List<String>> collectionParams) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        // 添加单值参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        // 添加集合参数（同一key多个值）
        if (collectionParams != null) {
            for (Map.Entry<String, List<String>> entry : collectionParams.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    urlBuilder.addQueryParameter(key, value);
                }
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        // 添加共用请求头
        addCommonHeaders(requestBuilder);

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * POST请求 - JSON格式
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postJson(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 添加共用请求头
        addCommonHeaders(requestBuilder);

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * POST请求 - 表单格式 - 带参数
     * @param url 请求地址
     * @param params 表单参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        return postForm(url, params, null);
    }

    /**
     * POST请求 - 表单格式 - 带参数和集合参数
     * @param url 请求地址
     * @param params 单值参数
     * @param collectionParams 集合参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> params, Map<String, List<String>> collectionParams) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();

        // 添加单值参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        // 添加集合参数
        if (collectionParams != null) {
            for (Map.Entry<String, List<String>> entry : collectionParams.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    formBuilder.add(key, value);
                }
            }
        }

        RequestBody body = formBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        // 添加共用请求头
        addCommonHeaders(requestBuilder);

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * PUT请求 - JSON格式
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String putJson(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .put(body);

        // 添加共用请求头
        addCommonHeaders(requestBuilder);

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * PUT请求 - 表单格式 - 带参数和集合参数
     * @param url 请求地址
     * @param params 单值参数
     * @param collectionParams 集合参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String putForm(String url, Map<String, String> params, Map<String, List<String>> collectionParams) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();

        // 添加单值参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        // 添加集合参数
        if (collectionParams != null) {
            for (Map.Entry<String, List<String>> entry : collectionParams.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    formBuilder.add(key, value);
                }
            }
        }

        RequestBody body = formBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .put(body);

        // 添加共用请求头
        addCommonHeaders(requestBuilder);

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 添加共用请求头到请求构建器
     * @param requestBuilder 请求构建器
     */
    private static void addCommonHeaders(Request.Builder requestBuilder) {
        if (commonHeaders != null) {
            for (Map.Entry<String, String> entry : commonHeaders.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
