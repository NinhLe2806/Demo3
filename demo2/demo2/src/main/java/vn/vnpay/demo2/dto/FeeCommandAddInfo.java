package vn.vnpay.demo2.dto;

import com.sun.istack.internal.NotNull;
import lombok.*;
import vn.vnpay.demo2.common.RequestInfo;

@Getter
@Setter
public class FeeCommandAddInfo extends RequestInfo {
    @NotNull
    private String commandCode;
    @NotNull
    private int totalRecord;
    @NotNull
    private double totalFee;
    @NotNull
    private String createdUser;
    @NotNull
    private String createdDate;
}
