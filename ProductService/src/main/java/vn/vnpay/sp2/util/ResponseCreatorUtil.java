package vn.vnpay.sp2.util;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import vn.vnpay.lib.bean.response.BaseResponse;
import vn.vnpay.sp2.common.GsonCommon;

import java.util.Objects;

public class ResponseCreatorUtil {
    private static ResponseCreatorUtil instance;

    public static ResponseCreatorUtil getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ResponseCreatorUtil();
        }
        return instance;
    }
    private final Gson gson = GsonCommon.getInstance();
    public void createResponse(ChannelHandlerContext context, BaseResponse responseResult) {
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
}
