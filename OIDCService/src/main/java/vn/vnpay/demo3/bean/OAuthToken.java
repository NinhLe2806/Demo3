package vn.vnpay.demo3.bean;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OAuthToken {
    private String accessToken;
    private String refreshToken;
}
