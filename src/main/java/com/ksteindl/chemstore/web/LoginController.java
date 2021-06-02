package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.payload.JwtLoginResponse;
import com.ksteindl.chemstore.payload.LoginRequest;
import com.ksteindl.chemstore.security.JwtProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/login")
@CrossOrigin
public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;


    // USER
    @PostMapping()
    public ResponseEntity<JwtLoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        logger.info("'/login' was called with {}", loginRequest.getUsername());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Authentication authentication  = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        logger.info("Successful login ({})", loginRequest.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt =  jwtProvider.generateToken(authentication);
        return ResponseEntity.ok().body(new JwtLoginResponse(true, jwt));
    }
}
