package github.studentpp1.chatapp.auth.oauth2;

import github.studentpp1.chatapp.model.User;
import github.studentpp1.chatapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${env.FRONT_END_URL}")
    private String frontendUrl;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User principal = (DefaultOAuth2User) token.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String password = "";
        String username = (String) attributes.getOrDefault("name", "");
        User newUser = new User();
        newUser.setUserName(username);
        newUser.setPassword(password);
        userService.saveUser(newUser);
        HttpSession session = request.getSession();
        session.setAttribute("user_id", newUser.getId());
        getRedirectStrategy().sendRedirect(request, response, frontendUrl);
    }
}
