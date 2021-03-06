package com.dawncos.android.mvp.model;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.dawncos.android.mvp.contract.UserContract;
import com.dawncos.android.mvp.model.api.cache.CommonCache;
import com.dawncos.android.mvp.model.api.service.UserService;
import com.dawncos.android.mvp.model.entity.User;
import com.dawncos.glutinousrice.base.android.repository.IRepositoryManager;
import com.dawncos.glutinousrice.base.dagger2.scope.ActivityScope;
import com.dawncos.glutinousrice.base.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import timber.log.Timber;

/**
 * ================================================
 * 展示 Model 的用法
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.4.3">Model wiki 官方文档</a>
 * Created by JessYan on 09/04/2016 10:56
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
@ActivityScope
public class UserModel extends BaseModel implements UserContract.Model {
    public static final int USERS_PER_PAGE = 10;

    @Inject
    public UserModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public Observable<List<User>> getUsers(int lastIdQueried, boolean update) {
        Timber.d("getUsers");
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return Observable
                .just(mRepositoryManager
                        .obtainRetrofitService(UserService.class)
                        .getUsers(lastIdQueried, USERS_PER_PAGE))
                .flatMap(listObservable ->
                     mRepositoryManager
                        .obtainCacheService(CommonCache.class)
                        .getUsers(listObservable, new DynamicKey(lastIdQueried), new EvictDynamicKey(update))
                        .map(listReply ->
                            listReply.getData()));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Timber.d("Release Resource");
    }

}
