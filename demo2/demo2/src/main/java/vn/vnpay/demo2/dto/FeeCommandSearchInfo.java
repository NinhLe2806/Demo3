package vn.vnpay.demo2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeCommandSearchInfo {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private int totalRecord;
    private int totalFee;
    private String createdUser;
    private String createDate;

}
