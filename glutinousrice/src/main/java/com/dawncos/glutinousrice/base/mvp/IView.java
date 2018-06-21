package com.dawncos.glutinousrice.base.mvp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * -----------------------------------------------
 * 框架要求框架中的每个 View 都需要实现此类,以满足规范
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IView {

    /**
     * 显示加载
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 {@code null}
     */
    void showMessage(@NonNull String message);

    /**
     * 跳转 {@link Activity}
     *
     * @param intent {@code intent} 不能为 {@code null}
     */
    void launchActivity(@NonNull Intent intent);

    /**
     * 杀死自己
     */
    void killMyself();
}
