package vn.vnpay.demo2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyTokenRequest {
    private String accessToken;
    private String clientId;
    private String clientSecret;
}
