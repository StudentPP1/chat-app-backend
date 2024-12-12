package com.example.websocket_app_test.auth.service;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.request.UserLoginRequest;
import com.example.websocket_app_test.request.UserRegisterRequest;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Converter;
import com.example.websocket_app_test.utils.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ChatUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();

    public UserResponse getSession() {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        return Converter.userConvertToResponse(user);
    }

    public void login(UserLoginRequest userLoginRequest,
                      HttpServletRequest request,
                      HttpServletResponse response
    ) {
        // set session cookie in response
        try {
            createSession(
                    request,
                    response,
                    userLoginRequest.getUsername(),
                    userLoginRequest.getPassword()
            );
        } catch (Exception exception) {
            throw ApiException.builder().status(500).message("User not found").build();
        }
    }

    public void register(
            UserRegisterRequest userRegisterRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ChatUser user = new ChatUser();
        user.setName(userRegisterRequest.getName());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user = userRepository.save(user);

        // save user to context
        authenticateUser(user);
        //  set session cookie in response
        createSession(
                request,
                response,
                userRegisterRequest.getUsername(),
                userRegisterRequest.getPassword()
        );
    }

    private void authenticateUser(ChatUser user) {
        log.info("start authentication");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("end authentication");
    }

    private void createSession(
            HttpServletRequest request,
            HttpServletResponse response,
            String username,
            String password
    ) {
        log.info("start creating session");
        var token = UsernamePasswordAuthenticationToken.unauthenticated(
                username,
                password
        );
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolderStrategy holder = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = holder.createEmptyContext();
        log.info("set authentication");
        context.setAuthentication(authentication);
        holder.setContext(context);
        log.info("saving context");
        contextRepository.saveContext(context, request, response);
        log.info("end creating session");
    }
}
