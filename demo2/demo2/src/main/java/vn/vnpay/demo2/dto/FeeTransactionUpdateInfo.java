package vn.vnpay.demo2.dto;

import lombok.Getter;
import vn.vnpay.demo2.common.RequestInfo;


@Getter
public class FeeTransactionUpdateInfo extends RequestInfo {
    private String commandCode;
}
