package vn.vnpay.sp2.controller;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.lib.bean.response.BaseResponse;
import vn.vnpay.sp2.common.Validator;
import vn.vnpay.sp2.config.AppUriConfig;
import vn.vnpay.sp2.util.ResponseCreatorUtil;

import java.util.Objects;

import static vn.vnpay.sp2.common.HttpResponseContextEnum.API_INVALID;
import static vn.vnpay.sp2.common.HttpResponseContextEnum.NOT_YET_LOGIN;

@Slf4j
@ChannelHandler.Sharable
public class RouterController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static RouterController instance;

    public static RouterController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RouterController();
        }
        return instance;
    }

    private final AppUriConfig appUriConfig = AppUriConfig.getInstance();
    private final ProductController productController = ProductController.getInstance();
    private final ResponseCreatorUtil responseCreatorUtil = ResponseCreatorUtil.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!HttpMethod.POST.equals(request.method())) {
            log.info("HttpMethod must be POST!!!");
            BaseResponse response = new BaseResponse();
            response.setCode(API_INVALID.getCode());
            response.setMessage(API_INVALID.getMessage());

            ResponseCreatorUtil responseCreator = ResponseCreatorUtil.getInstance();
            responseCreator.createResponse(ctx, response);
            log.info("Response invalid HttpMethod");
            return;
        }
        String uri = request.uri();
        if (Validator.isValidUser(ctx, request)) {
            if (appUriConfig.getUris().getAddProductUri().equals(uri)) {
                log.info("Start request to addProduct API");
                responseCreatorUtil.createResponse(ctx, productController.addProductRequest(request));
            }
        } else {
            BaseResponse baseResponse = BaseResponse
                    .builder()
                    .code(NOT_YET_LOGIN.getCode())
                    .message(NOT_YET_LOGIN.getMessage())
                    .build();
            responseCreatorUtil.createResponse(ctx, baseResponse);
        }
    }
}
