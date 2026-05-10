package com.sibanarayan.code.utility;

import java.util.UUID;

public class UserPrincipal {
    public UserPrincipal(String email,UUID userId){
        this.userId=userId;
        this.email=email;
    }
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private UUID userId;
    private String email;

}
