package com.example.websocket_app_test.auth.controller;

import com.example.websocket_app_test.utils.application.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class CsrfController {
    private final ApplicationProperties applicationProperties;
    private final Logger LOGGER = LoggerFactory.getLogger(CsrfController.class);

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        LOGGER.info("CSRF: get request");
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(applicationProperties.getCsrfAttribute());
        if (csrfToken == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CSRF Token not found");
        }
        LOGGER.info("CSRF: send token");
        return csrfToken;
    }
}
