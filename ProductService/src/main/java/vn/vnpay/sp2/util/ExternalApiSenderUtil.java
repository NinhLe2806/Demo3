package vn.vnpay.sp2.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.lib.bean.response.BaseResponse;
import vn.vnpay.sp2.bean.request.VerifyTokenRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static vn.vnpay.sp2.common.Constant.VERIFY_TOKEN_EXTERNAL;
import static vn.vnpay.sp2.common.HttpResponseContextEnum.SUCCESS;

@Slf4j
public class ExternalApiSenderUtil {
    private static ExternalApiSenderUtil instance;

    public static ExternalApiSenderUtil getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ExternalApiSenderUtil();
        }
        return instance;
    }

    public void verifyAccessToken(ChannelHandlerContext ctx, VerifyTokenRequest verifyTokenRequest) {
        try {
            log.info("Begin verifyAccessToken...");
            URL url = new URL(VERIFY_TOKEN_EXTERNAL);
            // Ghi dữ liệu vào request body
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(verifyTokenRequest);

            BaseResponse baseResponse = getResponseFromExternalApi(url, requestBody);
            if (Objects.isNull(baseResponse)) {
                log.info("baseResponse is null in verifyAccessToken");
                return;
            }
            //If success got key validate, skip response and start API to another
            if (baseResponse.getCode().equals(SUCCESS.getCode())) {
                log.info("AccessToken is valid, got verified");
                return;
            }

            ResponseCreatorUtil.getInstance().createResponse(ctx, baseResponse);
            log.info("End of verifyAccessToken");
            // close connection

        } catch (Exception e) {
            log.info("Exception in verifyAccessToken:{}", e.getMessage());
            log.error("Exception in verifyAccessToken:", e);
        }
    }

    private BaseResponse getResponseFromExternalApi(URL url, String requestBody) {
        HttpURLConnection connection = null;
        try {
            log.info("Begin getResponseFromExternalApi");
            //Open connection and setup to connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Ghi dữ liệu vào request body
            OutputStream os = connection.getOutputStream();
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);

            // Read response
            StringBuilder responseFromExternal = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                responseFromExternal.append(line);
            }
            log.info("Response from API: " + responseFromExternal);

            Gson gson = new Gson();
            BaseResponse baseResponse = gson.fromJson(responseFromExternal.toString(), BaseResponse.class);

            log.info("End of getResponseFromExternalApi");
            return baseResponse;
        } catch (Exception e) {
            log.info("Exception in getResponseFromExternalApi:{}", e.getMessage());
            log.error("Exception in getResponseFromExternalApi:", e);
            return null;
        } finally {
            if (!Objects.isNull(connection)) {
                connection.disconnect();
                log.info("Close connection to external API");
            }
        }
    }
}