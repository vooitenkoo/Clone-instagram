package com.example.cloneInstragram.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {

    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Use only Latin letters.")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

}
