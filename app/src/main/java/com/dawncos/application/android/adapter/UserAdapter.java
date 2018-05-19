package com.dawncos.application.android.adapter;

import android.view.View;

import com.dawncos.application.R;
import com.dawncos.application.android.holder.UserItemHolder;
import com.dawncos.application.mvp.model.entity.User;
import com.dawncos.cmodule.base.android.adapter.BaseHolder;
import com.dawncos.cmodule.base.android.adapter.DefaultAdapter;

import java.util.List;


/**
 * ================================================
 * 展示 {@link DefaultAdapter} 的用法
 * <p>
 * Created by JessYan on 09/04/2016 12:57
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class UserAdapter extends DefaultAdapter<User> {
    public UserAdapter(List<User> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<User> getHolder(View v, int viewType) {
        return new UserItemHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.recycle_list;
    }
}
