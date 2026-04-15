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

import java.util.List;



@RestController //κλάση θα δέχεται HTTP αιτήματα και θα επιστρέφει δεδομένα (συνήθως JSON
@RequestMapping("/api/applications") //τα αιτήματα σε αυτόν τον Controller θα ξεκινούν με αυτό το πρόθεμα
@CrossOrigin(origins = "*") //Επιτρέπει σε frontend εφαρμογές να "μιλάνε" με αυτό το API
public class ApplicationController {





    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }


    @Autowired
    private ApplicationService applicationService;

    // POST /api/applications — user applies to a job
    @PostMapping //Χρησιμοποιείται για δημιουργία νέας εγγραφής.
    @PreAuthorize("hasAuthority('ROLE_USER')") //το Spring Security ελέγχει αν ο χρήστης που έστειλε το JWT έχει όντως ρόλο USER
    public ResponseEntity<ApplicationResponse> applyToJob(
            @Valid @RequestBody ApplicationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.applyToJob(request, authentication.getName()));
    }

    // GET /api/applications/my — user sees their own applications
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(authentication.getName()));
    }

    // DELETE /api/applications/{id} — user deletes their own application
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteMyApplication(
            @PathVariable Long id,
            Authentication authentication) {
        applicationService.deleteMyApplication(id, authentication.getName());
        return ResponseEntity.ok("Application deleted successfully");
    }

    // GET /api/applications — admin sees all applications
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // GET /api/applications/job/{jobId} — admin sees applications for a job
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }

    // PUT /api/applications/{id}/status — admin updates status
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