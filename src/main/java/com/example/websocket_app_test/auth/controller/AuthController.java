package com.example.websocket_app_test.auth.controller;

import com.example.websocket_app_test.request.UserLoginRequest;
import com.example.websocket_app_test.request.UserRegisterRequest;
import com.example.websocket_app_test.auth.service.AuthService;
import com.example.websocket_app_test.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public void login(
            @Valid @RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.login(userLoginRequest, request, response);
    }

    @GetMapping("/getSession")
    public ResponseEntity<UserResponse> getSession() {
        return ResponseEntity.ok(authService.getSession());
    }

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.register(userRegisterRequest, request, response);
    }
}
