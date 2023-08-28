package vn.vnpay.demo3.handler;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.demo3.bean.request.ReuseAccessTokenRequest;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.GsonCommon;
import vn.vnpay.demo3.service.OIDCTokenService;
import vn.vnpay.demo3.util.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class LoggedInUserHandler {
    private static LoggedInUserHandler instance;

    public static LoggedInUserHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new LoggedInUserHandler();
        }
        return instance;
    }

    private final ResponseCreator responseCreator = ResponseCreator.getInstance();
    private final Gson gson = GsonCommon.getInstance();
    private final OIDCTokenService oidcTokenService = OIDCTokenService.getInstance();

    public void handleReuseAccessToken(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        log.info("Begin handleAuthentication...");
        try {
            String requestBody = fullHttpRequest.content().toString(StandardCharsets.UTF_8);
            ReuseAccessTokenRequest reuseAccessTokenRequest = gson.fromJson(requestBody, ReuseAccessTokenRequest.class);
            log.info("Get username:{} from requestBody in handleReuseAccessToken", reuseAccessTokenRequest.getUsername());

            log.info("Start getting reuse accessToken");
            Response response = oidcTokenService.grantAccessTokenForLoggedInUser(reuseAccessTokenRequest);
            log.info("End of getting reuse accessToken");

            responseCreator.createResponse(ctx, response);
            log.info("End handleAuthentication!!!");
        } catch (Exception e) {
            log.info("Exception in handleGrantAccessToken:{} ", e.getMessage());
            log.error("Exception in handleGrantAccessToken: ", e);
        }
    }
}
