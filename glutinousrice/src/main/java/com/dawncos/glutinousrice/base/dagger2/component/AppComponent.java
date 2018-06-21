package com.dawncos.glutinousrice.base.dagger2.component;

import android.app.Application;

import com.dawncos.glutinousrice.base.android.application.AppLifecycle;
import com.dawncos.glutinousrice.base.android.repository.IRepositoryManager;
import com.dawncos.glutinousrice.base.cache.Cache;
import com.dawncos.glutinousrice.base.dagger2.module.AppModule;
import com.dawncos.glutinousrice.base.dagger2.module.GlutinousRiceBuilder;
import com.dawncos.glutinousrice.base.dagger2.module.HttpModule;
import com.dawncos.glutinousrice.http.imageloader.ImageLoader;
import com.dawncos.glutinousrice.utils.android.ActivityHelper;
import com.google.gson.Gson;

import java.io.File;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * -----------------------------------------------
 * 通过此接口的实现类即可调用对应的方法拿到 Dagger2 提供的对应实例
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Singleton
@Component(modules = {AppModule.class, HttpModule.class, GlutinousRiceBuilder.class})
public interface AppComponent {
    Application application();

    ActivityHelper appManager();

    IRepositoryManager repositoryManager();

    ImageLoader imageLoader();

    OkHttpClient okHttpClient();

    Gson gson();

    File cacheFile();

    Cache<String, Object> cache();

    Cache.Factory cacheFactory();

    void inject(AppLifecycle lifecycle);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        Builder glutinousRiceBuilder(GlutinousRiceBuilder glutinousRiceBuilder);
        AppComponent build();
    }
}
