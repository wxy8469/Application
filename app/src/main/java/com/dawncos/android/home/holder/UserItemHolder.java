package com.dawncos.android.home.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dawncos.glutinousrice.http.imageloader.glide.GlideImageConfig;
import com.dawncos.android.R;
import com.dawncos.android.mvp.model.entity.User;
import com.dawncos.glutinousrice.base.android.adapter.BaseHolder;
import com.dawncos.glutinousrice.base.dagger2.component.AppComponent;
import com.dawncos.glutinousrice.http.imageloader.ImageLoader;
import com.dawncos.glutinousrice.utils.android.ModuleUtil;

import butterknife.BindView;
import io.reactivex.Observable;

/**
 * ================================================
 * 展示 {@link BaseHolder} 的用法
 * <p>
 * Created by JessYan on 9/4/16 12:56
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class UserItemHolder extends BaseHolder<User> {

    @BindView(R.id.iv_avatar)
    ImageView mAvatar;
    @BindView(R.id.tv_name)
    TextView mName;
    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用 Glide,使用策略模式,可替换框架

    public UserItemHolder(View itemView) {
        super(itemView);
        //可以在任何可以拿到 Context 的地方,拿到 AppComponent,从而得到用 Dagger 管理的单例对象
        mAppComponent = ModuleUtil.getAppComponent(itemView.getContext());
        mImageLoader = mAppComponent.imageLoader();
    }

    @Override
    public void setData(User data, int position) {
        Observable.just(data.getLogin())
                .subscribe(s -> mName.setText(s));

        //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
        mImageLoader.loadImage(itemView.getContext(),
                GlideImageConfig
                        .builder()
                        .url(data.getAvatarUrl())
                        .imageView(mAvatar)
                        .build());
    }


    @Override
    protected void onRelease() {
        mImageLoader.clear(mAppComponent.application(), GlideImageConfig.builder()
                .imageViews(mAvatar)
                .build());
    }
}
