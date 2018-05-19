package com.dawncos.cmodule.base.lifecycle;

import com.dawncos.cmodule.base.mvp.IView;
import com.dawncos.cmodule.utils.others.Preconditions;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.annotations.NonNull;

/**
 * ================================================
 * 使用此类操作 RxLifecycle 的特性
 * <p>
 * Created by JessYan on 26/08/2017 17:52
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */

public class RxLifecycleUtils {

    private RxLifecycleUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 绑定 Activity 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final ActivityEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof IRxLifecycleForActivity) {
            return bindUntilEvent((IRxLifecycleForActivity) view, event);
        } else {
            throw new IllegalArgumentException("view isn't IRxLifecycleForActivity");
        }
    }

    /**
     * 绑定 Fragment 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final FragmentEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof IRxLifecycleForFragment) {
            return bindUntilEvent((IRxLifecycleForFragment) view, event);
        } else {
            throw new IllegalArgumentException("view isn't IRxLifecycleForFragment");
        }
    }

    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final IRxLifecycle<R> IRxLifecycle,
                                                                final R event) {
        Preconditions.checkNotNull(IRxLifecycle, "IRxLifecycle == null");
        return RxLifecycle.bindUntilEvent(IRxLifecycle.provideLifecycleSubject(), event);
    }


    /**
     * 绑定 Activity/Fragment 的生命周期
     *
     * @param view
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IView view) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof IRxLifecycle) {
            return bindToLifecycle((IRxLifecycle) view);
        } else {
            throw new IllegalArgumentException("view isn't IRxLifecycle");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IRxLifecycle IRxLifecycle) {
        Preconditions.checkNotNull(IRxLifecycle, "IRxLifecycle == null");
        if (IRxLifecycle instanceof IRxLifecycleForActivity) {
            return RxLifecycleAndroid.bindActivity(((IRxLifecycleForActivity) IRxLifecycle).provideLifecycleSubject());
        } else if (IRxLifecycle instanceof IRxLifecycleForFragment) {
            return RxLifecycleAndroid.bindFragment(((IRxLifecycleForFragment) IRxLifecycle).provideLifecycleSubject());
        } else {
            throw new IllegalArgumentException("IRxLifecycle not match");
        }
    }

}
