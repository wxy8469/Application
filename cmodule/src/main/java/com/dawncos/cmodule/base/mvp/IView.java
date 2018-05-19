package com.dawncos.cmodule.base.mvp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * ================================================
 * 框架要求框架中的每个 View 都需要实现此类,以满足规范
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#2.4.2">View wiki 官方文档</a>
 * Created by JessYan on 4/22/2016
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
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
