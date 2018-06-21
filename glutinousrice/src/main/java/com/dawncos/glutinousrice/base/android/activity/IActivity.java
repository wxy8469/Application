package com.dawncos.glutinousrice.base.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dawncos.glutinousrice.base.cache.Cache;
import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;

/**
 * -----------------------------------------------
 * 框架规范{@link Activity} 需要实现此接口
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IActivity {

    @NonNull
    Cache<String, Object> provideCache();

    void setupActivityComponent(@NonNull AppComponent AppComponent);

    boolean useEventBus();

    boolean useFragment();

    int initView(@Nullable Bundle savedInstanceState);

    void initData(@Nullable Bundle savedInstanceState);

}
