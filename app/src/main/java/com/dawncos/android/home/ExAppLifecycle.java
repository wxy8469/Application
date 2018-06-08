package com.dawncos.android.home;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dawncos.android.BuildConfig;
import com.dawncos.glutinousrice.base.android.application.IAppLifecycle;
import com.dawncos.glutinousrice.base.cache.IntelligentCache;
import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;
import com.dawncos.glutinousrice.utils.android.ModuleUtil;
import com.dawncos.glutinousrice.utils.log.timber.CrashReportingTree;
import com.dawncos.glutinousrice.utils.log.timber.DebugLogTree;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link IAppLifecycle} 的用法
 *
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * ================================================
 */
public class ExAppLifecycle implements IAppLifecycle {
    private Application application;

    @Override
    public void attachBaseContext(@NonNull Context base) {
//          MultiDex.install(base);
    }

    @Override
    public void onCreate(@NonNull Application application) {
        this.application = application;
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        if (BuildConfig.LOG_DEBUG) {
//            Timber.plant(new FileLogTree());
            Timber.plant(new DebugLogTree());
            ButterKnife.setDebug(true);
        } else {
//            Timber.plant(new ReleaseLogTree());
            Timber.plant(new CrashReportingTree());
        }
        Timber.d( "onCreate");
        //LeakCanary 内存泄露检查
        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
        //否则存储在 LRU 算法的存储空间中, 前提是 extras 使用的是 IntelligentCache (框架默认使用)
        getAppComponent().extras().put(IntelligentCache.KEY_KEEP + RefWatcher.class.getName(),
                BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);
        //扩展 AppManager 的远程遥控功能
        getAppComponent().appManager().setHandleListener((appManager, message) -> {
            switch (message.what) {
                //case 0:
                //do something ...
                //   break;
            }
        });
        //Usage:
        //Message msg = new Message();
        //msg.what = 0;
        //AppManager.post(msg); like EventBus
    }

    @Override
    public void onTerminate(@NonNull Application application) {
        Timber.d( "onTerminate");
    }

    @Override
    public AppComponent getAppComponent() {
        Timber.d( "getAppComponent");
        return ModuleUtil.getAppComponent(application);
    }
}
