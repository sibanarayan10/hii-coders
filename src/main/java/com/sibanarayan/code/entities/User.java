package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.UserDetailProvider;
import com.sibanarayan.shared_package.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @ElementCollection(targetClass = UserDetailProvider.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_detail_provider",
            joinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    @Column(name = "user_detail_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserDetailProvider> userDetailProvider = new HashSet<>();
}
