package vn.vnpay.demo2.dto;

import lombok.Getter;

@Getter
public enum HttpResponseContext {
    //Error
    REQUEST_ID_NULL("400001", "RequestId is null"),
    UPDATE_TRANSACTION_FAIL("400002", "Update transaction fail"),
    ADD_COMMAND_FAIL("400003", "Add command fail"),
    REQUEST_ID_DUPLICATED("400004", "RequestId is duplicated within a day"),
    REQUEST_TIME_EXPIRED("400005", "Request time is expired "),
    NOT_FOUND_RECORD("40006","Not find any data for this request"),
    API_INVALID("404002", "API invalid"),
    NOT_YET_LOGIN("400007","Please login first"),
    UNKNOWN_ERROR("500000", "Unknown error"),
    BODY_REQUEST_NULL("500001", "Body request is null"),
    TOKEN_INVALID("400008", "Invalid token"),

    //Success
    UPDATE_SUCCESS("000","Update successfully"),
    ADD_SUCCESS("000","Add successfully"),
    SUCCESS("200000","Success");

    private final String code;
    private final String message;
    HttpResponseContext(String code, String message){
        this.code = code;
        this.message = message;
    }

}
