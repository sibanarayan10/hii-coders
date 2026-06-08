package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.enums.UserRole;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Builder
@Setter
@Getter
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private UserRole role;
    private RecordStatus recordStatus;
    private Long acceptedCount;
    private Instant createdAt;

}
