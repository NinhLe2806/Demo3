package vn.vnpay.demo2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.handler.FeeTransactionControllerHandler;
import vn.vnpay.demo2.service.feetransaction.FeeTransactionService;

import javax.inject.Inject;

@ChannelHandler.Sharable
@Slf4j
public class FeeTransactionController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final FeeTransactionControllerHandler feeTransactionControllerHandler;

    @Inject
    public FeeTransactionController(FeeTransactionControllerHandler feeTransactionControllerHandler) {
        this.feeTransactionControllerHandler = feeTransactionControllerHandler;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();

        if (HttpMethod.POST.equals(method)) {
            if ("/fee-transaction/update".equals(uri)) {
                log.info("Start request to update FeeTransaction");
                feeTransactionControllerHandler.handleUpdateFeeTransaction(ctx, request);
            }
        }
    }
}
