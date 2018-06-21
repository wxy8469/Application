package com.dawncos.glutinousrice.utils.log.interceptor;

import com.dawncos.glutinousrice.base.dagger2.module.GlutinousRiceBuilder;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
/**
 * -----------------------------------------------
 * 对OkHttp的请求和响应信息进行打印, 开发者可更根据自己的需求自行扩展打印格式
 * @see FormatHttpMessage
 * @see GlutinousRiceBuilder.Builder#formatPrinter(FormatHttpMsg)
 * Created by wxy8469
 * <a href="mailto:wxy8469163@gmail.com">Contact me</a>
 * <a href="https://github.com/wxy8469/Application">Follow me</a>
 * -----------------------------------------------
 */
public interface FormatHttpMsg {

    void printJsonRequest(Request request, String bodyString);

    void printJsonResponse(long chainMs, boolean isSuccessful, int code, String headers, MediaType contentType,
                           String bodyString, List<String> segments, String message, String responseUrl);

    void printFileRequest(Request request);

    void printFileResponse(long chainMs, boolean isSuccessful, int code, String headers,
                           List<String> segments, String message, String responseUrl);
}
