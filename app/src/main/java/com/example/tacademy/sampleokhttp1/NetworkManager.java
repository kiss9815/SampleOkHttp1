package com.example.tacademy.sampleokhttp1;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.begentgroup.xmlparser.XMLParser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dongja94 on 2016-02-05.
 */
public class NetworkManager {
    private static NetworkManager instance;
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    OkHttpClient mClient;
    private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;
    private  NetworkManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Context context = MyApplication.getContext();
        File cachefile = new File(context.getExternalCacheDir(), "mycache");
        if (!cachefile.exists()) {
            cachefile.mkdirs();
        }
        Cache cache = new Cache(cachefile, MAX_CACHE_SIZE);
        builder.cache(cache);

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
        builder.cookieJar(new JavaNetCookieJar(cookieManager));

//        disableCertificateValidation(builder);

        mClient = builder.build();
    }

    static void disableCertificateValidation(OkHttpClient.Builder builder) {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            builder.sslSocketFactory(sc.getSocketFactory());
            builder.hostnameVerifier(hv);
        } catch (Exception e) {
        }
    }


    public void cancelAll() {
        mClient.dispatcher().cancelAll();
    }

    public interface OnResultListener<T> {
        public void onSuccess(Request request, T result);
        public void onFailure(Request request, int code, Throwable cause);
    }

    private static final int MESSAGE_SUCCESS = 0;
    private static final int MESSAGE_FAILURE = 1;

    static class NetworkHandler extends Handler {
        public NetworkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CallbackObject object = (CallbackObject)msg.obj;
            Request request = object.request;
            OnResultListener listener = object.listener;
            switch (msg.what) {
                case MESSAGE_SUCCESS :
                    listener.onSuccess(request, object.result);
                    break;
                case MESSAGE_FAILURE :
                    listener.onFailure(request, -1, object.exception);
                    break;
            }
        }
    }

    Handler mHandler = new NetworkHandler(Looper.getMainLooper());

    static class CallbackObject<T> {
        Request request;
        T result;
        IOException exception;
        OnResultListener<T> listener;
    }

    public void cancelAll(Object tag) {

    }

    private static final String URL_FORMAT = "https://openapi.naver.com/v1/search/movie.xml?target=movie&query=%s&start=%s&display=%s";

    public Request getNaverMovie(Context context, String keyword, int start, int display,
                                 final OnResultListener<NaverMovies> listener) throws UnsupportedEncodingException {
        String url = String.format(URL_FORMAT, URLEncoder.encode(keyword, "utf-8"), start, display);

        final CallbackObject<NaverMovies> callbackObject = new CallbackObject<NaverMovies>();

        Request request = new Request.Builder().url(url)
                .header("X-Naver-Client-Id", "hsWPUuE3wajBeEop2EFs")
                .header("X-Naver-Client-Secret","Gr_bOJWFwW")
                .tag(context)
                .build();

        callbackObject.request = request;
        callbackObject.listener = listener;
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackObject.exception = e;
                Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XMLParser parser = new XMLParser();
                NaverMovies movies = parser.fromXml(response.body().byteStream(), "channel", NaverMovies.class);
                callbackObject.result = movies;
                Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                mHandler.sendMessage(msg);
            }
        });

        return request;
    }
}
