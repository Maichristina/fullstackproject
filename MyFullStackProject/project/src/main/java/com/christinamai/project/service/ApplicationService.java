//Service     → does ALL the thinking/logiService
package com.christinamai.project.service;

import com.christinamai.project.dto.ApplicationRequest;
import com.christinamai.project.dto.ApplicationResponse;
import com.christinamai.project.entity.Application;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.ApplicationRepository;
import com.christinamai.project.repository.JobRepository;
import com.christinamai.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository; //save/find/delete applications

    @Autowired
    private JobRepository jobRepository; //find the job being applied to

    @Autowired
    private UserRepository userRepository; //find the user who is applying

    // USER — apply to a job
    public ApplicationResponse applyToJob(ApplicationRequest request, String username) {
        //Find the User in DB by username not found? → throw exception → 404 error
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check if user already applied to this job
        if (applicationRepository.existsByJobAndUser(job, user)) {
            throw new RuntimeException("You have already applied to this job");
        }

        Application application = new Application(); // empty object
        application.setJob(job); // connect to the job
        application.setUser(user); // connect to the user
        application.setStatus(Application.Status.PENDING); //starts as PENDING always

        Application saved = applicationRepository.save(application);
        logger.info("User {} applied to job {}", username, job.getTitle());

        return mapToResponse(saved); //Μετατρέπει την οντότητα σε Response DTO για να τη δει ο χρήστης
    }

    // USER — get their own applications
    public List<ApplicationResponse> getMyApplications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return applicationRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // USER — delete their own application.Πριν διαγράψει, ελέγχει αν το username της αίτησης ταιριάζει με το username αυτού που ζητάει τη διαγραφή.
    // Έτσι, ο Χρήστης Α δεν μπορεί να διαγράψει την αίτηση του Χρήστη Β
    public void deleteMyApplication(Long id, String username) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Make sure the application belongs to this user
        if (!application.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own applications");
        }

        applicationRepository.delete(application);
        logger.info("User {} deleted application id={}", username, id);
    }

    // ADMIN — get all applications
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ADMIN — get all applications for a specific job
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return applicationRepository.findByJob(job)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ADMIN — update application status
    public ApplicationResponse updateStatus(Long id, String status, String username) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(Application.Status.valueOf(status.toUpperCase()));

        Application updated = applicationRepository.save(application);
        logger.info("Admin {} updated application {} status to {}", username, id, status);

        return mapToResponse(updated);
    }

    // Converts Application entity → ApplicationResponse DTO
    private ApplicationResponse mapToResponse(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getTitle(),
                application.getUser().getUsername(),
                application.getStatus(),
                application.getAppliedDate()
        );
    }
}