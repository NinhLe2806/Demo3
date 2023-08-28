package vn.vnpay.demo3.handler;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.TokenResourceServer;
import vn.vnpay.demo3.bean.request.VerifyTokenRequest;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.GsonCommon;
import vn.vnpay.demo3.service.OIDCTokenService;
import vn.vnpay.demo3.util.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@ChannelHandler.Sharable
@Slf4j
public class VerifyTokenHandler {

    private static VerifyTokenHandler instance;

    public static VerifyTokenHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new VerifyTokenHandler();
        }
        return instance;
    }

    private final TokenResourceServer tokenConfig = TokenResourceServer.getInstance();
    private final Gson gson = GsonCommon.getInstance();


    public void handleVerifyAccessToken(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Begin handleVerifyAccessToken...");
        String requestBody = request.content().toString(StandardCharsets.UTF_8);

        VerifyTokenRequest verifyTokenRequest = gson.fromJson(requestBody, VerifyTokenRequest.class);
        log.info("Start verify access token for clientId:{}", verifyTokenRequest.getClientId());

        Response response = OIDCTokenService
                .getInstance()
                .validateAccessToken(verifyTokenRequest.getAccessToken(), tokenConfig.getSecretKey());
        log.info("Done with validateAccessToken in handleVerifyAccessToken");
        ResponseCreator.getInstance().createResponse(ctx, response);
    }
}
