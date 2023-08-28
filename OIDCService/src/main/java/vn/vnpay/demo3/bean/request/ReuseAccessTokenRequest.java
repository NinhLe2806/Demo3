package vn.vnpay.demo3.bean.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReuseAccessTokenRequest extends TokenRequest{
    private String username;
}
