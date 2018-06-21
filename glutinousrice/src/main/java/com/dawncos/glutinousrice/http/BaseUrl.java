package com.dawncos.glutinousrice.http;

import okhttp3.HttpUrl;

/**
 * -----------------------------------------------
 * if 在 IAppComponent 启动时不能确定Url,需要请求服务器接口动态获取
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface BaseUrl {
    /**
     * 在调用Retrofit API接口之前,可以使用Okhttp或其他方式,请求到正确的BaseUrl
     * 并通过此方法返回
     * @return
     */
    HttpUrl url();
}
