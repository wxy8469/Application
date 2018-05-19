package com.dawncos.cmodule.base.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.ContentProvider;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.dawncos.cmodule.base.android.IConfig;
import com.dawncos.cmodule.base.cache.IntelligentCache;
import com.dawncos.cmodule.base.dagger2.component.AppComponent;
import com.dawncos.cmodule.base.dagger2.component.DaggerAppComponent;
import com.dawncos.cmodule.base.dagger2.module.GlobalConfigModule;
import com.dawncos.cmodule.utils.android.ManifestParser;
import com.dawncos.cmodule.utils.others.Preconditions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * ================================================
 * AppDelegate可以代理Application的生命周期，因为Java只能单继承所以当遇到某些三方库需要继承它的
 * Application的时候,就只有自定义 Application 并继承于三方库的Application，这时可以调用
 * AppDelegate对应的方法(Application一定要实现IApplication接口),框架就能照常运行
 * ================================================
 */
public class AppDelegateImp implements IApplication, AppDelegate {
    private Application mApplication;
    private AppComponent mAppComponent;
    private ComponentCallbacks2 mComponentCallback;

    @Inject
    @Named("ActivityLifecycle")
    protected Application.ActivityLifecycleCallbacks mActivityLifecycle;
    @Inject
    @Named("ActivityLifecycleForRxLifecycle")
    protected Application.ActivityLifecycleCallbacks mActivityLifecycleForRxLifecycle;

    private List<IConfig> iConfigs;
    private List<AppDelegate> listAppLifecycles = new ArrayList<>();
    private List<Application.ActivityLifecycleCallbacks> listActivityLifecycles = new ArrayList<>();

    public AppDelegateImp(@NonNull Context context) {

        //用反射, 将AndroidManifest.xml中带有IConfig标签的class
        //转成对象集合（List<IConfig>）
        this.iConfigs = new ManifestParser(context).parse();

        //遍历之前获得的集合, 执行每一个 IConfig 实现类的某些方法
        for (IConfig iConfig : iConfigs) {

            //将框架外部, 开发者实现的 Application 的生命周期回调 (AppDelegate)
            //存入 listAppLifecycles 集合 (此时还未注册回调)
            iConfig.injectAppLifecycle(context, listAppLifecycles);

            //将框架外部, 开发者实现的 Activity 的生命周期回调
            //(ActivityLifecycleCallbacks) 存入listActivityLifecycles集合(此时还未注册回调)
            iConfig.injectActivityLifecycle(context, listActivityLifecycles);
        }
    }

    @Override
    public void attachBaseContext(@NonNull Context base) {

        //遍历mAppLifecycles, 执行所有已注册的 AppDelegate 的
        //attachBaseContext() 方法 (框架外部, 开发者扩展的逻辑)
        for (AppDelegate lifecycle : listAppLifecycles) {
            lifecycle.attachBaseContext(base);
        }
    }

    @Override
    public void onCreate(@NonNull Application application) {
        this.mApplication = application;
        mAppComponent = DaggerAppComponent
                .builder()
                .application(mApplication)//提供application
                .globalConfigModule(getGlobalConfigModule(mApplication, iConfigs))//全局配置
                .build();
        mAppComponent.inject(this);

        //将 IConfig 的实现类的集合存放到缓存 Cache, 可以随时获取
        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
        //否则存储在LRU算法的存储空间中 (大于或等于缓存所能允许的最大size,则会根据LRU算法清除之前的条目)
        //前提是 extras 使用的是 IntelligentCache (框架默认使用)
        mAppComponent.extras().put(IntelligentCache.KEY_KEEP + IConfig.class.getName(), iConfigs);

        this.iConfigs = null;

        //注册框架内部已实现的 Activity 生命周期逻辑
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycle);

        //注册框架内部已实现的 RxLifecycle 逻辑
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleForRxLifecycle);

        //注册框架外部, 开发者扩展的 Activity 生命周期逻辑
        //每个 IConfig 的实现类可以声明多个 Activity 的生命周期回调
        //也可以有 N 个 IConfig 的实现类 (完美支持组件化项目 各个 Module 的各种独特需求)
        for (Application.ActivityLifecycleCallbacks lifecycle : listActivityLifecycles) {
            mApplication.registerActivityLifecycleCallbacks(lifecycle);
        }

        mComponentCallback = new AppComponentCallbacks(mApplication, mAppComponent);

        //注册回掉: 内存紧张时释放部分内存
        mApplication.registerComponentCallbacks(mComponentCallback);

        //执行框架外部, 开发者扩展的 IApplication onCreate 逻辑
        for (AppDelegate lifecycle : listAppLifecycles) {
            lifecycle.onCreate(mApplication);
        }

    }


    @Override
    public void onTerminate(@NonNull Application application) {
        if (mActivityLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycle);
        }
        if (mActivityLifecycleForRxLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleForRxLifecycle);
        }
        if (mComponentCallback != null) {
            mApplication.unregisterComponentCallbacks(mComponentCallback);
        }
        if (listActivityLifecycles != null && listActivityLifecycles.size() > 0) {
            for (Application.ActivityLifecycleCallbacks lifecycle : listActivityLifecycles) {
                mApplication.unregisterActivityLifecycleCallbacks(lifecycle);
            }
        }
        if (listAppLifecycles != null && listAppLifecycles.size() > 0) {
            for (AppDelegate lifecycle : listAppLifecycles) {
                lifecycle.onTerminate(mApplication);
            }
        }
        this.mAppComponent = null;
        this.mActivityLifecycle = null;
        this.mActivityLifecycleForRxLifecycle = null;
        this.listActivityLifecycles = null;
        this.mComponentCallback = null;
        this.listAppLifecycles = null;
        this.mApplication = null;
    }


    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明{@link IConfig}的实现类,和Glide的配置方式相似
     *
     * @return GlobalConfigModule
     */
    private GlobalConfigModule getGlobalConfigModule(Context context, List<IConfig> modules) {

        GlobalConfigModule.Builder builder = GlobalConfigModule
                .builder();

        //遍历 IConfig 集合, 给全局配置 GlobalConfigModule 添加参数
        for (IConfig module : modules) {
            module.applyOptions(context, builder);
        }

        return builder.build();
    }


    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(mAppComponent,
                "%s cannot be null,first call %s#onCreate(Application) in %s#onCreate()",
                AppComponent.class.getName(), getClass().getName(), Application.class.getName());
        return mAppComponent;
    }


    /**
     * {@link ComponentCallbacks2} 是一个细粒度的内存回收管理回调
     * {@link Application}、{@link Activity}、{@link Service}、{@link ContentProvider}、{@link Fragment} 实现了 {@link ComponentCallbacks2} 接口
     * 开发者应该实现 {@link ComponentCallbacks2#onTrimMemory(int)} 方法, 细粒度 release 内存, 参数的值不同可以体现出不同程度的内存可用情况
     * 响应 {@link ComponentCallbacks2#onTrimMemory(int)} 回调, 开发者的 IApplication 会存活的更持久, 有利于用户体验
     * 不响应 {@link ComponentCallbacks2#onTrimMemory(int)} 回调, 系统 kill 掉进程的几率更大
     */
    private static class AppComponentCallbacks implements ComponentCallbacks2 {
        private Application mApplication;
        private AppComponent mAppComponent;

        public AppComponentCallbacks(Application application, AppComponent appComponent) {
            this.mApplication = application;
            this.mAppComponent = appComponent;
        }

        /**
         * 在你的 IApplication 生命周期的任何阶段, {@link ComponentCallbacks2#onTrimMemory(int)} 发生的回调都预示着你设备的内存资源已经开始紧张
         * 你应该根据 {@link ComponentCallbacks2#onTrimMemory(int)} 发生回调时的内存级别来进一步决定释放哪些资源
         * {@link ComponentCallbacks2#onTrimMemory(int)} 的回调可以发生在 {@link Application}、{@link Activity}、{@link Service}、{@link ContentProvider}、{@link Fragment}
         *
         * @param level 内存级别
         * @see <a href="https://developer.android.com/reference/android/content/ComponentCallbacks2.html#TRIM_MEMORY_RUNNING_MODERATE">level 官方文档</a>
         */
        @Override
        public void onTrimMemory(int level) {
            //状态1. 当开发者的 IApplication 正在运行
            //设备开始运行缓慢, 不会被 kill, 也不会被列为可杀死的, 但是设备此时正运行于低内存状态下, 系统开始触发杀死 LRU 列表中的进程的机制
//                case TRIM_MEMORY_RUNNING_MODERATE:


            //设备运行更缓慢了, 不会被 kill, 但请你回收 unused 资源, 以便提升系统的性能, 你应该释放不用的资源用来提升系统性能 (但是这也会直接影响到你的 IApplication 的性能)
//                case TRIM_MEMORY_RUNNING_LOW:


            //设备运行特别慢, 当前 IApplication 还不会被杀死, 但是系统已经把 LRU 列表中的大多数进程都已经杀死, 因此你应该立即释放所有非必须的资源
            //如果系统不能回收到足够的 RAM 数量, 系统将会清除所有的 LRU 列表中的进程, 并且开始杀死那些之前被认为不应该杀死的进程, 例如那个包含了一个运行态 Service 的进程
//                case TRIM_MEMORY_RUNNING_CRITICAL:


            //状态2. 当前 IApplication UI 不再可见, 这是一个回收大个资源的好时机
//                case TRIM_MEMORY_UI_HIDDEN:


            //状态3. 当前的 IApplication 进程被置于 Background LRU 列表中
            //进程位于 LRU 列表的上端, 尽管你的 IApplication 进程并不是处于被杀掉的高危险状态, 但系统可能已经开始杀掉 LRU 列表中的其他进程了
            //你应该释放那些容易恢复的资源, 以便于你的进程可以保留下来, 这样当用户回退到你的 IApplication 的时候才能够迅速恢复
//                case TRIM_MEMORY_BACKGROUND:


            //系统正运行于低内存状态并且你的进程已经已经接近 LRU 列表的中部位置, 如果系统的内存开始变得更加紧张, 你的进程是有可能被杀死的
//                case TRIM_MEMORY_MODERATE:


            //系统正运行与低内存的状态并且你的进程正处于 LRU 列表中最容易被杀掉的位置, 你应该释放任何不影响你的 IApplication 恢复状态的资源
            //低于 API 14 的 IApplication 可以使用 onLowMemory 回调
//                case TRIM_MEMORY_COMPLETE:
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        /**
         * 当系统开始清除 LRU 列表中的进程时, 尽管它会首先按照 LRU 的顺序来清除, 但是它同样会考虑进程的内存使用量, 因此消耗越少的进程则越容易被留下来
         * {@link ComponentCallbacks2#onTrimMemory(int)} 的回调是在 API 14 才被加进来的, 对于老的版本, 你可以使用 {@link ComponentCallbacks2#onLowMemory} 方法来进行兼容
         * {@link ComponentCallbacks2#onLowMemory} 相当于 {@code onTrimMemory(TRIM_MEMORY_COMPLETE)}
         *
         * @see #TRIM_MEMORY_COMPLETE
         */
        @Override
        public void onLowMemory() {
            //系统正运行与低内存的状态并且你的进程正处于 LRU 列表中最容易被杀掉的位置, 你应该释放任何不影响你的 IApplication 恢复状态的资源
        }
    }

}

