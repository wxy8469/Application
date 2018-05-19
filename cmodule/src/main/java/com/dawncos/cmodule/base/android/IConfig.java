package com.dawncos.cmodule.base.android;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.dawncos.cmodule.base.delegate.AppDelegate;
import com.dawncos.cmodule.base.delegate.AppDelegateImp;
import com.dawncos.cmodule.base.dagger2.module.GlobalConfigModule;

import java.util.List;

/**
 * ================================================
 * {@link IConfig} 给框架配置一些参数,自己实现此接口后,在 AndroidManifest 用meta-data标签
 * 声明该实现类，{@link AppDelegateImp}构造方法中用反射机制初始化
 *
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * ================================================
 */
public interface IConfig {
    /**
     * 使用{@link GlobalConfigModule.Builder}给框架配置一些配置参数
     *
     * @param context
     * @param builder
     */
    void applyOptions(Context context, GlobalConfigModule.Builder builder);

    /**
     * 使用{@link AppDelegate}在Application的生命周期中注入一些操作
     *
     * @param context
     * @param lifecycles
     */
    void injectAppLifecycle(Context context, List<AppDelegate> lifecycles);

    /**
     * 使用{@link Application.ActivityLifecycleCallbacks}在Activity的生命周期中注入一些操作
     *
     * @param context
     * @param lifecycles
     */
    void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles);


    /**
     * 使用{@link FragmentManager.FragmentLifecycleCallbacks}在Fragment的生命周期中注入一些操作
     *
     * @param context
     * @param lifecycles
     */
    void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles);
}
