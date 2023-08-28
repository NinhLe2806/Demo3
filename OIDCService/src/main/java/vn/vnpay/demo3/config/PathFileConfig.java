package vn.vnpay.demo3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import vn.vnpay.demo3.common.YamlCommon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class PathFileConfig {
    private String oidcServiceFileConfigPath;
    private String tokenFileConfigPath;
    private String redisFileConfigPath;

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final String APP_PATH = "D:/JavaProject/OIDCService/src/main/resources/application.yaml";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static PathFileConfig instance;

    private PathFileConfig() {
    }

    public static PathFileConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(APP_PATH);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static PathFileConfig initInstance(String path) throws IOException {
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = APP_PATH;
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, PathFileConfig.class);
            log.info("PathFileConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }
}
