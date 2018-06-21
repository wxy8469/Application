package com.dawncos.glutinousrice.http.imageloader.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

/**
 * -----------------------------------------------
 * 配置 @{@link Glide} 自定义参数
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */

public interface GlideAppliesOptions {

    void applyGlideOptions(Context context, GlideBuilder builder);
}
