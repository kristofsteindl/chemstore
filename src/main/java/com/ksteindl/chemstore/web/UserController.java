package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.payload.JwtLoginResponse;
import com.ksteindl.chemstore.payload.LoginRequest;
import com.ksteindl.chemstore.security.JwtProvider;
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
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;


    // USER
    @PostMapping("/login")
    public ResponseEntity<JwtLoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Authentication authentication  = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt =  jwtProvider.generateToken(authentication);
        return ResponseEntity.ok().body(new JwtLoginResponse(true, jwt));
    }
}
