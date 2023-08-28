package vn.vnpay.demo2.bean;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Response {
    private String code;
    private String message;
}
