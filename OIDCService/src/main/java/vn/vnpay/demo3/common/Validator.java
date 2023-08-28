package vn.vnpay.demo3.common;


import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.TokenClient;
import vn.vnpay.demo3.bean.request.TokenRequest;

import java.util.Objects;

@Slf4j
public class Validator {
    public static boolean isValidRequestToken(TokenRequest request, TokenClient tokenClient) {
        log.info("Begin isValidRequestToken...");
        if (Objects.isNull(tokenClient)) {
            log.info("Invalid client id: {}", request.getClientId());
            return false;
        }
        if (!request.getClientId().equals(tokenClient.getClientId())) {
            log.info("ClientId is invalid:{}", request.getClientId());
            return false;
        }
        if (!request.getClientSecret().equals(tokenClient.getClientSecret())) {
            log.info("Client Secret is invalid:{}", request.getClientSecret());
            return false;
        }
        log.info("Request token is valid");
        return true;
    }
}
