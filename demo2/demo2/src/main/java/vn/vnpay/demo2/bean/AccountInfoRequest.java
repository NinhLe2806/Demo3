package vn.vnpay.demo2.bean;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoRequest {
    private String userId;
}
