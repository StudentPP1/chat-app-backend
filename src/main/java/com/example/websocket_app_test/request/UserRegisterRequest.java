package com.example.websocket_app_test.request;

import com.example.websocket_app_test.utils.validation.Unique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UserRegisterRequest {
    @Unique( // check if username is already exists in database
            columnName = "username",
            tableName = "chat_user",
            message = "User with this username already exists")
    private String username;
    private String name;
    @NotNull
    @Length(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "must contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;
}
