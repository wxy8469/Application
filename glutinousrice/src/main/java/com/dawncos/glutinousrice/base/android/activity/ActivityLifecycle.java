package com.dawncos.glutinousrice.base.android.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.dawncos.glutinousrice.base.android.GlutinousRiceModule;
import com.dawncos.glutinousrice.base.cache.Cache;
import com.dawncos.glutinousrice.base.cache.IntelligentCache;
import com.dawncos.glutinousrice.utils.android.ActivityHelper;
import com.dawncos.glutinousrice.utils.android.ModuleUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * {@link Application.ActivityLifecycleCallbacks} 默认实现类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Singleton
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    @Inject
    ActivityHelper mActivityHelper;
    @Inject
    Application mApplication;
    @Inject
    Cache<String, Object> mExtras;
    @Inject
    Lazy<FragmentManager.FragmentLifecycleCallbacks> mFragmentLifecycle;
    @Inject
    Lazy<List<FragmentManager.FragmentLifecycleCallbacks>> mFragmentLifecycles;

    Cache mCache;
    IActivity mActivity;

    @Inject
    public ActivityLifecycle() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.d( "onActivityCreated");
        mActivity = (IActivity)activity;

        boolean isNotAdd = false;
        if (activity.getIntent() != null)
            isNotAdd = activity.getIntent().getBooleanExtra(ActivityHelper.IS_NOT_ADD_ACTIVITY_LIST, false);
        if (!isNotAdd)
            mActivityHelper.addActivity(activity);

        if (mActivity instanceof IActivity) {
            if (mActivity.useEventBus()){
                EventBus.getDefault().register(mActivity);
            }
            mActivity.setupActivityComponent(ModuleUtil.getAppComponent(activity));
            mCache = mActivity.provideCache();
        }
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.CREATE);
        }

        registerFragmentCallbacks(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.d( "onActivityStarted");
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.START);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.d( "onActivityResumed");
        mActivityHelper.setCurrentActivity(activity);
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.RESUME);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.d( "onActivityPaused");
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.PAUSE);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.d( "onActivityStopped");
        if (mActivityHelper.getCurrentActivity() == activity) {
            mActivityHelper.setCurrentActivity(null);
        }
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.STOP);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.d( "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.d( "onActivityDestroyed");
        mActivityHelper.removeActivity(activity);
        mCache.clear();
        if (activity instanceof IRxLifecycleForActivity) {
            getSubject(activity).onNext(ActivityEvent.DESTROY);
        }
        if (mActivity.useEventBus()){
            EventBus.getDefault().unregister(mActivity);
        }
    }

    private void registerFragmentCallbacks(Activity activity) {
        Timber.d( "registerFragmentCallbacks");
        boolean useFragment = activity instanceof IActivity ? ((IActivity) activity).useFragment() : true;
        if (activity instanceof FragmentActivity && useFragment) {
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycle.get(), true);

            if (mExtras.containsKey(IntelligentCache.KEY_KEEP + GlutinousRiceModule.class.getName())) {
                List<GlutinousRiceModule> modules = (List<GlutinousRiceModule>) mExtras.get(IntelligentCache.KEY_KEEP + GlutinousRiceModule.class.getName());
                for (GlutinousRiceModule module : modules) {
                    module.expandFragment(mApplication, mFragmentLifecycles.get());
                }
                mExtras.remove(IntelligentCache.KEY_KEEP + GlutinousRiceModule.class.getName());
            }
            //注册框架外部, 开发者扩展的 Fragment 生命周期逻辑
            for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles.get()) {
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycle, true);
            }
        }
    }

    private Subject<ActivityEvent> getSubject(Activity activity) {
        Timber.d( "getSubject");
        return ((IRxLifecycleForActivity) activity).provideLifecycleSubject();
    }
}
