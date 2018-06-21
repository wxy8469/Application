package com.dawncos.glutinousrice.base.dagger2.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.dawncos.glutinousrice.base.android.activity.ActivityLifecycle;
import com.dawncos.glutinousrice.base.android.fragment.FragmentLifecycle;
import com.dawncos.glutinousrice.base.android.repository.IRepositoryManager;
import com.dawncos.glutinousrice.base.android.repository.RepositoryManager;
import com.dawncos.glutinousrice.base.cache.Cache;
import com.dawncos.glutinousrice.base.cache.CacheType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * 提供框架必须的实例 {@link Module}
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Module
public abstract class AppModule {
    @Binds
    abstract IRepositoryManager bindRepositoryManager(RepositoryManager repositoryManager);

    @Binds
    @Named("ActivityLifecycle")
    abstract Application.ActivityLifecycleCallbacks bindActivityLifecycle(ActivityLifecycle activityLifecycle);

    @Binds
    abstract FragmentManager.FragmentLifecycleCallbacks bindFragmentLifecycle(FragmentLifecycle fragmentLifecycle);

    @Singleton
    @Provides
    static Gson provideGson(Application application, @Nullable CustomizeGson customizeGson) {
        Timber.d( "provideGson");
        GsonBuilder builder = new GsonBuilder();
        if (customizeGson != null)
            customizeGson.setupGson(application, builder);
        return builder.create();
    }

    @Singleton
    @Provides
    static Cache<String, Object> provideCache(Cache.Factory cacheFactory) {
        return cacheFactory.build(CacheType.CACHE);
    }

    @Singleton
    @Provides
    static List<FragmentManager.FragmentLifecycleCallbacks> provideFragmentLifecycles(){
        return new ArrayList<>();
    }

    public interface CustomizeGson {
        void setupGson(Context context, GsonBuilder builder);
    }
}
