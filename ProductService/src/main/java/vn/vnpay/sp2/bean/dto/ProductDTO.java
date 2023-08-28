package vn.vnpay.sp2.bean.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private String productName;
    private double price;
    private String createdUser;
}
