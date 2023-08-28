package vn.vnpay.demo3.handler;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.OIDCToken;
import vn.vnpay.demo3.bean.request.OIDCTokenRequest;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.GsonCommon;
import vn.vnpay.demo3.service.OIDCTokenService;
import vn.vnpay.demo3.util.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static vn.vnpay.demo3.common.HttpResponseContext.SUCCESS;

@Slf4j
@ChannelHandler.Sharable
public class AuthenticationHandler {
    private static AuthenticationHandler instance;

    public static AuthenticationHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AuthenticationHandler();
        }
        return instance;
    }

    private final ResponseCreator responseCreator = ResponseCreator.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    public void handleAuthentication(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            log.info("Begin handleAuthentication...");
            String requestBody = fullHttpRequest.content().toString(StandardCharsets.UTF_8);
            OIDCTokenRequest oidcTokenRequest = gson.fromJson(requestBody, OIDCTokenRequest.class);
            log.info("OIDCTokenRequest:{}", oidcTokenRequest.getClientId());

            // Create token and response
            log.info("Start generating accessToken and idToken in handleAuthentication");
            String accessToken = OIDCTokenService.getInstance().generateOAuthToken(oidcTokenRequest);
            String idToken = OIDCTokenService.getInstance().generateIdToken(oidcTokenRequest);
            log.info("Successful generated accessToken and idToken in handleAuthentication");

            OIDCToken oidcToken = OIDCToken.builder().idToken(idToken).accessToken(accessToken).build();
            log.info("Got idToken:{} & accessToken:{}", oidcToken.getIdToken(), oidcToken.getAccessToken());
            Response response = new Response(SUCCESS.getCode(), SUCCESS.getMessage(), oidcToken);

            responseCreator.createResponse(ctx, response);
            log.info("End handleAuthentication.");
        } catch (Exception e) {
            log.info("Exception in handleAuthentication:{} ", e.getMessage());
            log.error("Exception in handleAuthentication: ", e);
        }
    }
}
