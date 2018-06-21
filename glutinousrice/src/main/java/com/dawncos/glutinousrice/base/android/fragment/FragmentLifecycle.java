package com.dawncos.glutinousrice.base.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.dawncos.glutinousrice.utils.android.ModuleUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * {@link FragmentManager.FragmentLifecycleCallbacks} 默认实现类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
@Singleton
public class FragmentLifecycle extends FragmentManager.FragmentLifecycleCallbacks {

    private Unbinder mUnbinder;

    @Inject
    public FragmentLifecycle() {
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        Timber.d(" - onFragmentAttached");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.ATTACH);
        }
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        Timber.d(" - onFragmentCreated");
        if (f instanceof IFragment) {
            ((IFragment)f).provideCache();
            if (((IFragment)f).useEventBus())//如果要使用eventbus请将此方法返回true
                EventBus.getDefault().register(f);//注册到事件主线
            ((IFragment)f).setupFragmentComponent(ModuleUtil.getAppComponent(f.getContext()));
        }
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.CREATE);
        }
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        Timber.d(" - onFragmentViewCreated");
        //绑定到butterknife
        if (v != null)
            mUnbinder = ButterKnife.bind(f, v);
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.CREATE_VIEW);
        }
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        Timber.d(" - onFragmentActivityCreated");
        ((IFragment)f).initData(savedInstanceState);
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentStarted");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.START);
        }
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentResumed");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.RESUME);
        }
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentPaused");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.PAUSE);
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentStopped");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.STOP);
        }
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        Timber.d(" - onFragmentSaveInstanceState");
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentViewDestroyed");
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                mUnbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                //fix Bindings already cleared
                Timber.d("onDestroyView: %s" ,e.getMessage());
            }
        }
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.DESTROY_VIEW);
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentDestroyed");
        if (f != null && ((IFragment)f).useEventBus())//如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(f);//注册到事件主线
        this.mUnbinder = null;
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.DESTROY);
        }
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        Timber.d(" - onFragmentDetached");
        if (f instanceof IRxLifecycleForFragment) {
            getSubject(f).onNext(FragmentEvent.DETACH);
        }
    }

    private Subject<FragmentEvent> getSubject(Fragment fragment) {
        return ((IRxLifecycleForFragment) fragment).provideLifecycleSubject();
    }

}
