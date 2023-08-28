package vn.vnpay.demo3.common;

import com.google.gson.Gson;

import java.util.Objects;

public class GsonCommon {
    private static Gson instance;

    private GsonCommon() {
    }

    public static Gson getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Gson();
        }
        return instance;
    }
}
