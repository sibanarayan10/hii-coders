package com.sibanarayan.code.entities;


import com.sibanarayan.code.enums.SolveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name="user_problem_engagement")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserProblemEngagement extends Base {
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "is_liked", nullable = false)
    private boolean liked = false;

    @Column(name = "is_saved", nullable = false)
    private boolean saved = false;

    @Column(name = "is_favorite", nullable = false)
    private boolean favorite = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "solve_status", nullable = false, length = 15)
    private SolveStatus solveStatus = SolveStatus.UNSOLVED;

}
