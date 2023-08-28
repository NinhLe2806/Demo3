package vn.vnpay.demo2.repository.feetransaction;

import vn.vnpay.demo2.entity.FeeTransaction;

import java.util.List;

public interface FeeTransactionRepository {
    int addFeeTransaction(FeeTransaction feeTransaction);
    int addFeeTransactions(List<FeeTransaction> feeTransactions);

    boolean updateFeeTransaction(FeeTransaction feeTransaction);

    List<FeeTransaction> getFeeTransactionsInProcess();

    List<FeeTransaction> findTransactionByCommandCode(String commandCode);


    int updateFeeTransactions(List<FeeTransaction> feeTransactions);

    FeeTransaction findByTransactionCode(String transactionCode);
}
