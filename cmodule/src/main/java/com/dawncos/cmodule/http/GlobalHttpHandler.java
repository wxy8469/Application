package com.dawncos.cmodule.http;

import com.dawncos.cmodule.base.dagger2.module.GlobalConfigModule;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 处理 Http 请求和响应结果的处理类
 * 使用 {@link GlobalConfigModule.Builder#globalHttpHandler(GlobalHttpHandler)} 方法配置
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#3.2">GlobalHttpHandler Wiki 官方文档</a>
 * Created by JessYan on 8/30/16 17:47
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface GlobalHttpHandler {
    Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response);

    Request onHttpRequestBefore(Interceptor.Chain chain, Request request);

    //空实现
    GlobalHttpHandler EMPTY = new GlobalHttpHandler() {
        @Override
        public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
            //不管是否处理,都必须将response返回出去
            return response;
        }

        @Override
        public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
            //不管是否处理,都必须将request返回出去
            return request;
        }
    };

}