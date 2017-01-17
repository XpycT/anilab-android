package com.xpyct.apps.anilab.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.xpyct.apps.anilab.models.myvi.MyviFile;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public class MyviApi {

    private static MyviApi instance = null;

    private final MyviService mWebService;

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //2015-01-18 15:48:56

    /**
     * Returns the instance of this singleton.
     */
    public static MyviApi getInstance(String url) {
        instance = new MyviApi(url);
        return instance;
    }

    public MyviApi(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setClient(new OkClient(httpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
                        request.addHeader("User-Agent", ApiConstants.USER_AGENT);
                    }
                })
                .build();
        mWebService = restAdapter.create(MyviService.class);
    }

    public interface MyviService {
        @GET("/")
        Observable<MyviFile> getFile(@Query("sig") String sig);
    }

    public Observable<MyviFile> fetchFile(String sig) {
        return mWebService.getFile(sig);
    }
}
