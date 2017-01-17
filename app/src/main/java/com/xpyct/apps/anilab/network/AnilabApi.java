package com.xpyct.apps.anilab.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.xpyct.apps.anilab.models.Comments;
import com.xpyct.apps.anilab.models.File;
import com.xpyct.apps.anilab.models.MovieFull;
import com.xpyct.apps.anilab.models.MovieList;
import com.xpyct.apps.anilab.models.ParseLink;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class AnilabApi {

    private static AnilabApi instance = null;

    private final AnilabService mWebService;

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //2015-01-18 15:48:56

    /**
     * Returns the instance of this singleton.
     */
    public static AnilabApi getInstance() {
        if (instance == null) {
            instance = new AnilabApi();
        }
        return instance;
    }

    public AnilabApi() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApiConstants.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(httpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("x-api-key", ApiConstants.ACCESS_KEY);
                        request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
                        request.addHeader("User-Agent", ApiConstants.USER_AGENT);
                    }
                })
                .build();
        mWebService = restAdapter.create(AnilabService.class);
    }

    public interface AnilabService {
        @GET("/{service}/page/{page}")
        Observable<MovieList> getCategory(@Path("service") String service, @Path("page") Integer page, @Query("path") String path);

        @GET("/{service}/page/{page}")
        Observable<MovieList> getSearch(@Path("service") String service, @Path("page") Integer page, @Query("q") String query);

        @GET("/{service}/show/{id}")
        Observable<MovieFull> getFullPage(@Path("service") String service, @Path("id") Integer movieId);

        @GET("/{service}/show/{id}/comments")
        Observable<Comments> getComments(@Path("service") String service, @Path("id") Integer movieId);

        @GET("/{service}/show/{id}/files")
        Observable<ArrayList<ArrayList<File>>> getFiles(@Path("service") String service, @Path("id") Integer movieId);

        @FormUrlEncoded
        @POST("/parse")
        Observable<ParseLink> getParseLink(@Field("link") String link);
    }

    public Observable<MovieFull> fetchFullPage(String service, Integer movieId) {
        return mWebService.getFullPage(service, movieId);
    }

    public Observable<Comments> fetchComments(String service, Integer movieId) {
        return mWebService.getComments(service, movieId);
    }

    public Observable<ArrayList<ArrayList<File>>> fetchFiles(String service, Integer movieId) {
        return mWebService.getFiles(service, movieId);
    }

    public Observable<MovieList> fetchCategory(String service, Integer page, String path) {
        return mWebService.getCategory(service, page, path);
    }

    public Observable<MovieList> searchQuery(String service, Integer page, String query) {
        return mWebService.getSearch(service, page, query);
    }

    public Observable<ParseLink> fetchParseLink(String link) {
        return mWebService.getParseLink(link);
    }
}
