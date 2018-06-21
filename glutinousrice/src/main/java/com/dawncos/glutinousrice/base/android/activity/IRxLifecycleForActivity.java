package com.dawncos.glutinousrice.base.android.activity;

import android.app.Activity;

import com.dawncos.glutinousrice.base.android.IRxLifecycle;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * -----------------------------------------------
 * 让 {@link Activity} 实现此接口,即可正常使用 {@link RxLifecycle}
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IRxLifecycleForActivity extends IRxLifecycle<ActivityEvent> {

}
