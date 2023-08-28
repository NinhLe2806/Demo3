package vn.vnpay.sp2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.lib.bean.response.BaseResponse;
import vn.vnpay.sp2.bean.dto.ProductDTO;
import vn.vnpay.sp2.bean.entity.Product;
import vn.vnpay.sp2.common.ObjectMapperCommon;
import vn.vnpay.sp2.repository.ProductRepository;
import vn.vnpay.sp2.util.GeneratorUtil;

import java.util.Objects;

import static vn.vnpay.sp2.common.Constant.PRODUCT_PREFIX;
import static vn.vnpay.sp2.common.HttpResponseContextEnum.EMPTY_REQUEST;
import static vn.vnpay.sp2.common.HttpResponseContextEnum.SUCCESS;
import static vn.vnpay.sp2.common.HttpResponseContextEnum.SYSTEM_ERROR;

@Slf4j
public class ProductService {
    private static ProductService instance;

    public static ProductService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ProductService();
        }
        return instance;
    }

    private final ProductRepository productRepository = ProductRepository.getInstance();
    private final ObjectMapper objectMapper = ObjectMapperCommon.getInstance();

    public BaseResponse addProduct(ProductDTO productDTO) {
        log.info("Begin addProduct...");
        try {
            if (Objects.isNull(productDTO)) {
                log.info("productDTO is null");
                return new BaseResponse(EMPTY_REQUEST.getCode(), EMPTY_REQUEST.getMessage());
            }
            Product product = objectMapper.convertValue(productDTO, Product.class);
            product.setProductId(GeneratorUtil.genRandomString(PRODUCT_PREFIX));
            int successfulRecord = productRepository.addProduct(product);
            if(successfulRecord<=0){
                log.info("addProduct have no record affected");
                return new BaseResponse(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
            }
            log.info("End of addProduct, success added product!!!");
            return new BaseResponse(SUCCESS.getCode(), SUCCESS.getMessage(), product);
        } catch (Exception e) {
            log.info("Exception in addProduct :{}", e.getMessage());
            log.error("Exception in addProduct:", e);
            return new BaseResponse(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
        } catch (Throwable throwable) {
            log.info("An unexpected error occurred in addProduct :{}", throwable.getMessage());
            log.error("An unexpected error occurred in addProduct", throwable);
            return new BaseResponse(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
        }
    }
}
