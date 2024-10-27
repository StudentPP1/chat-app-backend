package github.studentpp1.chatapp.auth;

import github.studentpp1.chatapp.model.User;
import github.studentpp1.chatapp.request.UserLoginRequestBody;
import github.studentpp1.chatapp.request.UserRegisterRequestBody;
import github.studentpp1.chatapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/registration")
    public void registration(@RequestBody UserRegisterRequestBody requestUser, HttpServletRequest request) {
        User user = new User();
        user.setUserName(requestUser.getUserName());
        user.setFirstName(requestUser.getFirstName());
        user.setLastName(requestUser.getLastName());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        userService.saveUser(user);
        HttpSession session = request.getSession();
        session.setAttribute("user_id", user.getId());
    }

    @PostMapping("/login")
    public void login(@RequestBody UserLoginRequestBody requestUser, HttpServletRequest request) throws UserPrincipalNotFoundException {
        User user = userService.findUserByUserNameAndPassword(
                requestUser.getUserName(),
                passwordEncoder.encode(requestUser.getPassword())
        );
        HttpSession session = request.getSession();
        session.setAttribute("user_id", user.getId());
    }
}
