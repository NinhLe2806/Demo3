package vn.vnpay.demo3.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.demo3.bean.OAuthToken;
import vn.vnpay.demo3.bean.TokenClient;
import vn.vnpay.demo3.bean.TokenResourceServer;
import vn.vnpay.demo3.bean.request.OIDCTokenRequest;
import vn.vnpay.demo3.bean.request.RefreshTokenRequest;
import vn.vnpay.demo3.bean.request.ReuseAccessTokenRequest;
import vn.vnpay.demo3.bean.response.Response;
import vn.vnpay.demo3.common.Validator;
import vn.vnpay.demo3.bean.dto.AccountDTO;
import vn.vnpay.demo3.bean.entity.Account;
import vn.vnpay.demo3.repository.AccountRepository;
import vn.vnpay.demo3.util.OIDCTokenUtil;

import java.security.Key;
import java.util.Objects;

import static vn.vnpay.demo3.common.Constant.ACCESS_TOKEN_TYPE;
import static vn.vnpay.demo3.common.Constant.TOKEN_TYPE;
import static vn.vnpay.demo3.common.HttpResponseContext.ACCESS_TOKEN_INVALID;
import static vn.vnpay.demo3.common.HttpResponseContext.INVALID_REQUEST;
import static vn.vnpay.demo3.common.HttpResponseContext.LOG_IN_AGAIN;
import static vn.vnpay.demo3.common.HttpResponseContext.NOT_FOUND_REC;
import static vn.vnpay.demo3.common.HttpResponseContext.NOT_FOUND_TOKEN;
import static vn.vnpay.demo3.common.HttpResponseContext.SUCCESS;
import static vn.vnpay.demo3.common.HttpResponseContext.SYSTEM_ERROR;
import static vn.vnpay.demo3.common.HttpResponseContext.TOKEN_EXPIRED;
import static vn.vnpay.demo3.common.HttpResponseContext.TOKEN_VALIDATE_ERROR;

@Slf4j
public class OIDCTokenService {
    private final TokenResourceServer tokenResourceServer = TokenResourceServer.getInstance();
    private static OIDCTokenService instance;

    public static OIDCTokenService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new OIDCTokenService();
        }
        return instance;
    }

    private OIDCTokenService() {
    }

    public String generateOAuthToken(OIDCTokenRequest request) {
        try {
            log.info("Begin generateAccessToken in OIDCTokenService... ");
            TokenClient tokenClient = tokenResourceServer.getClients().get(request.getClientId());
            log.info("TokenClient in generateOAuthToken:{}", tokenClient.getExpireTime());
            if (!Validator.isValidRequestToken(request, tokenClient)) {
                return StringUtils.EMPTY;
            }

            AccountDTO accountDTO = AccountDTO
                    .builder()
                    .userId(request.getUserId())
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();
            OAuthToken oAuthToken = OIDCTokenUtil.generateOAuthToken(
                    tokenResourceServer.getIssuer(),
                    tokenResourceServer.getSecretKey(),
                    tokenClient.getClientId(),
                    tokenClient.getExpireTime(),
                    tokenClient.getRefreshExpireTime(),
                    accountDTO);
            if (Objects.isNull(oAuthToken)) {
                log.info("OAuthToken is null");
                return null;
            }
            log.info("Save refresh token");
            RedisService
                    .getInstance()
                    .saveRefreshToken(tokenClient.getClientId(), accountDTO.getUserId(), oAuthToken.getRefreshToken(), tokenClient.getRefreshExpireTime());

            //Save user logged in to redis
            log.info("Save Logged in user");
            RedisService
                    .getInstance()
                    .saveLoggedInUser(accountDTO.getUserId(), oAuthToken.getAccessToken(), tokenClient.getExpireTime());
            log.info("End of generateAccessToken, got accessToken:{}", oAuthToken.getAccessToken());
            return oAuthToken.getAccessToken();
        } catch (Exception e) {
            log.info("Exception in generateAccessToken:{}", e.getMessage());
            log.error("Exception in generateAccessToken:", e);
            return null;
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in generateAccessToken:", throwable);
            return null;
        }
    }

    public String generateIdToken(OIDCTokenRequest request) {
        try {
            log.info("Begin generateIdToken in OIDCTokenService... ");
            TokenClient tokenClient = tokenResourceServer.getClients().get(request.getClientId());
            if (!Validator.isValidRequestToken(request, tokenClient)) {
                return null;
            }
            AccountDTO accountDTO = AccountDTO
                    .builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();
            String idToken = OIDCTokenUtil.generateIdToken(
                    tokenResourceServer.getIssuer(),
                    tokenResourceServer.getSecretKey(),
                    tokenClient.getClientId(),
                    tokenClient.getExpireTime(),
                    accountDTO);
            if (StringUtils.isBlank(idToken)) {
                log.info("OIDCToken is null or empty");
                return null;
            }
            log.info("End of generateIdToken in OIDCTokenService");
            return idToken;
        } catch (Exception e) {
            log.info("Exception in generateIdToken:{}", e.getMessage());
            log.error("Exception in generateIdToken:", e);
            return null;
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in generateIdToken:", throwable);
            return null;
        }
    }

    public Response validateAccessToken(String accessToken, String secretKey) {
        log.info("Begin validateAccessToken...");
        try {
            // Parse JWT token
            SignedJWT signedJWT = SignedJWT.parse(accessToken);

            // Create a verifier for HMAC-SHA256
            JWSVerifier verifier = new MACVerifier(secretKey);

            if (!signedJWT.verify(verifier)) {
                log.info("JWT signature is invalid");
                return new Response(TOKEN_VALIDATE_ERROR.getCode(), TOKEN_VALIDATE_ERROR.getMessage());
            }
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(accessToken)
                                .getBody();
            if (!ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE).toString())) {
                log.info("Token type not access token");
                return new Response(ACCESS_TOKEN_INVALID.getCode(), ACCESS_TOKEN_INVALID.getMessage());
            }
            return new Response(SUCCESS.getCode(), SUCCESS.getMessage());
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                log.info("Token has expired");
                return new Response(TOKEN_EXPIRED.getCode(), TOKEN_EXPIRED.getMessage());
            }
            log.error("validateAccessToken got Exception: ", e);
            return new Response(TOKEN_VALIDATE_ERROR.getCode(), TOKEN_VALIDATE_ERROR.getMessage());
        }
    }

    public Response reGrantAccessToken(RefreshTokenRequest request) {
        try {
            log.info("Begin reGrantAccessToken");
            String refreshToken = RedisService
                    .getInstance()
                    .getRefreshToken(request.getClientId(), request.getUserId());
            if (StringUtils.isBlank(refreshToken)) {
                log.info("Can not found refresh token for clientId:{} & userId:{}", request.getClientId(), request.getUserId());
                return new Response(NOT_FOUND_TOKEN.getCode(), NOT_FOUND_TOKEN.getMessage());
            }

            TokenClient tokenClient = tokenResourceServer.getClients().get(request.getClientId());
            if (!Validator.isValidRequestToken(request, tokenClient)) {
                return new Response(INVALID_REQUEST.getCode(), INVALID_REQUEST.getMessage());
            }

            AccountDTO accountDTO = AccountDTO
                    .builder()
                    .userId(request.getUserId())
                    .build();
            //Pretend username is always found record...
            OAuthToken oAuthToken = OIDCTokenUtil.generateOAuthToken(
                    tokenResourceServer.getIssuer(),
                    tokenResourceServer.getSecretKey(),
                    tokenClient.getClientId(),
                    tokenClient.getExpireTime(),
                    tokenClient.getRefreshExpireTime(),
                    accountDTO);

            if (Objects.isNull(oAuthToken)) {
                log.info("Some thing went wrong, oAuthToken in reGrantAccessToken is null!");
                return null;
            }
            if (!Objects.isNull(oAuthToken.getRefreshToken())) {
                RedisService
                        .getInstance()
                        .saveRefreshToken(tokenClient.getClientId(), request.getUserId(), oAuthToken.getRefreshToken(), tokenClient.getRefreshExpireTime());
                log.info("Save refreshToken in Redis ");
                log.info("Save Logged in user");
                RedisService
                        .getInstance()
                        .saveLoggedInUser(accountDTO.getUserId(), oAuthToken.getAccessToken(), tokenClient.getExpireTime());
            }
            log.info("Success reGrantAccessToken for clientID:{} & username:{}", request.getClientId(), request.getUserId());
            return new Response(SUCCESS.getCode(), SUCCESS.getMessage(), oAuthToken);
        } catch (Exception e) {
            throw e;
        }
    }

    public Response grantAccessTokenForLoggedInUser(ReuseAccessTokenRequest reuseAccessTokenRequest) {
        log.info("Begin grantAccessTokenForLoggedInUser...");
        try {
            if (StringUtils.isBlank(reuseAccessTokenRequest.getUsername())) {
                log.info("Username is invalid in grantAccessTokenForLoggedInUser");
                return new Response(INVALID_REQUEST.getCode(), INVALID_REQUEST.getMessage());
            }
            TokenClient tokenClient = tokenResourceServer.getClients().get(reuseAccessTokenRequest.getClientId());
            if (!Validator.isValidRequestToken(reuseAccessTokenRequest, tokenClient)) {
                return new Response(INVALID_REQUEST.getCode(), INVALID_REQUEST.getMessage());
            }
            Account account = AccountRepository
                    .getInstance()
                    .findAccountByUsername(reuseAccessTokenRequest.getUsername());
            if (Objects.isNull(account)) {
                log.error("Not found any account match with username :{} in grantAccessTokenForLoggedInUser ", reuseAccessTokenRequest.getUsername());
                return new Response(NOT_FOUND_REC.getCode(), NOT_FOUND_REC.getMessage());
            }
            String userId = account.getUserId();
            String accessToken = RedisService.getInstance().getAccessKeyFromLoggedInUser(userId);
            if (StringUtils.isBlank(accessToken)) {
                log.info("AccessToken is blank for username:{} in grantAccessTokenForLoggedInUser ", reuseAccessTokenRequest.getUsername());
                return new Response(LOG_IN_AGAIN.getCode(), LOG_IN_AGAIN.getMessage());
            }
            log.info("End of grantAccessTokenForLoggedInUser, success get accessToken for username:{}", reuseAccessTokenRequest.getUsername());
            log.info("AccessToken:{}", accessToken);
            return new Response(SUCCESS.getCode(), SUCCESS.getMessage(), OAuthToken
                    .builder()
                    .accessToken(accessToken)
                    .build());
        } catch (Exception e) {
            log.info("Exception in grantAccessTokenForLoggedInUser:{} ", e.getMessage());
            log.error("Exception in grantAccessTokenForLoggedInUser: ", e);
            return new Response(SYSTEM_ERROR.getCode(), SYSTEM_ERROR.getMessage());
        }
    }
}
