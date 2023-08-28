package vn.vnpay.demo2.controller;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.common.Constant;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.handler.ReceiveTokenHandler;
import vn.vnpay.demo2.handler.WelcomeHandler;
import vn.vnpay.demo2.utils.HttpUtils;
import vn.vnpay.demo2.utils.ResponseCreator;
import vn.vnpay.demo2.utils.Validator;

import static vn.vnpay.demo2.dto.HttpResponseContext.NOT_YET_LOGIN;

@Slf4j
public class RouterController extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final FeeTransactionController feeTransactionController;
    private final FeeCommandController feeCommandController;

    public RouterController(
            FeeTransactionController feeTransactionController,
            FeeCommandController feeCommandController) {
        this.feeTransactionController = feeTransactionController;
        this.feeCommandController = feeCommandController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // Lấy URI của yêu cầu
        String uri = request.uri();

        // Xử lý định tuyến dựa trên URI
        if (Validator.isValidUser(ctx, request)) {
            if (uri.startsWith("/fee-transaction")) {
                // Định tuyến yêu cầu tới FeeTransactionController
                feeTransactionController.channelRead0(ctx, request);
            } else if (uri.startsWith("/fee-command")) {
                // Định tuyến yêu cầu tới FeeCommandController
                feeCommandController.channelRead0(ctx, request);
            } else if (uri.startsWith("/login")) {
                // Chuyển tiếp đến localhost:8008/demo3/authorization
                HttpUtils.sendRedirectResponse(ctx, Constant.REDIRECT_AUTHENTICATION_URL);
            } else if (uri.startsWith("/auth0")) {
                ReceiveTokenHandler.getAccessToken(request);
            } else if (uri.startsWith("/welcome")) {
                WelcomeHandler.getInstance().createEntryResponse(ctx, request);
            } else {
                log.info("Can not found redirect url");
                HttpUtils.sendNotFoundResponse(ctx);
            }
        } else {
            HttpResponse response = HttpResponse
                    .builder()
                    .code(NOT_YET_LOGIN.getCode())
                    .message(NOT_YET_LOGIN.getMessage())
                    .build();
            ResponseCreator.getInstance().createResponse(ctx, response);
            HttpUtils.sendRedirectResponse(ctx, Constant.REDIRECT_AUTHENTICATION_URL);
        }
    }
}
