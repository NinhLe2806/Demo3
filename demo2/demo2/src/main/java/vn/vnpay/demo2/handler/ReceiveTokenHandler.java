package vn.vnpay.demo2.handler;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.security.Key;

import static vn.vnpay.demo2.common.Constant.SECRET_KEY;

@Slf4j
public class ReceiveTokenHandler {
    public static String getAccessToken(FullHttpRequest request) {
        log.info("Begin getAccessToken");
        // Get access token from header
        String accessToken = request.headers().get("Authorization");

        if (!StringUtils.isBlank(accessToken) && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // Skip "Bearer "
        } else {
            log.info("Access token is not valid");
            return null;
        }
        log.info("End of getAccessToken, get access Token: {}", accessToken);
        return accessToken;
    }

    public static String getUsernameFromIdToken(String idToken) {
        log.info("Begin getUsernameFromIdToken...");
        try {
            // Parse JWT token
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // Create a verifier for HMAC-SHA256
            JWSVerifier verifier = new MACVerifier(SECRET_KEY);

            if (!signedJWT.verify(verifier)) {
                log.info("JWT signature is invalid");
                return null;
            }
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(idToken)
                                .getBody();

            String username = claims.get("username", String.class);
            log.info("End of getUsernameFromIdToken, got username:{}", username);
            return username;
        } catch (Exception e) {
            log.error("Exception in getUsernameFromIdToken:", e);
            return null;
        }
    }

    public static String getUserIdFromAccessToken(String accessToken) {
        log.info("Begin getUserIdFromAccessToken...");
        try {
            // Parse JWT token
            SignedJWT signedJWT = SignedJWT.parse(accessToken);

            // Create a verifier for HMAC-SHA256
            JWSVerifier verifier = new MACVerifier(SECRET_KEY);

            if (!signedJWT.verify(verifier)) {
                log.info("JWT signature is invalid");
                return null;
            }
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(accessToken)
                                .getBody();

            String userId = claims.getSubject();
            log.info("End of getUserIdFromAccessToken, got userId:{}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Exception in getUsernameFromAccessToken:", e);
            return null;
        }
    }
}
