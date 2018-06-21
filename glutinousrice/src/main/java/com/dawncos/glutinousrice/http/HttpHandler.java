package com.dawncos.glutinousrice.http;

import com.dawncos.glutinousrice.base.dagger2.module.GlutinousRiceBuilder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * -----------------------------------------------
 * 处理Http请求和响应结果
 * 使用 {@link GlutinousRiceBuilder.Builder#globalHttpHandler(HttpHandler)} 方法配置
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface HttpHandler {

    Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response);

    Request onHttpRequestBefore(Interceptor.Chain chain, Request request);

    HttpHandler EMPTY = new HttpHandler() {
        @Override
        public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
            return response;
        }

        @Override
        public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
            return request;
        }
    };

}
