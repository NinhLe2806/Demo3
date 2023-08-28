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
public class FeeCommand {
    private String id;
    private String commandCode;
    private int totalRecord;
    //TODO: CHUYỂN VỀ BIGDECIMAL
    private double totalFee;
    private String createdUser;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    //TODO: CHUYỂN VỀ TIMESTAMP(CHECK)
    private LocalDateTime createdDate;
}
