package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.domain.entities.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    public static final Long EXPIRATION_TIME_IN_SECUNDS = 3600l;
    public static final String SECRET_KEY = "thisIsASecret";

    public String generateToken(Authentication authentication) {
        AppUser appUser = (AppUser) authentication.getPrincipal();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiryDate = now.plusSeconds(EXPIRATION_TIME_IN_SECUNDS);
        String userId = Long.toString(appUser.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", appUser.getUsername());
        claims.put("fullName", appUser.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toLocalDateTime().toInstant(ZoneOffset.of(now.getOffset().getId()))))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

    }


}
