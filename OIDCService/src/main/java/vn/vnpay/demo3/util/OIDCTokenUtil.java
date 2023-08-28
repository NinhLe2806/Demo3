package vn.vnpay.demo3.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.demo3.bean.OAuthToken;
import vn.vnpay.demo3.bean.dto.AccountDTO;

import java.security.Key;
import java.util.Date;

import static vn.vnpay.demo3.common.Constant.ACCESS_TOKEN_TYPE;
import static vn.vnpay.demo3.common.Constant.ID_TOKEN;
import static vn.vnpay.demo3.common.Constant.REFRESH_TOKEN;
import static vn.vnpay.demo3.common.Constant.TOKEN_TYPE;


@Slf4j
public class OIDCTokenUtil {
    private OIDCTokenUtil() {
    }

    public static String generateIdToken(
            String issuer, String secretKey, String clientId,
            Long expirationTimeIn, AccountDTO accountDTO) {
        log.info("Begin generateIdToken in OIDCTokenUtil");
        Date issueAt = new Date();
        Date expirationAt = new Date(issueAt.getTime() + expirationTimeIn);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            String idToken = Jwts.builder()
                                 .setIssuer(issuer)
                                 .setAudience(clientId)
                                 .claim("username", accountDTO.getUsername())
                                 .claim(TOKEN_TYPE, ID_TOKEN)
                                 .setIssuedAt(issueAt)
                                 .setExpiration(expirationAt)
                                 .signWith(key)
                                 .compact();
            log.info("End generateIdToken in OIDCTokenUtil, get idToken:{}", idToken);
            return idToken;
        } catch (Exception e) {
            log.info("Exception in generateIdToken:{}",e.getMessage());
            log.error("Exception in generateIdToken: ", e);
            return StringUtils.EMPTY;
        }
    }

    public static OAuthToken generateOAuthToken(
            String issuer, String secretKey, String clientId,
            Long expirationTimeIn, Long refreshExpirationTimeIn, AccountDTO accountDTO) {
        log.info("Begin generateOAuthToken in OIDCTokenUtil...");
        Date issueAt = new Date();
        Date expirationAt = new Date(issueAt.getTime() + expirationTimeIn);
        Date refreshExpirationAt = new Date(issueAt.getTime() + refreshExpirationTimeIn);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            String accessToken = Jwts.builder()
                                     .setIssuer(issuer)
                                     .claim("clientId", clientId)
                                     .claim(TOKEN_TYPE, ACCESS_TOKEN_TYPE)
                                     .setSubject(accountDTO.getUserId())
                                     .setIssuedAt(issueAt)
                                     .setExpiration(expirationAt)
                                     .signWith(key)
                                     .compact();
            log.info("Got accessToken in generateOAuthToken:{}", accessToken);
            String refreshToken = Jwts.builder()
                                      .setIssuer(issuer)
                                      .claim("clientId", clientId)
                                      .claim(TOKEN_TYPE, REFRESH_TOKEN)
                                      .setIssuedAt(issueAt)
                                      .setExpiration(refreshExpirationAt)
                                      .signWith(key)
                                      .compact();
            log.info("Got refreshToken in generateOAuthToken:{}", refreshToken);
            OAuthToken oAuthToken = OAuthToken.builder()
                                              .accessToken(accessToken)
                                              .refreshToken(refreshToken)
                                              .build();
            log.info("End generateIdToken in OIDCTokenUtil");
            return oAuthToken;
        } catch (Exception e) {
            log.info("Exception in generateOAuthToken:{} ", e.getMessage());
            log.error("Exception in generateOAuthToken: ", e);
            return null;
        }
    }
}
