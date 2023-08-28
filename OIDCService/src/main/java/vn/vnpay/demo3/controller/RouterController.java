package vn.vnpay.demo3.controller;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.HttpResponseContext;
import vn.vnpay.demo3.config.AppUriConfig;
import vn.vnpay.demo3.handler.AuthenticationHandler;
import vn.vnpay.demo3.handler.LoggedInUserHandler;
import vn.vnpay.demo3.handler.RefreshTokenAPIHandler;
import vn.vnpay.demo3.handler.VerifyTokenHandler;
import vn.vnpay.demo3.util.ResponseCreator;

import java.util.Objects;

@ChannelHandler.Sharable
@Slf4j
public class RouterController extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static RouterController instance;

    public static RouterController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RouterController();
        }
        return instance;
    }

    private final AuthenticationHandler authenticationHandler = AuthenticationHandler.getInstance();
    private final AppUriConfig appUriConfig = AppUriConfig.getInstance();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!HttpMethod.POST.equals(request.method())) {
            log.info("HttpMethod must be POST!!!");
            Response response = new Response();
            response.setCode(HttpResponseContext.API_INVALID.getCode());
            response.setMessage(HttpResponseContext.API_INVALID.getMessage());

            ResponseCreator responseCreator = ResponseCreator.getInstance();
            responseCreator.createResponse(ctx, response);
            log.info("Response to invalid HttpMethod finish");
            return;
        }
        String uri = request.uri();
        if (appUriConfig.getUris().getAuthenticationUri().equals(uri)) {
            authenticationHandler.handleAuthentication(ctx, request);
        } else if (appUriConfig.getUris().getVerifyTokenUri().equals(uri)) {
            log.info("VerifyToken is requested, start API to verify...");
            VerifyTokenHandler.getInstance().handleVerifyAccessToken(ctx, request);
        } else if (appUriConfig.getUris().getRefreshTokenUri().equals(uri)) {
            log.info("RefreshToken is requested, start API to generate...");
            RefreshTokenAPIHandler.getInstance().reGrantAccessToken(ctx, request);
        } else if (appUriConfig.getUris().getGetAccountInfo().equals(uri)) {
            log.info("AccountInfo is requested, start API to response...");
            AccountController.getInstance().getAccountInfo(ctx, request);
        } else if (appUriConfig.getUris().getReuseAccessToken().equals(uri)) {
            log.info("Reuse accessToken for user logged-in is requested, start API to response...");
            LoggedInUserHandler.getInstance().handleReuseAccessToken(ctx, request);
        }
    }
}

