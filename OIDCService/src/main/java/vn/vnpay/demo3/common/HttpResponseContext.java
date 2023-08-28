package vn.vnpay.demo3.common;

import lombok.Getter;

@Getter
public enum HttpResponseContext {
    SUCCESS("200000", "Success"),
    GENERATE_TOKEN_ERROR("400001", "Bad request!"),
    TOKEN_NOT_YET("400002", "Token not yet"),
    TOKEN_EXPIRED("400003", "Token is expired"),
    TOKEN_VALIDATE_ERROR("400004", "Validate token is error"),
    CLIENT_INVALID("400005", "Invalid client match"),
    ACCESS_TOKEN_INVALID("400006", "Invalid access token"),
    REFRESH_TOKEN_INVALID("400007", "Invalid refresh token"),
    NOT_FOUND_TOKEN("400008", "Not found valid token"),
    API_INVALID("404001", "API invalid"),
    UNKNOWN_ERROR("500000", "Unknown error"),
    NULL_REQUEST("400009","Request is null"),
    NOT_FOUND_REC("400101","Not found any record with your request"),
    INVALID_REQUEST("400103", "Invalid request"),
    LOG_IN_AGAIN("400104","Please login again"),
    SYSTEM_ERROR("500001","There is a problem with the application, please try again later")
    ;
    private final String code;
    private final String message;

    HttpResponseContext(String errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }
}
