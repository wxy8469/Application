package com.dawncos.glutinousrice.http.imageloader;

import android.widget.ImageView;

/**
 * -----------------------------------------------
 * 图片加载配置基础信息类
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public class ImageConfig {
    protected String url;
    protected ImageView imageView;
    protected int placeholder;//占位符
    protected int errorPic;//错误占位符

    public String getUrl() {
        return url;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public int getErrorPic() {
        return errorPic;
    }
}
