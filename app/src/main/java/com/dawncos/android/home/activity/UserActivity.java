package com.dawncos.android.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.dawncos.android.R;
import com.dawncos.android.di.component.DaggerUserComponent;
import com.dawncos.android.di.module.UserModule;
import com.dawncos.android.mvp.contract.UserContract;
import com.dawncos.android.mvp.presenter.UserPresenter;
import com.dawncos.glutinousrice.base.android.activity.BaseActivity;
import com.dawncos.glutinousrice.base.android.adapter.DefaultAdapter;
import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;
import com.dawncos.glutinousrice.utils.android.ModuleUtil;
import com.paginate.Paginate;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

public class UserActivity extends BaseActivity<UserPresenter>
        implements UserContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject
    RxPermissions mRxPermissions;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.Adapter mAdapter;

    private Paginate mPaginate;
    private boolean isLoadingMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("-------onCreate");
        super.onCreate(savedInstanceState);
    }//测试 BehaviorSubject

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        Timber.d("setupActivityComponent");
        DaggerUserComponent
                .builder()
                .appComponent(appComponent)
                .userModule(new UserModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        Timber.d("initView");
        return R.layout.activity_user;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Timber.d("initData");
        initRecyclerView();
        mRecyclerView.setAdapter(mAdapter);
        initPaginate();
    }


    @Override
    public void onRefresh() {
        Timber.d("onRefresh");
        mPresenter.requestUsers(true);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Timber.d("initRecyclerView");
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ModuleUtil.configRecyclerView(mRecyclerView, mLayoutManager);
    }


    @Override
    public void showLoading() {
        Timber.d("showLoading");
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        Timber.d("hideLoading");
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        Timber.d("showMessage");
        ModuleUtil.snackbarText(message);
    }

    @Override
    public void launchActivity(Intent intent) {
        ModuleUtil.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    /**
     * 开始加载更多
     */
    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    /**
     * 结束加载更多
     */
    @Override
    public void endLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    /**
     * 初始化Paginate,用于加载更多
     */
    private void initPaginate() {
        Timber.d("initPaginate");
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    mPresenter.requestUsers(false);
                }

                @Override
                public boolean isLoading() {
                    return isLoadingMore;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            };

            mPaginate = Paginate.with(mRecyclerView, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }

    @Override
    protected void onDestroy() {
        DefaultAdapter.releaseAllHolder(mRecyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mRxPermissions = null;
        this.mPaginate = null;
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
