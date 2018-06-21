package com.dawncos.android.home.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dawncos.android.R;
import com.dawncos.android.databinding.ActivityWelcomeBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class WelcomeActivity extends AppCompatActivity {

    public ActivityWelcomeBinding mBinding;
    private Disposable subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        long count = 3;

        subscribe = Completable
                .fromCallable(() -> Glide
                        .with(this)
                        .load("http://ojyz0c8un.bkt.clouddn.com/b_9.jpg")
//                        .placeholder(R.drawable.img_default_welcome)
//                        .error(R.drawable.img_default_welcome)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mBinding.ivPic))
                .subscribe(() -> Observable
                        .intervalRange(1, count, 0, 1, TimeUnit.SECONDS)
                        .map((Long start) -> count - start) // 将 0,1,2 转成 2,1,0.
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> launchActivity())
                        .subscribe((Long number) -> {
                            mBinding.tvJump.setVisibility(View.VISIBLE);
                            mBinding.tvJump.setText(String.valueOf(number) + "s跳过");
                        }));

    }

    private void launchActivity() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.translate_left_to_center, R.anim.translate_right_to_center);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscribe.dispose();
    }
}
