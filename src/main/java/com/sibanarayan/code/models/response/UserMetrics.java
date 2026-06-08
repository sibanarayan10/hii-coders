package com.sibanarayan.code.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserMetrics {
private long totalUsers;
private long activeUsers;
private long deactivateUsers;
}
