package vn.vnpay.demo2.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.demo2.bean.WelcomeRequest;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.utils.ResponseCreator;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static vn.vnpay.demo2.common.Constant.WELCOME_MESSAGE;
import static vn.vnpay.demo2.dto.HttpResponseContext.TOKEN_INVALID;

@Slf4j
public class WelcomeHandler {
    private static WelcomeHandler instance;

    public static WelcomeHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WelcomeHandler();
        }
        return instance;
    }

    private final ResponseCreator responseCreator = ResponseCreator.getInstance();

    public void createEntryResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Begin createEntryResponse");
        try {
            String requestBody = request.content().toString(StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            WelcomeRequest welcomeRequest = objectMapper.readValue(requestBody, WelcomeRequest.class);
            String username = ReceiveTokenHandler.getUsernameFromIdToken(welcomeRequest.getIdToken());
            if (StringUtils.isBlank(username)) {
                log.info("idToken is invalid");
                HttpResponse httpResponse = HttpResponse
                        .builder()
                        .code(TOKEN_INVALID.getCode())
                        .message(TOKEN_INVALID.getMessage())
                        .build();
                responseCreator.createResponse(ctx, httpResponse);
            }
            log.info("Create welcome message for username:{}", username);
            String welcomeMessage = new StringBuilder().append(WELCOME_MESSAGE).append(username).toString();
            ctx.writeAndFlush(responseCreator.createTextResponse(welcomeMessage));
            log.info("End of createEntryResponse!");
        } catch (JsonProcessingException e) {
            log.error("Error while parsing createEntryResponse JSON: ", e);
        }
    }
}
