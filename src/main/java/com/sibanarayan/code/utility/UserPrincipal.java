package com.sibanarayan.code.utility;

import com.sibanarayan.code.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal {
    private UUID userId;
    private String email;
    private String role;
}
