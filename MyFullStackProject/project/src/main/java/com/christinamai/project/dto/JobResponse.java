package com.christinamai.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
//when server returns data to user
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private LocalDateTime postedDate;
    private String postedByUsername;
}