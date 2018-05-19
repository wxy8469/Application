package com.dawncos.cmodule.utils.android;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.dawncos.cmodule.base.android.IConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 用于解析 AndroidManifest 中的 Meta 属性
 * 配合 {@link IConfig} 使用
 * <p>
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * ================================================
 */
public final class ManifestParser {
    private static final String MODULE_VALUE = "IConfig";

    private final Context context;

    public ManifestParser(Context context) {
        this.context = context;
    }

    public List<IConfig> parse() {
        List<IConfig> iConfigs = new ArrayList<IConfig>();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                for (String key : appInfo.metaData.keySet()) {
                    if (MODULE_VALUE.equals(appInfo.metaData.get(key))) {
                        iConfigs.add(parseIConfig(key));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unable to find metadata to parse IConfig", e);
        }

        return iConfigs;
    }

    private static IConfig parseIConfig(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to find IConfig implementation", e);
        }

        Object config;
        try {
            config = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to instantiate IConfig implementation for " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate IConfig implementation for " + clazz, e);
        }

        if (!(config instanceof IConfig)) {
            throw new RuntimeException("Expected instanceof IConfig, but found: " + config);
        }
        return (IConfig) config;
    }
}