package com.sibanarayan.code.models.request;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String name;
    private Long phone;
    private String password;
}
