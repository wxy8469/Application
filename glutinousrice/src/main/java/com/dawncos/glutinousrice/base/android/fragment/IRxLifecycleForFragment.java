package com.dawncos.glutinousrice.base.android.fragment;

import android.support.v4.app.Fragment;

import com.dawncos.glutinousrice.base.android.IRxLifecycle;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;

/**
 * -----------------------------------------------
 * 让 {@link Fragment} 实现此接口,即可正常使用 {@link RxLifecycle}
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IRxLifecycleForFragment extends IRxLifecycle<FragmentEvent> {
}
