package github.studentpp1.chatapp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegisterRequestBody {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
}
