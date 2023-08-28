package vn.vnpay.sp2.common;

import org.yaml.snakeyaml.Yaml;

import java.util.Objects;

public class YamlCommon {
    private static Yaml instance;

    private YamlCommon() {
    }

    public static Yaml getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Yaml();
        }
        return instance;
    }
}
