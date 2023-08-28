package vn.vnpay.demo2.service.feetransaction;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import vn.vnpay.demo2.common.Generator;
import vn.vnpay.demo2.common.Constant;
import vn.vnpay.demo2.dto.FeeTransactionAddInfo;
import vn.vnpay.demo2.dto.FeeTransactionUpdateInfo;
import vn.vnpay.demo2.dto.HttpResponseContext;
import vn.vnpay.demo2.entity.FeeTransaction;
import vn.vnpay.demo2.entity.Status;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.utils.Validator;
import vn.vnpay.demo2.repository.feetransaction.FeeTransactionRepository;
import vn.vnpay.demo2.schedule.CronJobScheduler;


//TODO: Check TryCatch tất cả service
@Slf4j
public class FeeTransactionServiceImpl implements FeeTransactionService {

    private final FeeTransactionRepository repository;
    private final ModelMapper modelMapper;
    private final CronJobScheduler cronJobScheduler;
    private final Generator generator;

    @Inject
    public FeeTransactionServiceImpl(FeeTransactionRepository repository, Generator generator) {
        this.repository = repository;
        this.generator = generator;
        this.modelMapper = new ModelMapper();
        this.cronJobScheduler = new CronJobScheduler();
    }

    @Override
    public void addFeeTransaction(FeeTransactionAddInfo feeTransactionAddInfo) {
        log.info("Begin addFeeTransaction...");
        try {
            if (feeTransactionAddInfo == null) {
                log.info("feeTransactionAddInfo is null");
            }

            FeeTransaction feeTransaction = modelMapper.map(feeTransactionAddInfo, FeeTransaction.class);

            feeTransaction.setId(NanoIdUtils.randomNanoId().replaceAll("-", ""));

            feeTransaction.setTransactionCode(generateUniqueTransactionCode());
            feeTransaction.setCreatedDate(LocalDateTime.now());
            feeTransaction.setModifiedDate(LocalDateTime.now());

            if (repository.addFeeTransaction(feeTransaction) > 0) {
                log.info("Successfully added this Transaction: {}", feeTransaction.getTransactionCode());
            } else {
                log.info("Fail added this Transaction: {}", feeTransaction.getTransactionCode());
            }
        } catch (Exception e) {
            log.error("Exception in addFeeTransaction: ", e);
        }

    }

    @Override
    public void addFeeTransactions(List<FeeTransactionAddInfo> feeTransactionAddInfos) {
        log.info("Begin addFeeTransactions...");
        if (feeTransactionAddInfos == null || feeTransactionAddInfos.isEmpty()) {
            log.info("feeTransactionAddInfo is null or empty");
            return;
        }
        List<FeeTransaction> feeTransactions = new ArrayList<>();

        for (FeeTransactionAddInfo feeTransactionAddInfo : feeTransactionAddInfos) {
            FeeTransaction feeTransaction = modelMapper.map(feeTransactionAddInfo, FeeTransaction.class);
            feeTransaction.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            feeTransaction.setTransactionCode(generateUniqueTransactionCode());
            feeTransaction.setCreatedDate(LocalDateTime.now());
            feeTransaction.setModifiedDate(LocalDateTime.now());

            feeTransactions.add(feeTransaction);
        }
        int result = repository.addFeeTransactions(feeTransactions);

        if (result > 0) {
            log.info("Successfully added {} Transactions", result);
        } else {
            log.info("Fail");
        }
    }

    @Override
    public HttpResponse updateFeeTransaction(FeeTransactionUpdateInfo feeTransactionUpdateInfo) {
        log.info("Begin updateFeeTransaction...");
        try {
            if (feeTransactionUpdateInfo == null) {
                log.info("Please check again the update info");
                return HttpResponse.failed(HttpResponseContext.BODY_REQUEST_NULL);
            }
            String requestId = feeTransactionUpdateInfo.getRequestId();
            if (StringUtils.isBlank(requestId))
                return HttpResponse.failed(HttpResponseContext.REQUEST_ID_NULL);
            if (!Validator.checkDuplicateRequest(requestId)) {
                return HttpResponse.failed(HttpResponseContext.REQUEST_ID_DUPLICATED);
            }
            if (!Validator.checkByTimeLimit(feeTransactionUpdateInfo)) {
                return HttpResponse.failed(HttpResponseContext.REQUEST_TIME_EXPIRED);
            }

            List<FeeTransaction> feeTransactions =
                    findFeeTransactionByCommandCode(feeTransactionUpdateInfo.getCommandCode());

            if (feeTransactions == null || feeTransactions.isEmpty()) {
                log.info("Not find any Transaction with Command code: {}", feeTransactionUpdateInfo.getCommandCode());
                return HttpResponse.failed(HttpResponseContext.NOT_FOUND_RECORD);
            }
            for (FeeTransaction feeTransaction : feeTransactions) {
                log.info(feeTransaction.getTransactionCode());
                feeTransaction.setModifiedDate(LocalDateTime.now());
                //todo: ném 1 ra constant(check lại tất cả fix cứng)
                feeTransaction.setTotalScan(Constant.initial_scan);
                feeTransaction.setStatus(Status.FEE_COLLECTION);
            }
            if (repository.updateFeeTransactions(feeTransactions) > 0) {
                log.info("Fee Transaction updated successfully");
                return HttpResponse.success(HttpResponseContext.UPDATE_SUCCESS, feeTransactions);
            }
            log.info("No record updated");
            return HttpResponse.failed(HttpResponseContext.UPDATE_TRANSACTION_FAIL);
        } catch (Exception e) {
            log.error("Exception in updateFeeTransaction:", e);
            return HttpResponse.failed(HttpResponseContext.UPDATE_TRANSACTION_FAIL);
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in updateFeeTransaction", throwable);
            return HttpResponse.failed(HttpResponseContext.UPDATE_TRANSACTION_FAIL);

        }
    }

    private void updateTransactionsWithCronJob() {
        log.info("CronJob started");
        try {
            List<FeeTransaction> feeTransactions = repository.getFeeTransactionsInProcess();

            for (FeeTransaction feeTransaction : feeTransactions) {
                int newScanCount = feeTransaction.getTotalScan() + 1;
                feeTransaction.setTotalScan(newScanCount);
                //TODO: CHO 5 RA CONSTANT
                if (newScanCount >= Constant.limitScan) {
                    log.info("This transaction reach limit scan: {}", feeTransaction.getTransactionCode());
                    feeTransaction.setStatus(Status.STOPPED);
                }
                if (repository.updateFeeTransaction(feeTransaction)) {
                    //TODO: XEM LOG CÁC THÔNG TIN THÌ PHẢI THÊM TÊN TRƯỜNG...
                    log.info("Cronjob updated this Transaction successfully with Transaction code: {}", feeTransaction.getTransactionCode());
                } else {
                    log.info("Cronjob updated this Transaction failed with Transaction code: {}", feeTransaction.getTransactionCode());
                }
            }
        } catch (Exception e) {
            log.error("Exception in updateTransactionsWithCronJob:", e);
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in updateTransactionsWithCronJob", throwable);
        }

    }

    @Override
    public void startCronJobToUpdateTransactions() {
        //Config for cronjob
        //TODO: LOG KHỞI TẠO CRONJOB && NÉM CONSTANT
        log.info("Begin to initialize cronjob");
        Runnable task = this::updateTransactionsWithCronJob;
        long initialDelay = Constant.cronjob_initial_delay;
        long period = Constant.cronjob_period;
        TimeUnit unit = TimeUnit.SECONDS;

        cronJobScheduler.startCronJob(task, initialDelay, period, unit);
    }

    @Override
    public List<FeeTransaction> findFeeTransactionByCommandCode(String commandCode) {
        //todo: thêm trycatch
        try {
            if (StringUtils.isBlank(commandCode)) {
                log.info("CommandCode is not correct");
                return null;
            }
            return repository.findTransactionByCommandCode(commandCode);
        } catch (Exception e) {
            log.error("Exception in findFeeTransactionByCommandCode:", e);
            return null;
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in findFeeTransactionByCommandCode", throwable);
            return null;
        }
    }

    private String generateUniqueTransactionCode() {
        String transactionCode;
        do {
            transactionCode = generator.generatorCode(Constant.TRANSACTION_CODE_PREFIX);
        } while (repository.findByTransactionCode(transactionCode) == null); // check if existed in DB

        return transactionCode;
    }
}
