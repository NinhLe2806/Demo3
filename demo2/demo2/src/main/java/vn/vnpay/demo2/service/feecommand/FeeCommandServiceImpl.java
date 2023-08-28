package vn.vnpay.demo2.service.feecommand;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import vn.vnpay.demo2.dto.FeeCommandAddInfo;
import vn.vnpay.demo2.dto.FeeTransactionAddInfo;
import vn.vnpay.demo2.dto.HttpResponseContext;
import vn.vnpay.demo2.entity.FeeCommand;
import vn.vnpay.demo2.entity.Status;
import vn.vnpay.demo2.common.Generator;
import vn.vnpay.demo2.common.Constant;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.utils.Validator;
import vn.vnpay.demo2.repository.feecommand.FeeCommandRepository;
import vn.vnpay.demo2.service.feetransaction.FeeTransactionService;

@Slf4j
public class FeeCommandServiceImpl implements FeeCommandService {
    private final FeeCommandRepository feeCommandRepository;
    private final ModelMapper modelMapper;
    private final FeeTransactionService feeTransactionService;
    private final Generator generator;

    @Inject
    public FeeCommandServiceImpl(FeeCommandRepository feeCommandRepository, FeeTransactionService feeTransactionService,
                                 Generator generator) {
        this.feeCommandRepository = feeCommandRepository;
        this.feeTransactionService = feeTransactionService;
        this.generator = generator;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public HttpResponse addFeeCommand(FeeCommandAddInfo feeCommandAddInfo) {
        log.info("Begin addFeeCommand...");
        try{
            if (feeCommandAddInfo == null) {
                log.info(" Fee Command Add Info is null");
                return HttpResponse.failed(HttpResponseContext.BODY_REQUEST_NULL);
            }
            String requestId = feeCommandAddInfo.getRequestId();
            if (StringUtils.isBlank(requestId))
                return HttpResponse.failed(HttpResponseContext.REQUEST_ID_NULL);
            if (!Validator.checkDuplicateRequest(requestId)) {
                return HttpResponse.failed(HttpResponseContext.REQUEST_ID_DUPLICATED);
            }
            if (!Validator.checkByTimeLimit(feeCommandAddInfo)) {
                return HttpResponse.failed(HttpResponseContext.REQUEST_TIME_EXPIRED);
            }

            FeeCommand feeCommand = modelMapper.map(feeCommandAddInfo, FeeCommand.class);
            //TODO: Throw to constant
            feeCommand.setId(NanoIdUtils.randomNanoId());
            feeCommand.setCommandCode(generator.generatorCode(Constant.COMMAND_CODE_PREFIX));
            feeCommand.setCreatedDate(LocalDateTime.now());

            if (feeCommandRepository.addFeeCommand(feeCommand) > 0) {
                List<FeeTransactionAddInfo> feeTransactionAddInfos = new ArrayList<>();
                int totalRec = feeCommand.getTotalRecord();
                String commandCode = feeCommand.getCommandCode();
                for (int i = 0; i < totalRec; i++) {
                    FeeTransactionAddInfo addInfo = new FeeTransactionAddInfo();
                    addInfo.setCommandCode(commandCode);
                    addInfo.setStatus(Status.INITIALIZED);

                    feeTransactionAddInfos.add(addInfo);
                }
                feeTransactionService.addFeeTransactions(feeTransactionAddInfos);
                //TODO: log with full note
                log.info("Fee Command add successfully with command code:{}", feeCommand.getCommandCode());
                return HttpResponse.success(HttpResponseContext.ADD_SUCCESS, feeCommand);
            }
            //TODO: log nameMethod success or fail
            log.info("Cannot add in addFeeCommand");
            return HttpResponse.failed(HttpResponseContext.ADD_COMMAND_FAIL);
        } catch (Exception e) {
            log.error("Exception in addFeeCommand:",e);
            return HttpResponse.failed(HttpResponseContext.ADD_COMMAND_FAIL);
        }catch (Throwable throwable){
            log.error("An unexpected error occurred in addFeeCommand:", throwable);
            return HttpResponse.failed(HttpResponseContext.ADD_COMMAND_FAIL);
        }
    }
}
