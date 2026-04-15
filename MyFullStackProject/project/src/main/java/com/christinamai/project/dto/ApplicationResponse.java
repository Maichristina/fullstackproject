package com.christinamai.project.dto;

import com.christinamai.project.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String jobTitle;
    private String applicantUsername;
    private Application.Status status;
    private LocalDateTime appliedDate;
}