package vn.vnpay.demo2.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.dto.FeeCommandAddInfo;
import vn.vnpay.demo2.dto.HttpResponseContext;
import vn.vnpay.demo2.service.feecommand.FeeCommandService;
import vn.vnpay.demo2.utils.ExternalApiSender;
import vn.vnpay.demo2.utils.Validator;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
@Slf4j
public class FeeCommandControllerHandler {
    private final FeeCommandService feeCommandService;
    private final ObjectMapper objectMapper;

    @Inject
    public FeeCommandControllerHandler(FeeCommandService feeCommandService) {
        this.feeCommandService = feeCommandService;
        this.objectMapper = new ObjectMapper();
    }

    public void handleAddFeeCommand(ChannelHandlerContext ctx, FullHttpRequest request) {
        //TODO: CHECK REQUEST NULL
        HttpResponse response = null;
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        if (Validator.isRequestNull(requestBody)) {
            log.info("Request is null");
            response = HttpResponse.failed(HttpResponseContext.BODY_REQUEST_NULL);
            FullHttpResponse fullHttpResponse = createResponse(HttpResponseStatus.BAD_REQUEST, response);
            ctx.writeAndFlush(fullHttpResponse);
            return;
        }
        FeeCommandAddInfo feeCommandAddInfo;
        try {
            feeCommandAddInfo = objectMapper.readValue(requestBody, FeeCommandAddInfo.class);
            log.info("Received FeeCommandAddInfo: {}", feeCommandAddInfo);
            //Get username from accessToken, call externalAPI
            log.info("Start getting accessToken in handleAddFeeCommand");
            String accessToken = ReceiveTokenHandler.getAccessToken(request);
            log.info("Done with getting accessToken in handleAddFeeCommand, get accessToken:{}", accessToken);

            log.info("Start getting userId from accessToken in handleAddFeeCommand");
            String userId = ReceiveTokenHandler.getUserIdFromAccessToken(accessToken);
            log.info("Done with getting userId from accessToken in handleAddFeeCommand get userId:{}", userId);

            String username = ExternalApiSender.getInstance().getUsernameFromExternal(userId);
            log.info("Done with getting username from accessToken in handleAddFeeCommand get username:{}", username);
            feeCommandAddInfo.setCreatedUser(username);
            response = feeCommandService.addFeeCommand(feeCommandAddInfo);
        } catch (JsonProcessingException e) {
            log.info("Error while parsing FeeCommandAddInfo JSON: {}", requestBody, e);
        }
        if (response != null && response.getCode().equals(HttpResponseContext.ADD_SUCCESS.getCode())) {
            FullHttpResponse HttpResponse = createResponse(HttpResponseStatus.CREATED, response);
            log.info("Response success");
            ctx.writeAndFlush(HttpResponse);
        } else {
            FullHttpResponse HttpResponse = createResponse(HttpResponseStatus.BAD_REQUEST, response);
            log.info("Response bad request");
            ctx.writeAndFlush(HttpResponse);
        }
    }

    private FullHttpResponse createResponse(HttpResponseStatus status, HttpResponse httpResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String content;
        try {
            content = objectMapper.writeValueAsString(httpResponse);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing HttpResponse to JSON", e);
            // Handle the error and return an appropriate response, e.g. error 500
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
