package com.dawncos.glutinousrice.utils.Rx;

import com.dawncos.glutinousrice.base.android.IRxLifecycle;
import com.dawncos.glutinousrice.base.android.activity.IRxLifecycleForActivity;
import com.dawncos.glutinousrice.base.android.fragment.IRxLifecycleForFragment;
import com.dawncos.glutinousrice.base.mvp.IView;
import com.dawncos.glutinousrice.utils.others.Preconditions;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * -----------------------------------------------
 * 使用此类操作 RxLifecycle 的特性
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class RxLifecycleUtil {

    private RxLifecycleUtil() {
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

    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final IRxLifecycle<R> IRxLifecycle, final R event) {
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
        Timber.d("bindToLifecycle for IView");
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof IRxLifecycle) {
            return bindToLifecycle((IRxLifecycle) view);
        } else {
            throw new IllegalArgumentException("view isn't IRxLifecycle");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IRxLifecycle IRxLifecycle) {
        Timber.d("bindToLifecycle for IRxLifecycle");
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
