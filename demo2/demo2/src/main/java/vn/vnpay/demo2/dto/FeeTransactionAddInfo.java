package vn.vnpay.demo2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vnpay.demo2.entity.Status;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeeTransactionAddInfo {
    private String transactionCode;
    private String commandCode;
    private double feeAmount;
    private Status status;
    private String accountNumber;
    private int totalScan;
    private String remark;
}
