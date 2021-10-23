package com.ksteindl.chemstore.security;

import com.google.gson.Gson;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;



@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    private static String INVALID_AUTHENTICATION_RESPONSE;

    {
        Map<String, String> invalidAuthenticationMap = Map.of("username", "Invalid username", "password", "Invalid password");
        INVALID_AUTHENTICATION_RESPONSE =new Gson().toJson(invalidAuthenticationMap);
    }
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(401);
        httpServletResponse.getWriter().print(INVALID_AUTHENTICATION_RESPONSE);

    }

}
