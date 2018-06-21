package com.dawncos.glutinousrice.base.dagger2.module;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dawncos.glutinousrice.base.cache.Cache;
import com.dawncos.glutinousrice.base.cache.CacheType;
import com.dawncos.glutinousrice.base.cache.IntelligentCache;
import com.dawncos.glutinousrice.base.cache.LruCache;
import com.dawncos.glutinousrice.http.BaseUrl;
import com.dawncos.glutinousrice.http.HttpHandler;
import com.dawncos.glutinousrice.http.imageloader.BaseImageLoaderStrategy;
import com.dawncos.glutinousrice.http.imageloader.glide.GlideImageLoaderStrategy;
import com.dawncos.glutinousrice.utils.android.DataHelper;
import com.dawncos.glutinousrice.utils.log.interceptor.FormatHttpMessage;
import com.dawncos.glutinousrice.utils.log.interceptor.FormatHttpMsg;
import com.dawncos.glutinousrice.utils.log.interceptor.HttpLogInterceptor;
import com.dawncos.glutinousrice.utils.others.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * 允许向框架中注入外部配置的自定义参数
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Module
public class GlutinousRiceBuilder {
    private HttpUrl mApiUrl;
    private BaseUrl mBaseUrl;
    private BaseImageLoaderStrategy mLoaderStrategy;
    private HttpHandler mHttpHandler;
    private List<Interceptor> mInterceptors;
    private File mCacheFile;
    private HttpModule.CustomizeRetrofit mCustomizeRetrofit;
    private HttpModule.CustomizeOkHttpClient mCustomizeOkHttpClient;
    private HttpModule.CustomizeRxCache mCustomizeRxCache;
    private AppModule.CustomizeGson mCustomizeGson;
    private HttpLogInterceptor.Level mPrintHttpLogLevel;
    private FormatHttpMsg mFormatHttpMsg;
    private Cache.Factory mCacheFactory;

    private GlutinousRiceBuilder(Builder builder) {
        this.mApiUrl = builder.apiUrl;
        this.mBaseUrl = builder.baseUrl;
        this.mLoaderStrategy = builder.loaderStrategy;
        this.mHttpHandler = builder.httpHandler;
        this.mInterceptors = builder.interceptors;
        this.mCacheFile = builder.cacheFile;
        this.mCustomizeRetrofit = builder.CustomizeRetrofit;
        this.mCustomizeOkHttpClient = builder.customizeOkHttpClient;
        this.mCustomizeRxCache = builder.customizeRxCache;
        this.mCustomizeGson = builder.customizeGson;
        this.mPrintHttpLogLevel = builder.printHttpLogLevel;
        this.mFormatHttpMsg = builder.formatHttpMsg;
        this.mCacheFactory = builder.cacheFactory;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Singleton
    @Provides
    @Nullable
    List<Interceptor> provideInterceptors() {
        Timber.d( "provideInterceptors");
        return mInterceptors;
    }

    @Singleton
    @Provides
    HttpUrl provideBaseUrl() { //提供BaseUrl, 否则默认使用 <"https://api.github.com/">
        Timber.d( "provideBaseUrl");
        if (mBaseUrl != null) {
            HttpUrl httpUrl = mBaseUrl.url();
            if (httpUrl != null) {
                return httpUrl;
            }
        }
        return mApiUrl == null ? HttpUrl.parse("https://api.github.com/") : mApiUrl;
    }

    @Singleton
    @Provides
    BaseImageLoaderStrategy provideImageLoaderStrategy() {
        Timber.d( "provideImageLoaderStrategy");
        return mLoaderStrategy == null ? new GlideImageLoaderStrategy() : mLoaderStrategy;
    }

    @Singleton
    @Provides
    @Nullable
    HttpHandler provideHttpHandler() {
        Timber.d( "provideHttpHandler");
        return mHttpHandler;
    }

    @Singleton
    @Provides
    File provideCacheFile(Application application) {
        Timber.d( "provideCacheFile");
        return mCacheFile == null ? DataHelper.getCacheFile(application) : mCacheFile;
    }

    @Singleton
    @Provides
    @Nullable
    HttpModule.CustomizeRetrofit provideConfigRetrofit() {
        Timber.d( "provideConfigRetrofit");
        return mCustomizeRetrofit;
    }

    @Singleton
    @Provides
    @Nullable
    HttpModule.CustomizeOkHttpClient provideConfigOkHttpClient() {
        Timber.d( "provideConfigOkHttpClient");
        return mCustomizeOkHttpClient;
    }

    @Singleton
    @Provides
    @Nullable
    HttpModule.CustomizeRxCache provideConfigRxCache() {
        Timber.d( "provideConfigRxCache");
        return mCustomizeRxCache;
    }

    @Singleton
    @Provides
    @Nullable
    AppModule.CustomizeGson provideConfigGson() {
        Timber.d( "provideConfigGson");
        return mCustomizeGson;
    }

    @Singleton
    @Provides
    HttpLogInterceptor.Level providePrintHttpLogLevel() {
        Timber.d( "providePrintHttpLogLevel");
        return mPrintHttpLogLevel == null ? HttpLogInterceptor.Level.ALL : mPrintHttpLogLevel;
    }

    @Singleton
    @Provides
    FormatHttpMsg provideFormatPrinter(){
        Timber.d( "provideFormatPrinter");
        return mFormatHttpMsg == null ? new FormatHttpMessage() : mFormatHttpMsg;
    }

    @Singleton
    @Provides
    Cache.Factory provideCacheFactory(Application application) {
        Timber.d( "provideCacheFactory");
        return mCacheFactory == null ? type -> {
            switch (type.getCacheTypeId()) {
                case CacheType.CACHE_TYPE_ID:
                case CacheType.ACTIVITY_CACHE_TYPE_ID:
                case CacheType.FRAGMENT_CACHE_TYPE_ID:
                    return new IntelligentCache(type.calculateCacheSize(application));
                default:
                    return new LruCache(type.calculateCacheSize(application));
            }
        } : mCacheFactory;
    }

    public static final class Builder {
        private HttpUrl apiUrl;
        private BaseUrl baseUrl;
        private BaseImageLoaderStrategy loaderStrategy;
        private HttpHandler httpHandler;
        private List<Interceptor> interceptors;
        private File cacheFile;
        private HttpModule.CustomizeRetrofit CustomizeRetrofit;
        private HttpModule.CustomizeOkHttpClient customizeOkHttpClient;
        private HttpModule.CustomizeRxCache customizeRxCache;
        private AppModule.CustomizeGson customizeGson;
        private HttpLogInterceptor.Level printHttpLogLevel;
        private FormatHttpMsg formatHttpMsg;
        private Cache.Factory cacheFactory;

        private Builder() {
        }

        public Builder baseurl(String baseUrl) {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw new NullPointerException("BaseUrl can not be empty");
            }
            this.apiUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseurl(BaseUrl baseUrl) {
            this.baseUrl = Preconditions.checkNotNull(baseUrl, BaseUrl.class.getCanonicalName() + "can not be null.");
            return this;
        }

        public Builder imageLoaderStrategy(BaseImageLoaderStrategy loaderStrategy) {//用来请求网络图片
            this.loaderStrategy = loaderStrategy;
            return this;
        }

        public Builder httpHandler(HttpHandler httpHandler) {//用来处理http响应结果
            this.httpHandler = httpHandler;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {//动态添加任意个interceptor
            if (interceptors == null)
                interceptors = new ArrayList<>();
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        public Builder retrofit(HttpModule.CustomizeRetrofit CustomizeRetrofit) {
            this.CustomizeRetrofit = CustomizeRetrofit;
            return this;
        }

        public Builder okHttpClient(HttpModule.CustomizeOkHttpClient customizeOkHttpClient) {
            this.customizeOkHttpClient = customizeOkHttpClient;
            return this;
        }

        public Builder rxCache(HttpModule.CustomizeRxCache customizeRxCache) {
            this.customizeRxCache = customizeRxCache;
            return this;
        }

        public Builder gson(AppModule.CustomizeGson customizeGson) {
            this.customizeGson = customizeGson;
            return this;
        }

        public Builder printHttpLogLevel(HttpLogInterceptor.Level printHttpLogLevel) {//是否让框架打印 Http 的请求和响应信息
            this.printHttpLogLevel = Preconditions.checkNotNull(printHttpLogLevel, "The printHttpLogLevel can not be null, use HttpLogInterceptor.Level.NONE instead.");
            return this;
        }

        public Builder formatPrinter(FormatHttpMsg formatHttpMsg){
            this.formatHttpMsg = Preconditions.checkNotNull(formatHttpMsg, FormatHttpMsg.class.getCanonicalName() + "can not be null.");
            return this;
        }

        public Builder cacheFactory(Cache.Factory cacheFactory) {
            this.cacheFactory = cacheFactory;
            return this;
        }

        public GlutinousRiceBuilder build() {
            return new GlutinousRiceBuilder(this);
        }

    }
}
