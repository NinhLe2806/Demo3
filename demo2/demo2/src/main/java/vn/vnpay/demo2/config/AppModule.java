package vn.vnpay.demo2.config;

import com.google.inject.AbstractModule;
import vn.vnpay.demo2.repository.feecommand.FeeCommandRepository;
import vn.vnpay.demo2.repository.feecommand.FeeCommandRepositoryImpl;
import vn.vnpay.demo2.repository.feetransaction.FeeTransactionRepository;
import vn.vnpay.demo2.repository.feetransaction.FeeTransactionRepositoryImpl;
import vn.vnpay.demo2.service.feecommand.FeeCommandService;
import vn.vnpay.demo2.service.feecommand.FeeCommandServiceImpl;
import vn.vnpay.demo2.service.feetransaction.FeeTransactionService;
import vn.vnpay.demo2.service.feetransaction.FeeTransactionServiceImpl;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // FeeCommand Module
        bind(FeeCommandRepository.class).to(FeeCommandRepositoryImpl.class);
        bind(FeeCommandService.class).to(FeeCommandServiceImpl.class);
        bind(FeeTransactionService.class).to(FeeTransactionServiceImpl.class);
        bind(FeeTransactionRepository.class).to(FeeTransactionRepositoryImpl.class);
    }
}
