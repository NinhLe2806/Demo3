package vn.vnpay.demo2.dto;

import lombok.Getter;

@Getter
public class FeeCommandUpdateInfo {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private int totalRecord;
    private double totalFee;
    private String createdUser;
    private String createdDate;

}
