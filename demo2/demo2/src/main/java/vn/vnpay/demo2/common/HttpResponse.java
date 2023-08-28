package vn.vnpay.demo2.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vnpay.demo2.dto.HttpResponseContext;


@Getter
@Setter
@Builder
public class HttpResponse {
    private String code;
    private String message;
//    private HttpStatusResponse httpStatusResponse;
    private Object data;

    @Builder
    public HttpResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static HttpResponse success(HttpResponseContext httpResponseContext, Object data) {
        return new HttpResponse(httpResponseContext.getCode(), httpResponseContext.getMessage(), data);
    }
    public static HttpResponse failed(HttpResponseContext httpResponseContext) {
        return new HttpResponse(httpResponseContext.getCode(), httpResponseContext.getMessage(), null);
    }
}
