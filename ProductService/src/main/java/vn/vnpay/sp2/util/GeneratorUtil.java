package vn.vnpay.sp2.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class GeneratorUtil {
    public static String genRandomString(String prefix) {
        StringBuilder genCode = new StringBuilder(prefix);

        return genCode
                .append(NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, "0123456789".toCharArray(), 12))
                .toString();
    }
}
