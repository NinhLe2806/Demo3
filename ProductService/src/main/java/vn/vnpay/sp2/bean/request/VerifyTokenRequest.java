package vn.vnpay.sp2.bean.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyTokenRequest {
    private String accessToken;
    private String clientId;
    private String clientSecret;
}
