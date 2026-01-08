package com.viet.aodai.user.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
    @Size(max = 100)
    private String fullName;
    @Size(max = 15)
    private String phoneNumber;

    private String address;
    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 100)
    private String confirmPassword;

    public boolean isPasswordMatch(){
        return password != null && password.equals(confirmPassword);
    }

}
