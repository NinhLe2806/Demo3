package vn.vnpay.demo2.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import vn.vnpay.demo2.bean.AccountInfoRequest;
import vn.vnpay.demo2.bean.AccountInfoResponse;
import vn.vnpay.demo2.common.HttpResponse;
import vn.vnpay.demo2.dto.VerifyTokenRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Objects;

import static vn.vnpay.demo2.common.Constant.GET_ACCOUNT_INFO_EXTERNAL;
import static vn.vnpay.demo2.common.Constant.VERIFY_TOKEN_EXTERNAL;
import static vn.vnpay.demo2.dto.HttpResponseContext.SUCCESS;

@Slf4j
public class ExternalApiSender {
    private static ExternalApiSender instance;

    public static ExternalApiSender getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ExternalApiSender();
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

            HttpResponse httpResponse = getResponseFromExternalApi(url, requestBody);
            if (Objects.isNull(httpResponse)) {
                log.info("httpResponse is null in verifyAccessToken");
                return;
            }
            //If success got key validate, skip response and start API to another
            if (httpResponse.getCode().equals(SUCCESS.getCode())) {
                log.info("AccessToken is valid, got verified");
                return;
            }

            ResponseCreator.getInstance().createResponse(ctx, httpResponse);
            log.info("End of verifyAccessToken");
            // close connection

        } catch (Exception e) {
            log.error("Error in verifyAccessToken:", e);
        }
    }

    private HttpResponse getResponseFromExternalApi(URL url, String requestBody) {
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
            HttpResponse httpResponse = gson.fromJson(responseFromExternal.toString(), HttpResponse.class);

            log.info("End of getResponseFromExternalApi");
            return httpResponse;
        } catch (Exception e) {
            log.error("Error in getResponseFromExternalApi:", e);
            return null;
        } finally {
            if(!Objects.isNull(connection)){
                connection.disconnect();
                log.info("Close connection to external API");
            }
        }
    }

    public String getUsernameFromExternal(String userId) {
        try {
            log.info("Begin getUsernameFromExternal...");
            URL url = new URL(GET_ACCOUNT_INFO_EXTERNAL);

            //Read userId in accessToken
            log.info("Start reading userId in accessToken in getUsernameFromExternal");

            //Write data in reqBody
            ObjectMapper objectMapper = new ObjectMapper();
            AccountInfoRequest accountInfoRequest = AccountInfoRequest.builder().userId(userId).build();
            String requestBody = objectMapper.writeValueAsString(accountInfoRequest);
            log.info("Request body in getUsernameFromExternal:{} ", requestBody);

            HttpResponse httpResponse = getResponseFromExternalApi(url, requestBody);
            if (Objects.isNull(httpResponse)) {
                log.info("httpResponse is null in getUsernameFromExternal");
                return null;
            }
            Object httpResponseData = httpResponse.getData();
            ModelMapper modelMapper = new ModelMapper();
            AccountInfoResponse accountInfoResponse = modelMapper.map(httpResponseData, AccountInfoResponse.class);

            log.info("Get username in getUsernameFromExternal:{}", accountInfoResponse.getUsername());
            return accountInfoResponse.getUsername();
        } catch (Exception e) {
            log.error("Exception in getUsernameFromExternal:", e);
            return null;
        }
    }
}