package com.dawncos.glutinousrice.utils.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.dawncos.glutinousrice.base.android.GlutinousRiceModule;

import java.util.ArrayList;
import java.util.List;

/**
 * -----------------------------------------------
 * 解析AndroidManifest中的Meta属性--工具类
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public final class ManifestParserHelper {
    private static final String MODULE_VALUE = "GLUTINOUSRICE_MODULE";

    private final Context context;

    public ManifestParserHelper(Context context) {
        this.context = context;
    }

    public List<GlutinousRiceModule> parse() {
        List<GlutinousRiceModule> glutinousRiceModules = new ArrayList<>();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                for (String key : appInfo.metaData.keySet()) {
                    if (appInfo.metaData.get(key).equals(MODULE_VALUE)) {
                        glutinousRiceModules.add(instanceForName(key));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unable to find metadata to parse MODULE_CONFIG", e);
        }

        return glutinousRiceModules;
    }

    private static GlutinousRiceModule instanceForName(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to find GlutinousRiceModule implementation", e);
        }

        Object config;
        try {
            config = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to instantiate GlutinousRiceModule implementation for " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate GlutinousRiceModule implementation for " + clazz, e);
        }

        if (!(config instanceof GlutinousRiceModule)) {
            throw new RuntimeException("Expected instanceof GlutinousRiceModule, but found: " + config);
        }
        return (GlutinousRiceModule) config;
    }
}