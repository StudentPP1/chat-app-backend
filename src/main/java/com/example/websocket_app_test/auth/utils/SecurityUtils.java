package com.example.websocket_app_test.auth.utils;

import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Slf4j
public class SecurityUtils {
    private static final SecurityContextRepository securityRepository = new HttpSessionSecurityContextRepository();
    public static UserDetails getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails user) {
            return user;
        }
        else {
            log.error("User requested but not found in SecurityContextHolder");
            throw ApiException.builder().status(401).message("Authentication required").build();
        }
    }
}
