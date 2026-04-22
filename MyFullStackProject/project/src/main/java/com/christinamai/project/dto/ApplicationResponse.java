package com.christinamai.project.dto;

import com.christinamai.project.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String jobTitle;
    private String applicantUsername;
    private Application.Status status;
    private LocalDateTime appliedDate;

    // ← NEW FIELDS
    private String firstName;
    private String lastName;
    private String birthDate;
    private List<String> education;
    private List<String> experience;
}