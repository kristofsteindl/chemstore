package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.domain.entities.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    public static final Long EXPIRATION_TIME_IN_SECONDS = 3600l;
    public static final String SECRET_KEY = "thisIsASecret";

    public String generateToken(Authentication authentication) {
        AppUser appUser = (AppUser) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_SECONDS * 1000);
        String userId = Long.toString(appUser.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", appUser.getUsername());
        claims.put("fullName", appUser.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");
        return  Long.parseLong(id);
    }

}
