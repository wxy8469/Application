package com.dawncos.glutinousrice.base.android;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.trello.rxlifecycle2.RxLifecycle;

import io.reactivex.subjects.Subject;

/**
 * -----------------------------------------------
 * java单继承，不想继承 {@link RxLifecycle} 提供的 RxActivity,RxFragmentActivity等
 * 让 {@link Activity}/{@link Fragment} 实现此接口，
 * 自己实现 {@link RxLifecycle}（参考源码，目的是解决rxjava内存泄露问题）的功能。
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface IRxLifecycle<E> {
    @NonNull
    Subject<E> provideLifecycleSubject();
}
