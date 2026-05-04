package com.christinamai.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    @Column(columnDefinition = "TEXT") // it can store very long text
    private String description;

    private String location;

    private Double salary;

    @Column(nullable = false)
    private LocalDateTime postedDate = LocalDateTime.now();

    // many appliactions 1 user
    @ManyToOne(fetch = FetchType.EAGER) // dont load user data unless i tell you
    @JoinColumn(name = "posted_by_id", nullable = false) //creates a column posted_by_id in the jobs table — this is the foreign key pointing to users.id
    private User postedBy;

    //1 application ,many users
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;
}