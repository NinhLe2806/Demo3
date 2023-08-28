package vn.vnpay.sp2.controller;

import com.google.gson.Gson;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.lib.bean.response.BaseResponse;
import vn.vnpay.sp2.bean.dto.ProductDTO;
import vn.vnpay.sp2.common.GsonCommon;
import vn.vnpay.sp2.common.Validator;
import vn.vnpay.sp2.config.AppUriConfig;
import vn.vnpay.sp2.service.ProductService;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static vn.vnpay.sp2.common.HttpResponseContextEnum.NULL_REQUEST;

@Slf4j
public class ProductController {
    private static ProductController instance;

    public static ProductController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ProductController();
        }
        return instance;
    }

    private final AppUriConfig appUriConfig = AppUriConfig.getInstance();
    private final ProductService productService = ProductService.getInstance();
    private final Gson gson = GsonCommon.getInstance();


    protected BaseResponse addProductRequest(FullHttpRequest request) {
        log.info("Begin addProductRequest..");
        //Mapping request to ProductDTO
        String requestBody = request.content().toString(StandardCharsets.UTF_8);
        if (Validator.isRequestNull(requestBody)) {
            log.info("Request is invalid in addProductRequest");
            return new BaseResponse(NULL_REQUEST.getCode(), NULL_REQUEST.getMessage());
        }
        ProductDTO productDTO = gson.fromJson(requestBody, ProductDTO.class);
        BaseResponse baseResponse = productService.addProduct(productDTO);
        log.info("End of addProductRequest, get response:{}", baseResponse);
        return baseResponse;
    }
}
