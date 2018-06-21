package com.dawncos.glutinousrice.base.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.dawncos.glutinousrice.base.cache.CacheType;
import com.dawncos.glutinousrice.base.mvp.IPresenter;
import com.dawncos.glutinousrice.utils.android.ModuleUtil;
import com.dawncos.glutinousrice.base.cache.Cache;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

import static com.dawncos.glutinousrice.utils.android.ThirdViewUtil.convertAutoView;


/**
 * -----------------------------------------------
 * {@link BaseActivity}
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public abstract class BaseActivity<P extends IPresenter>
        extends AppCompatActivity implements IActivity, IRxLifecycleForActivity {
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Cache<String, Object> mCache;
    private Unbinder mUnbinder;

    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        try {
            int layoutResID = initView(savedInstanceState);
            if (layoutResID != 0) {
                setContentView(layoutResID);
                mUnbinder = ButterKnife.bind(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(savedInstanceState);
        if (mPresenter != null)
            getLifecycle().addObserver(mPresenter);//添加LifecycleObserver 与Presenter绑定生命周期
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        Timber.d("onCreateView");
        View view = convertAutoView(name, context, attrs);
        return view == null ? super.onCreateView(name, context, attrs) : view;
    }

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        Timber.d("provideCache");
        if (mCache == null)
            mCache = ModuleUtil.getAppComponent(this)
                    .cacheFactory().build(CacheType.ACTIVITY_CACHE);
        return mCache;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        Timber.d("provideLifecycleSubject");
        return mLifecycleSubject;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null)
            getLifecycle().removeObserver(mPresenter);
    }
}
