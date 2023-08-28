package vn.vnpay.demo3.bean.request;

import lombok.Getter;

@Getter
public class OIDCTokenRequest extends TokenRequest {
    private String userId;
    private String username;
    private String password;
}
