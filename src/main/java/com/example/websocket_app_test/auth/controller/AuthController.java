package com.example.websocket_app_test.auth.controller;

import com.example.websocket_app_test.request.UserLoginRequest;
import com.example.websocket_app_test.request.UserRegisterRequest;
import com.example.websocket_app_test.auth.service.AuthService;
import com.example.websocket_app_test.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public void login(
            @Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.login(userLoginRequest, request, response);
    }

    @GetMapping("/getSession")
    public ResponseEntity<UserResponse> getSession() {
        log.info("getting session");
        return ResponseEntity.ok(authService.getSession());
    }

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        authService.register(userRegisterRequest, request, response);
    }
}
