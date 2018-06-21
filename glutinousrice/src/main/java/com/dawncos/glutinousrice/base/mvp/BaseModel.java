package com.dawncos.glutinousrice.base.mvp;

import android.arch.lifecycle.LifecycleOwner;

import com.dawncos.glutinousrice.base.android.repository.IRepositoryManager;

/**
 * -----------------------------------------------
 * Model基类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class BaseModel implements IModel {
    protected IRepositoryManager mRepositoryManager;//用于管理网络请求层, 以及数据缓存层

    public BaseModel(IRepositoryManager repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        mRepositoryManager = null;
        owner.getLifecycle().removeObserver(this);
    }

}
