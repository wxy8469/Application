package com.dawncos.glutinousrice.utils.log.timber;

import android.os.Build;
import android.util.Log;

import timber.log.Timber;

/**
 * -----------------------------------------------
 * 打印日志DebugLogTree--工具类
 * Created by Wang xiaoyu
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class DebugLogTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (Build.MANUFACTURER.equals("HUAWEI") || Build.MANUFACTURER.equals("samsung")) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
                priority = Log.ERROR;
        }
        super.log(priority, tag, message, t);
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }
}