package com.christinamai.project.controller;

import com.christinamai.project.dto.ApplicationRequest;
import com.christinamai.project.dto.ApplicationResponse;
import com.christinamai.project.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {





    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }


    @Autowired
    private ApplicationService applicationService;


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApplicationResponse> applyToJob(
            @Valid @RequestBody ApplicationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.applyToJob(request, authentication.getName()));
    }




    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteMyApplication(
            @PathVariable Long id,
            Authentication authentication) {
        applicationService.deleteMyApplication(id, authentication.getName());
        return ResponseEntity.ok("Application deleted successfully");
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(Principal principal) {
        String username = principal.getName();
        List<ApplicationResponse> myApps = applicationService.getMyApplications(username);
        return ResponseEntity.ok(myApps);
    }


    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }


    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.updateStatus(id, status, authentication.getName()));
    }
}