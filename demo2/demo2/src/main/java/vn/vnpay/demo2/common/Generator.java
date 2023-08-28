package vn.vnpay.demo2.common;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class Generator {
    public String generatorCode(String prefix) {
        StringBuilder genCode = new StringBuilder(prefix);

        return genCode
                .append(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, "0123456789".toCharArray(), 12))
                .toString();
    }
}
