package com.dawncos.glutinousrice.base.android;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.dawncos.glutinousrice.base.android.application.AppLifecycle;
import com.dawncos.glutinousrice.base.android.application.IAppLifecycle;
import com.dawncos.glutinousrice.base.dagger2.module.GlutinousRiceBuilder;

import java.util.List;

/**
 * -----------------------------------------------
 * {@link GlutinousRiceModule} 给框架配置参数,使用此框架时，需要自己实现此接口后,
 * 在AndroidManifest中用meta-data标签声明该实现类，参考{@link Glide}
 * 的配置方式
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface GlutinousRiceModule {
    /**
     * 使用{@link GlutinousRiceBuilder.Builder}给框架配置参数
     * @param context
     * @param builder
     */
    void applyOptions(Context context, GlutinousRiceBuilder.Builder builder);

    /**
     * 使用{@link AppLifecycle}扩展Application
     *  @param context
     * @param lifecycles
     */
    void expandApp(Context context, List<IAppLifecycle> lifecycles);

    /**
     * 使用{@link Application.ActivityLifecycleCallbacks}扩展Activity
     * @param context
     * @param lifecycles
     */
    void expandActivity(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles);


    /**
     * 使用{@link FragmentManager.FragmentLifecycleCallbacks}扩展Fragment
     * @param context
     * @param lifecycles
     */
    void expandFragment(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles);
}
