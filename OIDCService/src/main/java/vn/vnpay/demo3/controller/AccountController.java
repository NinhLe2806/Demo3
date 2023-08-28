package vn.vnpay.demo3.controller;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.GsonCommon;
import vn.vnpay.demo3.bean.dto.AccountDTO;
import vn.vnpay.demo3.service.AccountService;
import vn.vnpay.demo3.util.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class AccountController {
    private static AccountController instance;

    public static AccountController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AccountController();
        }
        return instance;
    }

    private final AccountService accountService = AccountService.getInstance();
    private final ResponseCreator responseCreator = ResponseCreator.getInstance();
    private final Gson gson = GsonCommon.getInstance();


    protected void getAccountInfo(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        String requestBody = fullHttpRequest.content().toString(StandardCharsets.UTF_8);

        log.info("Start to getAccountInfo in AccountController");
        AccountDTO accountDTO = gson.fromJson(requestBody, AccountDTO.class);
        Response response = accountService.getAccountInfo(accountDTO);

        responseCreator.createResponse(ctx, response);
        log.info("End of getAccountInfo in AccountController, finish created response");
    }
}
