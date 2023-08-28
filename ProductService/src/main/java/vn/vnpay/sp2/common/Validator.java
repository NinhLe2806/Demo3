package vn.vnpay.sp2.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.sp2.bean.request.VerifyTokenRequest;
import vn.vnpay.sp2.handler.ReceiveTokenHandler;
import vn.vnpay.sp2.util.ExternalApiSenderUtil;

import static vn.vnpay.sp2.common.Constant.CLIENT_ID;
import static vn.vnpay.sp2.common.Constant.CLIENT_SECRET;

@Slf4j
public class Validator {
    public static boolean isRequestNull(String requestBody) {
        if (StringUtils.isBlank(requestBody) || requestBody.trim().replaceAll("\\s", "").equals("{}")) {
            return true;
        }
        return false;
    }

    public static boolean isValidUser(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Begin isValidUser...");
        if (null != request) {
            String accessToken = ReceiveTokenHandler.getAccessToken(request);
            if (!StringUtils.isBlank(accessToken)) {
                VerifyTokenRequest verifyTokenRequest = VerifyTokenRequest
                        .builder()
                        .accessToken(accessToken)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .build();
                ExternalApiSenderUtil.getInstance().verifyAccessToken(ctx, verifyTokenRequest);
                return true;
            }
            log.info("AccessToken is invalid");
            return false;
        }
        log.info("Request is null in isValidUser");
        return false;
    }
}
