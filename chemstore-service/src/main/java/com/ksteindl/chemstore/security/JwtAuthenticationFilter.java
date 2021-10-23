package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.service.AppUserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_STRING = "Authorization";

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AppUserService appUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = httpServletRequest.getHeader(HEADER_STRING);
            Long userId = jwtProvider.getUserIdFromToken(token);
            UserDetails appUserDetails = appUserService.loadUserById(userId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(appUserDetails, null, appUserDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (SignatureException signatureException) {
            logger.info("Invalid JWT Signature");
        } catch (MalformedJwtException malformedJwtException) {
            logger.info("Invalid JWT token");
        } catch (ExpiredJwtException expiredJwtException) {
            logger.info("Expired JWT token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            logger.info("Unsopported JWT token");
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.info("JWT claims string is empty");
        } catch (Exception exception) {
            logger.error("Could not set user authentication in security context", exception);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
