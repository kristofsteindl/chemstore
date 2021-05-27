package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.payload.JwtLoginResponse;
import com.ksteindl.chemstore.payload.LoginRequest;
import com.ksteindl.chemstore.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private JwtTokenProvider jwtTokenProvider;
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
        return ResponseEntity.status(HttpStatus.OK).body(new JwtLoginResponse(true, jwtTokenProvider.generateToken(authentication)));
    }
}
