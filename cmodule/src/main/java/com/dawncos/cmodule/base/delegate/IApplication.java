package com.dawncos.cmodule.base.delegate;

import android.support.annotation.NonNull;

import com.dawncos.cmodule.base.android.BaseApplication;
import com.dawncos.cmodule.base.dagger2.component.AppComponent;

/**
 * ================================================
 * 每个 {@link android.app.Application} 都需要实现此类
 * 以满足框架规范
 * @see BaseApplication
 * ================================================
 */
public interface IApplication {
    @NonNull
    AppComponent getAppComponent();
}
