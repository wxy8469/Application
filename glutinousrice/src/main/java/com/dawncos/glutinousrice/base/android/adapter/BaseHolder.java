package com.dawncos.glutinousrice.base.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dawncos.glutinousrice.utils.android.ThirdViewUtil;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * -----------------------------------------------
 * 基类 {@link RecyclerView.ViewHolder}
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected OnViewClickListener mOnViewClickListener = null;
    protected final String TAG = this.getClass().getSimpleName();

    public BaseHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);//点击事件
        if (ThirdViewUtil.USE_AUTOLAYOUT == 1) AutoUtils.autoSize(itemView);//适配
        ThirdViewUtil.bindTarget(this, itemView);//绑定
    }

    public abstract void setData(T data, int position);

    protected void onRelease() {

    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }
}
