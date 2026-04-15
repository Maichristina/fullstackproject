//διαχειρίζεται όλη τη λογική γύρω από τις αγγελίες εργασίας.δημιουργία και η τροποποίηση των θέσεων εργασίας
package com.christinamai.project.service;

import com.christinamai.project.dto.JobRequest;
import com.christinamai.project.dto.JobResponse;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.JobRepository;
import com.christinamai.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    // ADMIN — create a new job
    public JobResponse createJob(JobRequest request, String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setPostedBy(admin);

        Job saved = jobRepository.save(job);
        logger.info("Job created: {} by {}", saved.getTitle(), username);

        return mapToResponse(saved);
    }

    // EVERYONE — get all jobs
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToResponse) //ο :: είναι ένας σύντομος τρόπος να πεις: "Για κάθε στοιχείο που περνάει από το stream, κάλεσε τη μέθοδο mapToResponse της τρέχουσας κλάσης
                .collect(Collectors.toList());
    }

    // EVERYONE — get one job by id
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToResponse(job);
    }

    // ADMIN — update a job
    public JobResponse updateJob(Long id, JobRequest request, String username) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());

        Job updated = jobRepository.save(job);
        logger.info("Job updated: {} by {}", updated.getTitle(), username);

        return mapToResponse(updated);
    }

    // ADMIN — delete a job
    public void deleteJob(Long id, String username) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        jobRepository.delete(job);
        logger.info("Job deleted: id={} by {}", id, username);
    }

    // ADMIN — get jobs posted by a specific admin
    public List<JobResponse> getJobsByAdmin(String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobRepository.findByPostedBy(admin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Converts Job entity → JobResponse DTO
    private JobResponse mapToResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getSalary(),
                job.getPostedDate(),
                job.getPostedBy().getUsername()
        );
    }
}