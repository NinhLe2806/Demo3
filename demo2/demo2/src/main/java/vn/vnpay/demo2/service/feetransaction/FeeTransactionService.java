package vn.vnpay.demo2.service.feetransaction;

import vn.vnpay.demo2.dto.FeeTransactionAddInfo;
import vn.vnpay.demo2.dto.FeeTransactionUpdateInfo;
import vn.vnpay.demo2.entity.FeeTransaction;
import vn.vnpay.demo2.common.HttpResponse;

import java.util.List;

public interface FeeTransactionService {
    void addFeeTransaction(FeeTransactionAddInfo feeTransactionAddInfo);

    void addFeeTransactions(List<FeeTransactionAddInfo> feeTransactionAddInfos);

    HttpResponse updateFeeTransaction(FeeTransactionUpdateInfo feeTransactionUpdateInfo);

    void startCronJobToUpdateTransactions();

    List<FeeTransaction> findFeeTransactionByCommandCode(String commandCode);
}
