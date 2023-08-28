package vn.vnpay.demo2.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpUtils {
    public static void sendRedirectResponse(ChannelHandlerContext ctx, String redirectURL) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.FOUND
        );
        response.headers().set(HttpHeaderNames.LOCATION, redirectURL);
        ctx.writeAndFlush(response);
    }

    public static void sendNotFoundResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND,
                Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8)
        );
        ctx.writeAndFlush(response);
    }

}
