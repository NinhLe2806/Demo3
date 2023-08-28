package vn.vnpay.demo2.controller;

import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.handler.FeeCommandControllerHandler;

@ChannelHandler.Sharable
@Slf4j
public class FeeCommandController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final FeeCommandControllerHandler feeCommandControllerHandler;

    @Inject
    public FeeCommandController(FeeCommandControllerHandler feeCommandControllerHandler) {
        this.feeCommandControllerHandler = feeCommandControllerHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        if (HttpMethod.POST.equals(method)) {
            if ("/fee-command/add".equals(uri)) {
                log.info("Start request to add FeeCommand");
                feeCommandControllerHandler.handleAddFeeCommand(ctx, request);
            }
        }
    }
}
