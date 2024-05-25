package com.doc.mamagement.security.jwt;

import com.doc.mamagement.security.SecurityConfiguration;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET_KEY = "TakemyhandtakemywholelifetooforIcanthelpfallinginlovewithyou";
    public static final long JWT_TOKEN_VALIDITY = 1000L;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class.getName());

    public String generateToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_TOKEN_VALIDITY * SecurityConfiguration.tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .claim("userId", userId)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {0} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {0}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {0}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {0}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {0}", e);
        }

        return false;
    }

    public JWTUser getPrincipalFromJwtToken(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return new JWTUser((String) body.get("userId"), body.getSubject());
    }
}

