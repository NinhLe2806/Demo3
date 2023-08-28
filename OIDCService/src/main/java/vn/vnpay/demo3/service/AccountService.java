package vn.vnpay.demo3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.ObjectMapperCommon;
import vn.vnpay.demo3.bean.dto.AccountDTO;
import vn.vnpay.demo3.bean.entity.Account;
import vn.vnpay.demo3.repository.AccountRepository;

import java.util.Objects;

import static vn.vnpay.demo3.common.HttpResponseContext.NOT_FOUND_REC;
import static vn.vnpay.demo3.common.HttpResponseContext.NULL_REQUEST;
import static vn.vnpay.demo3.common.HttpResponseContext.SUCCESS;
import static vn.vnpay.demo3.common.HttpResponseContext.SYSTEM_ERROR;

@Slf4j
public class AccountService {
    private final AccountRepository accountRepository = AccountRepository.getInstance();
    private final ObjectMapper objectMapper = ObjectMapperCommon.getInstance();

    private static AccountService instance;

    public static AccountService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AccountService();
        }
        return instance;
    }

//    public boolean isValidLogin(AccountDTO accountDTO){
//        log.info("Begin isValidLogin... ");
//        if(null == accountDTO){
//            log.info("AccountDTO is null");
//            return false;
//        }
//        if(StringUtils.isBlank(accountDTO.getUsername()) || StringUtils.isBlank(accountDTO.getPassword())){
//            log.info("Username or Password can not be blank");
//            return false;
//        }
//        Account account = accountRepository.findAccountByUsername(accountDTO.getUsername());
//        if(null == account){
//            log.info("Can not find any username like that:{}",accountDTO.getUsername());
//            return false;
//        }
//        if(!accountDTO.getPassword().equals(account.getPassword())){
//            log.info("Password is not true");
//            return false;
//        }
//        log.info("Login success");
//        return true;
//    }

    public Response getAccountInfo(AccountDTO accountDTO) {
        try {
            if (Objects.isNull(accountDTO)) {
                log.info("accountDTO is null in getAccountInfo");
                return new Response(NULL_REQUEST.getCode(), NULL_REQUEST.getMessage());
            }
            Account account = accountRepository.findAccountByUserId(accountDTO.getUserId());
            if(Objects.isNull(account)){
                log.info("Can not find any account with userId:{}", accountDTO.getUserId());
                return new Response(NOT_FOUND_REC.getCode(), NOT_FOUND_REC.getMessage());
            }
            AccountDTO accountInfo = objectMapper.convertValue(account, AccountDTO.class);
            log.info("accountInfo:{}",accountInfo);
            return new Response(SUCCESS.getCode(), SUCCESS.getMessage(), accountInfo);

        } catch (Exception e) {
            log.info("Exception in getAccountInfo:{}", e.getMessage());
            log.error("Exception in getAccountInfo:", e);
            return new Response(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
        }
    }
}
