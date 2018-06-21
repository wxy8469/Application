package com.dawncos.glutinousrice.base.dagger2.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.dawncos.glutinousrice.http.HttpHandler;
import com.dawncos.glutinousrice.utils.android.DataHelper;
import com.dawncos.glutinousrice.utils.log.interceptor.HttpLogInterceptor;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * 提供一些http相关实例 {@link Module}
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Module
public abstract class HttpModule {
    private static final int TIME_OUT = 10;

    @Binds
    abstract Interceptor bindInterceptor(HttpLogInterceptor interceptor);

    @Singleton
    @Provides
    static Retrofit provideRetrofit(Application application, @Nullable CustomizeRetrofit CustomizeRetrofit,
            OkHttpClient okHttpClient, HttpUrl httpUrl, Gson gson) {
        Timber.d( "provideRetrofit");
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(httpUrl)
               .client(okHttpClient)
               .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .addConverterFactory(GsonConverterFactory.create(gson));
        if (CustomizeRetrofit != null)
            CustomizeRetrofit.setupRetrofit(application, builder);
        return builder.build();
    }

    @Singleton
    @Provides
    static OkHttpClient provideOkHttpClient(Application application, @Nullable CustomizeOkHttpClient customizeOkHttpClient,
            Interceptor intercept, @Nullable List<Interceptor> interceptors, @Nullable HttpHandler httpHandler) {
        Timber.d( "provideOkHttpClient");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
               .readTimeout(TIME_OUT, TimeUnit.SECONDS)
               .addNetworkInterceptor(intercept);
        if (httpHandler != null)
            builder.addInterceptor(chain ->
               chain.proceed(httpHandler.onHttpRequestBefore(chain, chain.request())));
        if (interceptors != null) {//如果外部提供了interceptor的集合则遍历添加
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        if (customizeOkHttpClient != null)
            customizeOkHttpClient.setupOkHttpClient(application, builder);
        return builder.build();
    }

    @Singleton
    @Provides
    static RxCache provideRxCache(Application application, @Nullable CustomizeRxCache customizeRxCache,
              @Named("RxCacheDirectory") File cacheDirectory) {
        Timber.d( "provideRxCache");
        RxCache.Builder builder = new RxCache.Builder();
        if (customizeRxCache != null)
            customizeRxCache.setupRxCache(application, builder);
        return builder.persistence(cacheDirectory, new GsonSpeaker());
    }

    @Singleton
    @Provides
    @Named("RxCacheDirectory")
    static File provideRxCacheDirectory(File cacheDir) {
        Timber.d( "provideRxCacheDirectory");
        File cacheDirectory = new File(cacheDir, "RxCache");
        return DataHelper.makeDirs(cacheDirectory);
    }

    public interface CustomizeRetrofit {
        void setupRetrofit(Context context, Retrofit.Builder builder);
    }

    public interface CustomizeOkHttpClient {
        void setupOkHttpClient(Context context, OkHttpClient.Builder builder);
    }

    public interface CustomizeRxCache {
        void setupRxCache(Context context, RxCache.Builder builder);
    }
}
