package com.effectivemobile.socialmedia.controllers;

import com.effectivemobile.socialmedia.dto.userEntity.UserEntityLoginRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityRegistrationRequest;
import com.effectivemobile.socialmedia.dto.userEntity.UserEntityResponseDTO;
import com.effectivemobile.socialmedia.mapper.userEntity.UserEntityMapper;
import com.effectivemobile.socialmedia.model.UserEntity;
import com.effectivemobile.socialmedia.security.JWTUtil;
import com.effectivemobile.socialmedia.service.userEntity.UserEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final UserEntityService userEntityService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserEntityMapper userEntityMapper;

    public AuthController(UserEntityService userEntityService, JWTUtil jwtUtil,
                          AuthenticationManager authenticationManager, UserEntityMapper userEntityMapper) {
        this.userEntityService = userEntityService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userEntityMapper = userEntityMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@Valid @RequestBody UserEntityRegistrationRequest registrationUserEntity) {
        try {
            UserEntity newUserEntity = userEntityService.registerUserEntity(registrationUserEntity);
            UserEntityResponseDTO userEntityResponseDTO = userEntityMapper.toResponseDTO(newUserEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(userEntityResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginHandler(@RequestBody UserEntityLoginRequest loginUserEntity) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUserEntity.getEmail(), loginUserEntity.getPassword());
            authenticationManager.authenticate(authenticationToken);
            String token = jwtUtil.generateToken(loginUserEntity.getEmail());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
}
