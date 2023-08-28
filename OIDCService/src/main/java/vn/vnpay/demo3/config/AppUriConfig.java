package vn.vnpay.demo3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import vn.vnpay.demo3.common.ObjectMapperCommon;
import vn.vnpay.demo3.common.YamlCommon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Getter
public class AppUriConfig {
    private int port;
    private String host;
    private Uris uris;

    private AppUriConfig() {
    }

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
    private static AppUriConfig instance;

    public static AppUriConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getOidcServiceFileConfigPath());
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
            }
        }
        return instance;
    }

    public static AppUriConfig initInstance() throws IOException {
        return initInstance(null);
    }

    public static AppUriConfig initInstance(String path) throws IOException {
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = PATH_FILE_CONFIG.getOidcServiceFileConfigPath();
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, AppUriConfig.class);
            log.info("AppUriConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }

    @Getter
    public static class Uris {
        private String authenticationUri;
        private String verifyTokenUri;
        private String refreshTokenUri;
        private String getAccountInfo;
        private String reuseAccessToken;
    }
}
