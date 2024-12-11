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

import java.util.Optional;

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
        log.info("getting session user info");
        return Converter.userConvertToResponse(user);
    }

    public void login(UserLoginRequest userLoginRequest,
                      HttpServletRequest request,
                      HttpServletResponse response
    ) {
        Optional<ChatUser> userOptional = userRepository.findByUsername(
                userLoginRequest.getUsername()
        );
        boolean passwordMatch = passwordEncoder.matches(
                userLoginRequest.getPassword(),
                userOptional.orElseThrow(
                        () -> new ApiException("user not found", 422)
                ).getPassword()
        );
        // check is user in context & get session id to cookie
        if (passwordMatch) {
            createSession(
                    request,
                    response,
                    userLoginRequest.getUsername(),
                    userLoginRequest.getPassword()
            );
        }
        else {
            throw new ApiException("user not found", 422);
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
        // check is user in context & get session id to cookie
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
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("end authentication");
    }

    private void createSession(
            HttpServletRequest request,
            HttpServletResponse response,
            String username,
            String password) {
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
