package vn.vnpay.demo2.dto;

public enum HttpStatusCode {
    SUCCESS("000"),
    FAILED("001"),
    TIMEOUT("002");

    private final String code;

    HttpStatusCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
