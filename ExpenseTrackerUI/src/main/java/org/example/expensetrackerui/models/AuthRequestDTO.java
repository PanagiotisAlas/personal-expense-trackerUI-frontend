package org.example.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String fullName;
    private String username;
    private String password;
}
