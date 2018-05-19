package com.dawncos.cmodule.base.android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dawncos.cmodule.base.delegate.AppDelegate;
import com.dawncos.cmodule.base.delegate.AppDelegateImp;
import com.dawncos.cmodule.base.delegate.IApplication;
import com.dawncos.cmodule.base.dagger2.component.AppComponent;
import com.dawncos.cmodule.utils.android.CModuleUtils;
import com.dawncos.cmodule.utils.others.Preconditions;

public class BaseApplication extends Application implements IApplication {
    private AppDelegate mAppDelegate;

    /**
     * 这里会在 {@link BaseApplication#onCreate} 之前被调用,可以做一些较早的初始化
     * 常用于 MultiDex 以及插件化框架的初始化
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mAppDelegate == null)
            this.mAppDelegate = new AppDelegateImp(base);
        this.mAppDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mAppDelegate != null)
            this.mAppDelegate.onCreate(this);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAppDelegate != null)
            this.mAppDelegate.onTerminate(this);
    }

    /**
     * 返回 {@link AppComponent}  方便其它地方使用,
     * 在 {@link #getAppComponent()} 拿到对象后都可以直接使用
     *
     * @see CModuleUtils#obtainAppComponentFromContext(Context)
     * 可直接获取 {@link AppComponent}
     * @return AppComponent
     */
    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppDelegateImp.class.getName());
        Preconditions.checkState(mAppDelegate instanceof IApplication, "%s must be implements %s", AppDelegateImp.class.getName(), IApplication.class.getName());
        return ((IApplication) mAppDelegate).getAppComponent();
    }

}
