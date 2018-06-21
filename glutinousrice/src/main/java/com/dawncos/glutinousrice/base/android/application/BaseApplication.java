package com.dawncos.glutinousrice.base.android.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;
import com.dawncos.glutinousrice.utils.others.Preconditions;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * {@link BaseApplication}
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class BaseApplication extends Application implements IAppLifecycle {
    private IAppLifecycle iAppLifecycle;

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (iAppLifecycle == null)
            this.iAppLifecycle = new AppLifecycle(base);
        this.iAppLifecycle.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (iAppLifecycle != null)
            this.iAppLifecycle.onCreate(this);
    }

    @Override
    public void onTerminate() {
        Timber.d( "onTerminate");
        super.onTerminate();
        if (iAppLifecycle != null)
            this.iAppLifecycle.onTerminate(this);
    }

    @NonNull
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(iAppLifecycle, "%s cannot be null",
                AppLifecycle.class.getName());
        return this.iAppLifecycle.getAppComponent();
    }

    @Override
    public void onCreate(@NonNull Application application) {

    }

    @Override
    public void onTerminate(@NonNull Application application) {

    }

}
