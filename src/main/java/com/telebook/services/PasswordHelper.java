package com.telebook.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class PasswordHelper {

    @NotBlank(message = "This field can't be empty!!")
    private String oldPassword;

    @NotBlank(message = "This field can't be empty!!")
    private String newPassword;

    @NotBlank(message = "This field can't be empty!!")
    private String rePassword;
}
