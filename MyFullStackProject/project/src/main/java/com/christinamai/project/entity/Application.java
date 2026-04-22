package com.christinamai.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private LocalDateTime appliedDate = LocalDateTime.now();

    // ← NEW FIELDS
    private String firstName;
    private String lastName;
    private String birthDate;

    @ElementCollection
    @CollectionTable(name = "application_education", joinColumns = @JoinColumn(name = "application_id"))
    @Column(name = "education")
    private List<String> education;

    @ElementCollection
    @CollectionTable(name = "application_experience", joinColumns = @JoinColumn(name = "application_id"))
    @Column(name = "experience")
    private List<String> experience;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}