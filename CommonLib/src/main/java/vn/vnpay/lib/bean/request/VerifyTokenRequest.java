package vn.vnpay.lib.bean.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyTokenRequest extends TokenRequest {
    private String accessToken;
}