package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.UserDetailProvider;
import com.sibanarayan.shared_package.enums.UserRole;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private UUID id;
    private String email;
    private String name;
    private Long phone;
    private String password;
    private UserRole role=UserRole.USER;
    private UserDetailProvider userDetailProvider=UserDetailProvider.MANUAL;
}
