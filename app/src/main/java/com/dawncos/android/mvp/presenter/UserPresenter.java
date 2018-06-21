package com.dawncos.android.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.RecyclerView;

import com.dawncos.android.mvp.contract.UserContract;
import com.dawncos.android.mvp.model.entity.User;
import com.dawncos.glutinousrice.base.dagger2.scope.ActivityScope;
import com.dawncos.glutinousrice.base.mvp.BasePresenter;
import com.dawncos.glutinousrice.utils.Rx.RxLifecycleUtil;
import com.dawncos.glutinousrice.utils.Rx.RxPermissionsUtil;
import com.dawncos.glutinousrice.utils.android.ActivityHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class UserPresenter extends BasePresenter<UserContract.Model, UserContract.View> {
    @Inject
    ActivityHelper mActivityHelper;
    @Inject
    Application mApplication;
    @Inject
    List<User> mUsers;
    @Inject
    RecyclerView.Adapter mAdapter;
    private int lastUserId = 1;
    private boolean isFirst = true;
    private int preEndIndex;


    @Inject
    public UserPresenter(UserContract.Model model, UserContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 使用 2017 Google IO 发布的 Architecture Components 中的 Lifecycles 的新特性 (此特性已被加入 Support library)
     * 使 {@code Presenter} 可以与 {@link SupportActivity} 和 {@link Fragment} 的部分生命周期绑定
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        Timber.d("onCreate");
        requestUsers(true);//打开 App 时自动加载列表
    }

    @SuppressLint("CheckResult")
    public void requestUsers(final boolean pullToRefresh) {
        Timber.d("requestUsers");
        //请求外部存储权限用于适配android6.0的权限管理机制
        RxPermissionsUtil.externalStorage(new RxPermissionsUtil.PermissionListener(){

            @Override
            public void onPermissionApplied() {

            }

            @Override
            public void onPermissionApplySuccess() {

            }

            @Override
            public void onPermissionApplyFailure(List<String> permissions) {

            }

            @Override
            public void onPermissionApplyFailureAndNeverAsk(List<String> permissions) {

            }
        }, mRootView.getRxPermissions());


        if (pullToRefresh) lastUserId = 1;//下拉刷新默认只请求第一页

        //关于RxCache缓存库的使用请参考 http://www.jianshu.com/p/b58ef6b0624b

        boolean isEvictCache = pullToRefresh;//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存

        if (pullToRefresh && isFirst) {//默认在第一次下拉刷新时使用缓存
            isFirst = false;
            isEvictCache = false;
        }



        mModel.getUsers(lastUserId, isEvictCache)
                .subscribeOn(Schedulers.io())//subscribeOn() 用来确定数据发射所在的线程，位置放在哪里都可以，但它是只能调用一次的。
                .doOnSubscribe(disposable -> {
                    if (pullToRefresh)
                        mRootView.showLoading();//显示下拉刷新的进度条
                    else
                        mRootView.startLoadMore();//显示上拉加载更多的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())//可以调用多次来切换线程，observeOn 决定他下面的方法执行时所在的线程。
                .doFinally(() -> {
                    if (pullToRefresh)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                    else
                        mRootView.endLoadMore();//隐藏上拉加载更多的进度条
                })
                .compose(RxLifecycleUtil.bindToLifecycle(mRootView))//解决RxJava内存泄漏,添加compose(this.<Long>bindToLifecycle()) 当Activity结束掉以后，Observable停止发送数据，订阅关系解除。
                .subscribe((List<User> users) -> {
                    lastUserId = users.get(users.size() - 1).getId();//记录最后一个id,用于下一次请求
                    if (pullToRefresh) mUsers.clear();//如果是下拉刷新则清空列表
                    preEndIndex = mUsers.size();//更新之前列表总长度,用于确定加载更多的起始位置
                    mUsers.addAll(users);
                    if (pullToRefresh)
                        mAdapter.notifyDataSetChanged();
                    else
                        mAdapter.notifyItemRangeInserted(preEndIndex, users.size());
                });
    }


    @Override
    public void onDestroy(LifecycleOwner owner) {
        this.mAdapter = null;
        this.mUsers = null;
        this.mActivityHelper = null;
        this.mApplication = null;
    }
}
