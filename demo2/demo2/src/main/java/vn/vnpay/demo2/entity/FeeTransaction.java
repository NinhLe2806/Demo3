package vn.vnpay.demo2.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeeTransaction {
    private String id;
    private String transactionCode;
    private String commandCode;
    private double feeAmount;
    private Status status;
    private String accountNumber;
    private int totalScan;
    private String remark;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime modifiedDate;
}
