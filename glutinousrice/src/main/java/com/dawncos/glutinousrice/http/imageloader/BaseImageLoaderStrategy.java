package com.dawncos.glutinousrice.http.imageloader;

import android.content.Context;

/**
 * -----------------------------------------------
 * 图片加载策略基础接口
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface BaseImageLoaderStrategy<T extends ImageConfig> {
    /**
     * 加载图片
     *
     * @param ctx
     * @param config
     */
    void loadImage(Context ctx, T config);

    /**
     * 停止加载
     *
     * @param ctx
     * @param config
     */
    void clear(Context ctx, T config);
}
