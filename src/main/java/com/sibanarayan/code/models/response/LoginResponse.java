package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private UUID id;
    private String email;
    private String name;
    private UserRole role;
}
