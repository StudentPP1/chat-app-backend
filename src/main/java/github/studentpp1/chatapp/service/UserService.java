package github.studentpp1.chatapp.service;

import github.studentpp1.chatapp.model.User;
import github.studentpp1.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUser(Long userId) throws UserPrincipalNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UserPrincipalNotFoundException("user not found"));
    }

    public User findUserByUserNameAndPassword(String userName, String password) throws UserPrincipalNotFoundException {
        return userRepository.findByUserNameAndPassword(userName, password).orElseThrow(() -> new UserPrincipalNotFoundException("user not found"));
    }
}
