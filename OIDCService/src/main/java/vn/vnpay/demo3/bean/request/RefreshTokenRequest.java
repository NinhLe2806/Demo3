package vn.vnpay.demo3.bean.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequest extends TokenRequest {
    private String userId;
}
