package vn.vnpay.demo3.bean.request;

import lombok.Getter;

@Getter
public class VerifyTokenRequest extends TokenRequest {
    private String accessToken;
}
