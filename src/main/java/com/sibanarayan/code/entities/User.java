package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends  Base {

    private String email;

    private String name;

    @Column(name="phone_number")
    private String phone;

    private String password;

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
