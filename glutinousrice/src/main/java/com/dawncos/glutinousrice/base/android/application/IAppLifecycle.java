package com.dawncos.glutinousrice.base.android.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;

/**
 * -----------------------------------------------
 * 用于代理 {@link Application} 的生命周期
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IAppLifecycle {
    void attachBaseContext(@NonNull Context base);

    void onCreate(@NonNull Application application);

    void onTerminate(@NonNull Application application);

    AppComponent getAppComponent();
}
