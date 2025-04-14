package com.kunal.spring_security.dto;

import com.kunal.spring_security.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String role;
    private String email;

    public UserDTO(UserModel userModel) {
        this.id = userModel.getId();
        this.username = userModel.getUsername();
        this.role = userModel.getRole();
        this.password = userModel.getPassword();
        this.email = userModel.getEmail();
    }
}
