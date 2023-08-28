package vn.vnpay.demo3.bean;

import lombok.Getter;

@Getter
public class TokenClient {
    private Long expireTime;
    private Long refreshExpireTime;
    private String clientId;
    private String clientSecret;
}
