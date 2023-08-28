package vn.vnpay.demo2.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
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
import vn.vnpay.demo2.dto.FeeTransactionUpdateInfo;
import vn.vnpay.demo2.dto.HttpResponseContext;
import vn.vnpay.demo2.service.feetransaction.FeeTransactionService;
import vn.vnpay.demo2.utils.Validator;

import java.nio.charset.StandardCharsets;

@Slf4j
@ChannelHandler.Sharable
public class FeeTransactionControllerHandler{
    private final FeeTransactionService feeTransactionService;
    private final ObjectMapper objectMapper;

    @Inject
    public FeeTransactionControllerHandler(FeeTransactionService feeTransactionService, ObjectMapper objectMapper) {
        this.feeTransactionService = feeTransactionService;
        this.objectMapper = objectMapper;
    }

    public void handleUpdateFeeTransaction(ChannelHandlerContext ctx, FullHttpRequest request) {
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        HttpResponse response = null;
        if (Validator.isRequestNull(requestBody)) {
            log.info("Request is null");
            response = HttpResponse.failed(HttpResponseContext.BODY_REQUEST_NULL);
            FullHttpResponse fullHttpResponse = createResponse(HttpResponseStatus.BAD_REQUEST, response);
            ctx.writeAndFlush(fullHttpResponse);
            return;
        }

        FeeTransactionUpdateInfo feeTransactionUpdateInfo;
        try {
            feeTransactionUpdateInfo = objectMapper.readValue(requestBody, FeeTransactionUpdateInfo.class);
            log.info("Received FeeTransactionUpdateInfo with requestId: {}", feeTransactionUpdateInfo.getRequestId());
            response = feeTransactionService.updateFeeTransaction(feeTransactionUpdateInfo);
            log.info(response.toString());
        } catch (JsonProcessingException e) {
            log.info("Error while parsing FeeTransactionUpdateInfo JSON: {}", requestBody, e);
        }
        if (response != null && response.getCode().equals(HttpResponseContext.UPDATE_SUCCESS.getCode())) {
            FullHttpResponse HttpResponse = createResponse(HttpResponseStatus.CREATED, response);
            log.info("Response success in handleUpdateFeeTransaction");
            ctx.writeAndFlush(HttpResponse);
        } else {
            FullHttpResponse HttpResponse = createResponse(HttpResponseStatus.BAD_REQUEST, response);
            log.info("Response bad request in handleUpdateFeeTransaction");
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
