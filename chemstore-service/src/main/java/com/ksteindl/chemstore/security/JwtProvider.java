package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.util.Lang;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Autowired
    private AppUserService appUserService;

    public static final Long EXPIRATION_TIME_IN_SECONDS = 1200l;
    public static final String SECRET_KEY = "thisIsASecretthisIsASecretthisIsASecretthisIsASecretthisIsASecretthisIsASecretthisIsASecretthisIsASecret";

    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName());
    }

    public String generateToken(String username) {
        AppUser appUser = appUserService.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, username));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_SECONDS * 1000);
        String userId = Long.toString(appUser.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", appUser.getUsername());
        claims.put("fullName", appUser.getFullName());
        UserDetails userDetails = new UserDetailsImpl(appUser);
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("labsAsUser", appUser.getLabKeysAsUser());
        claims.put("labsAsAdmin", appUser.getLabKeysAsAdmin());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");
        return  Long.parseLong(id);
    }

}
