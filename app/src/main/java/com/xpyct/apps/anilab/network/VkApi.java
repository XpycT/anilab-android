package com.xpyct.apps.anilab.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.xpyct.apps.anilab.models.vk.VkFile;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public class VkApi {

    private static VkApi instance = null;

    private final VkService mWebService;

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //2015-01-18 15:48:56

    /**
     * Returns the instance of this singleton.
     */
    public static VkApi getInstance(String url) {
        instance = new VkApi(url);
        return instance;
    }

    public VkApi(String url) {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApiConstants.VK_BASE_URL)
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
        mWebService = restAdapter.create(VkService.class);
    }

    public interface VkService {
        @GET("/video.php")
        Observable<VkFile> getFile(@Query("act") String act, @Query("vid") String vid);
    }

    public Observable<VkFile> fetchFile(String act, String vid) {
        return mWebService.getFile(act, vid);
    }
}
