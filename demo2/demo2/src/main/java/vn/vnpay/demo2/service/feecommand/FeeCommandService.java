package vn.vnpay.demo2.service.feecommand;

import vn.vnpay.demo2.dto.FeeCommandAddInfo;
import vn.vnpay.demo2.entity.FeeCommand;
import vn.vnpay.demo2.common.HttpResponse;

public interface FeeCommandService {
    HttpResponse addFeeCommand(FeeCommandAddInfo feeCommandAddInfo);
}
