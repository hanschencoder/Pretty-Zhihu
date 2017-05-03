package site.hanschen.pretty.zhihu;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {

    private static final long DEFAULT_CONNECT_TIMEOUT  = 5;
    private static final long DEFAULT_RESPONSE_TIMEOUT = 10;

    private final OkHttpClient mClient;

    public HttpClient() {
        this(DEFAULT_CONNECT_TIMEOUT, DEFAULT_RESPONSE_TIMEOUT, TimeUnit.SECONDS);
    }

    public HttpClient(long connectTimeout, long responseTimeout, TimeUnit unit) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, unit);
        builder.readTimeout(responseTimeout, unit);
        mClient = builder.build();
    }

    public String httpGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    public String httpPost(String url, Map<String, String> header, RequestBody body) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).post(body);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = builder.build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
