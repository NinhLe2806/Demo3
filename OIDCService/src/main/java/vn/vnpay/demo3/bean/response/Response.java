package vn.vnpay.demo3.bean.response;

import lombok.Setter;

@Setter
public class Response {
    private String code ;
    private String message ;
    private Object data;

    public Response() {
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(Object data) {
        this.data = data;
    }
}
