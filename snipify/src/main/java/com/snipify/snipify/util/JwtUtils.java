package com.snipify.snipify.util;


import com.snipify.snipify.service.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(UserDetailsImpl userDetails){
            String username=userDetails.getEmail();
            String roles=userDetails.getAuthorities().stream()
                    .map(authority->authority.getAuthority())
                    .collect(Collectors.joining(","));
            return Jwts.builder()
                    .subject(username)
                    .claim("roles",roles)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date().getTime()+jwtExpirationMs)))
                    .signWith(getSecretKey())
                    .compact();

    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }



    public boolean validateToken (String token){
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());

            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
