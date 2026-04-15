//«διαχειριστής» των αγγελιών εργασίας.Admins έχουν πλήρη έλεγχο (δημιουργία, αλλαγή, διαγραφή),
// ενώ οι απλοί χρήστες (και οι Admins) μπορούν μόνο να διαβάσουν τις αγγελίες
//Το Αίτημα: Ένας Admin στέλνει ένα PUT στο /api/jobs/10.
//
//Το Φίλτρο: Το JwtAuthFilter (που είδαμε πριν) διαβάζει το token, βλέπει ότι είναι Admin και τον αφήνει να φτάσει στον Controller.
//
//Ο Controller: Η μέθοδος updateJob δέχεται το αίτημα, ελέγχει αν το JSON είναι έγκυρο και καλεί τον jobService.updateJob.
//
//Ο Service: Ο Service μιλάει με τη βάση, κάνει την αλλαγή και επιστρέφει το νέο αποτέλεσμα.
//
//Η Απάντηση: Ο Controller στέλνει πίσω το JobResponse (DTO) στον Admin με status 200 OK.
package com.christinamai.project.controller;

import com.christinamai.project.dto.JobRequest;
import com.christinamai.project.dto.JobResponse;
import com.christinamai.project.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobService jobService;

    // POST /api/jobs — admin only
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                jobService.createJob(request, authentication.getName()));
    }

    // GET /api/jobs — everyone with token
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/{id} — everyone with token
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // PUT /api/jobs/{id} — admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                jobService.updateJob(id, request, authentication.getName()));
    }

    // DELETE /api/jobs/{id} — admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {
        jobService.deleteJob(id, authentication.getName());
        return ResponseEntity.ok("Job deleted successfully");
    }
}