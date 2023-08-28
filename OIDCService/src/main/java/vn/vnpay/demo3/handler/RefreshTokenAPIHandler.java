package vn.vnpay.demo3.handler;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.request.RefreshTokenRequest;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.GsonCommon;
import vn.vnpay.demo3.service.OIDCTokenService;
import vn.vnpay.demo3.util.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class RefreshTokenAPIHandler {
    private static RefreshTokenAPIHandler instance;

    public static RefreshTokenAPIHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RefreshTokenAPIHandler();
        }
        return instance;
    }

    private final ResponseCreator responseCreator = ResponseCreator.getInstance();
    private final Gson gson = GsonCommon.getInstance();


    public void reGrantAccessToken(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            log.info("Begin responseRefreshToken...");
            String requestBody = fullHttpRequest.content().toString(StandardCharsets.UTF_8);
            RefreshTokenRequest refreshTokenRequest = gson.fromJson(requestBody, RefreshTokenRequest.class);

            // Create token and response
            log.info("Start generating accessToken and refreshToken in responseRefreshToken");
            Response response = OIDCTokenService.getInstance().reGrantAccessToken(refreshTokenRequest);
            log.info("Successful generated accessToken and refreshToken in responseRefreshToken");
            responseCreator.createResponse(ctx, response);
        } catch (Exception e) {
            log.info("Exception in handleAuthentication:{} ", e.getMessage());
            log.error("Exception in handleAuthentication: ", e);
        }
    }
}
