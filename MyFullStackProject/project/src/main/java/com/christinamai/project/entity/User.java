package com.christinamai.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity  // create a table
@Table(name = "users") //name of table
@Data //automatically setters getters
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank //not be empty
    @Size(min = 3, max = 50) //chars between 3-50
    @Column(unique = true, nullable = false) //no 2 users with same username,no null
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)  // saves the word "ROLE_ADMIN" in the DB, not a number. Spring Security will use this to decide who can access what.
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Application> applications;

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    private List<Job> jobs;
}