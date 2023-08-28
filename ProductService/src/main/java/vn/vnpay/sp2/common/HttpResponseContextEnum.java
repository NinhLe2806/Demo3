package vn.vnpay.sp2.common;

import lombok.Getter;

@Getter
public enum HttpResponseContextEnum {
    SUCCESS("200000", "Success"),
    ADD_COMMAND_FAIL("400003", "Add request fail"),
    NOT_YET_LOGIN("400007","Please login first"),
    API_INVALID("404001", "API invalid"),
    UNKNOWN_ERROR("500000", "Unknown error"),
    NULL_REQUEST("400009","Request is null"),
    EMPTY_REQUEST("400010","Request is invalid"),
    NOT_FOUND_REC("400101","Not found any record with your request"),
    SYSTEM_ERROR("400102","There is a problem with the application, please try again later")
    ;
    private final String code;
    private final String message;

    HttpResponseContextEnum(String errorCode, String errorMessage) {
        this.code = errorCode;
        this.message = errorMessage;
    }
}
