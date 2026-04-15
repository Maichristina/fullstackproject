package com.christinamai.project.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
// request->when a user wants send data to server.transfers data from a form to servre
@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Salary is required")  //not null->int  not blank->String
    @Positive(message = "Salary must be a positive number") //greater of 0
    private Double salary;
}