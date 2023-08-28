package vn.vnpay.demo3.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import vn.vnpay.demo3.common.ObjectMapperCommon;
import vn.vnpay.demo3.common.YamlCommon;
import vn.vnpay.demo3.config.PathFileConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class TokenResourceServer {
    private String secretKey;
    private String issuer;
    private Map<String, TokenClient> clients;

    private TokenResourceServer() {
    }

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
    private static TokenResourceServer instance;

    public static TokenResourceServer getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getTokenFileConfigPath());
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static TokenResourceServer initInstance(String path) throws IOException {
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = PATH_FILE_CONFIG.getTokenFileConfigPath();
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, TokenResourceServer.class);
            log.info("TokenFileConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }
}