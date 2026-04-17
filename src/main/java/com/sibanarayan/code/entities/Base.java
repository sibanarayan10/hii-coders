package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_status", nullable = false, length = 10)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}