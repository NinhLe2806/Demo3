package vn.vnpay.demo2.utils;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import vn.vnpay.demo2.common.HttpResponse;

import java.util.Objects;

public class ResponseCreator {
    private static ResponseCreator instance;

    public static ResponseCreator getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ResponseCreator();
        }
        return instance;
    }
    private final Gson gson = new Gson();
    public void createResponse(ChannelHandlerContext context, HttpResponse responseResult) {
        String result = gson.toJson(responseResult);
        FullHttpResponse httpResponse = createJsonResponse(result);
        context.writeAndFlush(httpResponse);
    }

    private FullHttpResponse createJsonResponse(String result) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.content().writeBytes(result.getBytes());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    public FullHttpResponse createTextResponse(String result) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.content().writeBytes(result.getBytes());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
