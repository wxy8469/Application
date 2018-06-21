package com.dawncos.glutinousrice.base.android.application;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.dawncos.glutinousrice.base.android.GlutinousRiceModule;
import com.dawncos.glutinousrice.base.cache.IntelligentCache;
import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;
import com.dawncos.glutinousrice.base.dagger2.component.DaggerAppComponent;
import com.dawncos.glutinousrice.base.dagger2.module.GlutinousRiceBuilder;
import com.dawncos.glutinousrice.utils.android.ManifestParserHelper;
import com.dawncos.glutinousrice.utils.others.Preconditions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * 不强求继承{@link BaseApplication} 可以通过{@link AppLifecycle}
 * 代理Application的生命周期。
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class AppLifecycle implements IAppLifecycle, ComponentCallbacks2 {
    private Application mApplication;
    private AppComponent mAppComponent;

    @Inject
    @Named("ActivityLifecycle")
    protected Application.ActivityLifecycleCallbacks mActivityLifecycle;

    private List<GlutinousRiceModule> glutinousRiceModules;
    private List<IAppLifecycle> appLifecycles = new ArrayList<>();
    private List<Application.ActivityLifecycleCallbacks> callbacks = new ArrayList<>();

    public AppLifecycle(@NonNull Context context) {
        //获取框架外配置信息
        this.glutinousRiceModules = new ManifestParserHelper(context).parse();
        for (GlutinousRiceModule glutinousRiceModule : glutinousRiceModules) {
            glutinousRiceModule.expandApp(context, appLifecycles);
            glutinousRiceModule.expandActivity(context, callbacks);
        }
    }

    @Override
    public void attachBaseContext(@NonNull Context base) {
        //执行框架外部，开发者扩展的IApplication#attachBaseContext()
        for (IAppLifecycle lifecycle : appLifecycles) {
            lifecycle.attachBaseContext(base);
        }
    }

    @Override
    public void onCreate(@NonNull Application application) {
        this.mApplication = application;
        mAppComponent = DaggerAppComponent
                .builder()
                .application(mApplication)
                .glutinousRiceBuilder(get(mApplication, glutinousRiceModules))
                .build();
        mAppComponent.inject(this);
        mAppComponent.cache().put(IntelligentCache.KEY_KEEP + GlutinousRiceModule.class.getName(), glutinousRiceModules);
        this.glutinousRiceModules = null;
        //注册框架内部，ActivityLifecycle的生命周期
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycle);
        //注册框架外部，开发者扩展的Activity的生命周期
        for (Application.ActivityLifecycleCallbacks lifecycle : callbacks) {
            mApplication.registerActivityLifecycleCallbacks(lifecycle);
        }
        //注册回调，内存紧张时释放部分内存
        mApplication.registerComponentCallbacks(this);
        //执行框架外部，开发者扩展的IApplication#onCreate
        for (IAppLifecycle lifecycle : appLifecycles) {
            lifecycle.onCreate(mApplication);
        }
    }

    @Override
    public void onTerminate(@NonNull Application application) {
        Timber.d( "onTerminate");
        if (mActivityLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycle);
        }
        if (callbacks != null && callbacks.size() > 0) {
            for (Application.ActivityLifecycleCallbacks lifecycle : callbacks) {
                mApplication.unregisterActivityLifecycleCallbacks(lifecycle);
            }
        }
        if (appLifecycles != null && appLifecycles.size() > 0) {
            for (IAppLifecycle lifecycle : appLifecycles) {
                lifecycle.onTerminate(mApplication);
            }
        }
        this.mAppComponent = null;
        this.mActivityLifecycle = null;
        this.callbacks = null;
        this.appLifecycles = null;
        this.mApplication = null;
    }

    private GlutinousRiceBuilder get(Context context, List<GlutinousRiceModule> modules) {
        GlutinousRiceBuilder.Builder builder = GlutinousRiceBuilder.builder();
        for (GlutinousRiceModule module : modules) {
            module.applyOptions(context, builder);
        }
        return builder.build();
    }

    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Timber.d( "getAppComponent");
        Preconditions.checkNotNull(mAppComponent,
                "%s cannot be null,first call %s#onCreate(Application) in %s#onCreate()",
                AppComponent.class.getName(), getClass().getName(), Application.class.getName());
        return mAppComponent;
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.d( "onTrimMemory");
//            trimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Timber.d( "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        Timber.d( "onLowMemory");
//            clearMemory();
    }

}

