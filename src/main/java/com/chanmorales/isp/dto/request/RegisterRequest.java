package com.chanmorales.isp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "username is required") @Size(
            min = 3,
            max = 30,
            message = "username should at least be 3 characters and not exceed 30 characters")
        String username,
    @Email(message = "email should be a valid email format") String email,
    @NotBlank(message = "password is required") @Size(min = 8, message = "password should be at least 8 characters ") String password) {}
