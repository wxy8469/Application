package com.dawncos.cmodule.base.dagger2.component;

import android.app.Application;
import android.content.Context;

import com.dawncos.cmodule.base.delegate.AppDelegateImp;
import com.google.gson.Gson;
import com.dawncos.cmodule.base.dagger2.module.AppModule;
import com.dawncos.cmodule.base.dagger2.module.ClientModule;
import com.dawncos.cmodule.base.dagger2.module.GlobalConfigModule;
import com.dawncos.cmodule.http.imageloader.ImageLoader;
import com.dawncos.cmodule.base.android.AppManager;
import com.dawncos.cmodule.base.android.IRepositoryManager;
import com.dawncos.cmodule.base.cache.Cache;
import com.dawncos.cmodule.utils.android.CModuleUtils;

import java.io.File;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import okhttp3.OkHttpClient;

/**
 * ================================================
 * 可通过 {@link CModuleUtils#obtainAppComponentFromContext(Context)} 拿到此接口的实现类
 * 拥有此接口的实现类即可调用对应的方法拿到 Dagger2 提供的对应实例
 *
 ** Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * ================================================
 */
@Singleton
@Component(modules = {AppModule.class, ClientModule.class, GlobalConfigModule.class})
public interface AppComponent {
    Application application();

    //用于管理所有 activity
    AppManager appManager();

    //用于管理网络请求层,以及数据缓存层
    IRepositoryManager repositoryManager();

    //RxJava 错误处理管理类
    RxErrorHandler rxErrorHandler();

    //图片管理器,用于加载图片的管理类,默认使用 Glide ,使用策略模式,可在运行时替换框架
    ImageLoader imageLoader();

    OkHttpClient okHttpClient();

    //gson
    Gson gson();

    //缓存文件根目录(RxCache 和 Glide 的缓存都已经作为子文件夹放在这个根目录下),应该将所有缓存都放到这个根目录下,便于管理和清理,可在 GlobalConfigModule 里配置
    File cacheFile();

    //用来存取一些整个App公用的数据,切勿大量存放大容量数据
    Cache<String, Object> extras();

    //用于创建框架所需缓存对象的工厂
    Cache.Factory cacheFactory();

    void inject(AppDelegateImp delegate);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        Builder globalConfigModule(GlobalConfigModule globalConfigModule);
        AppComponent build();
    }
}
