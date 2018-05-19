package com.dawncos.cmodule.base.dagger2.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dawncos.cmodule.base.lifecycle.ActivityLifecycle;
import com.dawncos.cmodule.base.lifecycle.FragmentLifecycle;
import com.dawncos.cmodule.base.android.IRepositoryManager;
import com.dawncos.cmodule.base.android.RepositoryManager;
import com.dawncos.cmodule.base.cache.Cache;
import com.dawncos.cmodule.base.cache.CacheType;
import com.dawncos.cmodule.base.lifecycle.ActivityLifecycleForRxLifecycle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * ================================================
 * 提供一些框架必须的实例的 {@link Module}
 * <p>
 * Created by JessYan on 8/4/2016.
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@Module
public abstract class AppModule {

    @Singleton
    @Provides
    static Gson provideGson(Application application, @Nullable GsonConfiguration configuration) {
        GsonBuilder builder = new GsonBuilder();
        if (configuration != null)
            configuration.configGson(application, builder);
        return builder.create();
    }

    @Binds
    abstract IRepositoryManager bindRepositoryManager(RepositoryManager repositoryManager);

    @Singleton
    @Provides
    static Cache<String, Object> provideExtras(Cache.Factory cacheFactory) {
        return cacheFactory.build(CacheType.EXTRAS);
    }

    @Binds
    @Named("ActivityLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycle(ActivityLifecycle activityLifecycle);

    @Binds
    @Named("ActivityLifecycleForRxLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycleForRxLifecycle(ActivityLifecycleForRxLifecycle activityLifecycleForRxLifecycle);

    @Binds
    abstract FragmentManager.FragmentLifecycleCallbacks bindFragmentLifecycle(FragmentLifecycle fragmentLifecycle);

    @Singleton
    @Provides
    static List<FragmentManager.FragmentLifecycleCallbacks> provideFragmentLifecycles(){
        return new ArrayList<>();
    }

    public interface GsonConfiguration {
        void configGson(Context context, GsonBuilder builder);
    }
}
