package com.example.websocket_app_test.auth.service;

import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ChatUserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> ApiException.builder()
                                .status(500)
                                .message("Cannot find user with username: " + username)
                                .build()
                );
    }
}
