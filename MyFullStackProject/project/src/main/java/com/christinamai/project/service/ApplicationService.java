
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
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public ApplicationResponse applyToJob(ApplicationRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByJobAndUser(job, user)) {
            throw new RuntimeException("You have already applied to this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setStatus(Application.Status.PENDING);
        application.setFirstName(request.getFirstName());
        application.setLastName(request.getLastName());
        application.setBirthDate(request.getBirthDate());
        application.setEducation(request.getEducation());
        application.setExperience(request.getExperience());

        Application saved = applicationRepository.save(application);
        logger.info("User {} applied to job {}", username, job.getTitle());

        return mapToResponse(saved);
    }


    public List<ApplicationResponse> getMyApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return applicationRepository.findByUser_Email(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public void deleteMyApplication(Long id, String email) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));


        if (!application.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own applications");
        }

        applicationRepository.delete(application);
        logger.info("User {} deleted application id={}", email, id);
    }


    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return applicationRepository.findByJob(job)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public ApplicationResponse updateStatus(Long id, String status, String username) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(Application.Status.valueOf(status.toUpperCase()));

        Application updated = applicationRepository.save(application);
        logger.info("Admin {} updated application {} status to {}", username, id, status);

        return mapToResponse(updated);
    }

    public List<Application> getApplicationsByUsername(String username) {

        return applicationRepository.findByUser_Email(username);
    }


    private ApplicationResponse mapToResponse(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJob().getTitle(),
                application.getUser().getUsername(),
                application.getStatus(),
                application.getAppliedDate(),
                application.getFirstName(),
                application.getLastName(),
                application.getBirthDate(),
                application.getEducation(),
                application.getExperience()
        );
    }
}