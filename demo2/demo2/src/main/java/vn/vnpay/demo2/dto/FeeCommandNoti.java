package vn.vnpay.demo2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FeeCommandNoti {
    private String commandCode;
    private String status;
}
