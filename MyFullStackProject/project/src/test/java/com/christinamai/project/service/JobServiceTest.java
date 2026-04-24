package com.christinamai.project.service;

import com.christinamai.project.dto.JobRequest;
import com.christinamai.project.dto.JobResponse;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.JobRepository;
import com.christinamai.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith → tells JUnit to use Mockito
// No real database needed — Mockito fakes everything!
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    // @Mock → fake version of the repository (no real DB)
    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    // @InjectMocks → creates real JobService but injects the fake repos
    @InjectMocks
    private JobService jobService;

    // Test data — reused across tests
    private User adminUser;
    private Job  testJob;
    private JobRequest jobRequest;

    // @BeforeEach → runs before EVERY test
    // Sets up fresh test data each time
    @BeforeEach
    void setUp() {
        // Create a fake admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("encoded_password");
        adminUser.setRole(User.Role.ROLE_ADMIN);

        // Create a fake job
        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Java Developer");
        testJob.setDescription("Spring Boot developer needed");
        testJob.setLocation("Athens");
        testJob.setSalary(2500.0);
        testJob.setPostedDate(LocalDateTime.now());
        testJob.setPostedBy(adminUser);

        // Create a fake job request (what admin sends)
        jobRequest = new JobRequest();
        jobRequest.setTitle("Java Developer");
        jobRequest.setDescription("Spring Boot developer needed");
        jobRequest.setLocation("Athens");
        jobRequest.setSalary(2500.0);
    }

    // ─────────────────────────────────────────────
    // TEST 1: Create a job successfully
    // ─────────────────────────────────────────────
    @Test
    void createJob_Success() {
        // GIVEN: admin exists in DB, save returns the job
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        // WHEN: admin creates a job
        JobResponse result = jobService.createJob(jobRequest, "admin");

        // THEN: result is not null and has correct title
        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals("Athens",         result.getLocation());
        assertEquals(2500.0,           result.getSalary());
        assertEquals("admin",          result.getPostedByUsername());

        // Verify save was called exactly once
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    // ─────────────────────────────────────────────
    // TEST 2: Create job — admin user not found
    // ─────────────────────────────────────────────
    @Test
    void createJob_UserNotFound_ThrowsException() {
        // GIVEN: no user found in DB
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // WHEN + THEN: expect RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.createJob(jobRequest, "unknown"));

        assertEquals("User not found", ex.getMessage());

        // Verify save was NEVER called
        verify(jobRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 3: Get all jobs
    // ─────────────────────────────────────────────
    @Test
    void getAllJobs_ReturnsListOfJobs() {
        // GIVEN: DB has 2 jobs
        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Frontend Developer");
        job2.setDescription("React developer needed");
        job2.setLocation("Thessaloniki");
        job2.setSalary(2000.0);
        job2.setPostedDate(LocalDateTime.now());
        job2.setPostedBy(adminUser);

        when(jobRepository.findAll()).thenReturn(List.of(testJob, job2));

        // WHEN: get all jobs
        List<JobResponse> result = jobService.getAllJobs();

        // THEN: returns 2 jobs
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java Developer",     result.get(0).getTitle());
        assertEquals("Frontend Developer", result.get(1).getTitle());
    }

    // ─────────────────────────────────────────────
    // TEST 4: Get all jobs — empty list
    // ─────────────────────────────────────────────
    @Test
    void getAllJobs_EmptyList() {
        // GIVEN: DB has no jobs
        when(jobRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<JobResponse> result = jobService.getAllJobs();

        // THEN: empty list, no crash
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ─────────────────────────────────────────────
    // TEST 5: Get job by ID — found
    // ─────────────────────────────────────────────
    @Test
    void getJobById_Success() {
        // GIVEN: job with id=1 exists
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        // WHEN
        JobResponse result = jobService.getJobById(1L);

        // THEN
        assertNotNull(result);
        assertEquals(1L,               result.getId());
        assertEquals("Java Developer", result.getTitle());
    }

    // ─────────────────────────────────────────────
    // TEST 6: Get job by ID — not found
    // ─────────────────────────────────────────────
    @Test
    void getJobById_NotFound_ThrowsException() {
        // GIVEN: no job with id=99
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.getJobById(99L));

        assertEquals("Job not found", ex.getMessage());
    }

    // ─────────────────────────────────────────────
    // TEST 7: Update job successfully
    // ─────────────────────────────────────────────
    @Test
    void updateJob_Success() {
        // GIVEN: job exists, save returns updated job
        JobRequest updateRequest = new JobRequest();
        updateRequest.setTitle("Senior Java Developer");
        updateRequest.setDescription("Updated description");
        updateRequest.setLocation("Remote");
        updateRequest.setSalary(3500.0);

        Job updatedJob = new Job();
        updatedJob.setId(1L);
        updatedJob.setTitle("Senior Java Developer");
        updatedJob.setDescription("Updated description");
        updatedJob.setLocation("Remote");
        updatedJob.setSalary(3500.0);
        updatedJob.setPostedDate(LocalDateTime.now());
        updatedJob.setPostedBy(adminUser);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(updatedJob);

        // WHEN
        JobResponse result = jobService.updateJob(1L, updateRequest, "admin");

        // THEN
        assertNotNull(result);
        assertEquals("Senior Java Developer", result.getTitle());
        assertEquals("Remote",                result.getLocation());
        assertEquals(3500.0,                  result.getSalary());
    }

    // ─────────────────────────────────────────────
    // TEST 8: Delete job successfully
    // ─────────────────────────────────────────────
    @Test
    void deleteJob_Success() {
        // GIVEN: job exists
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        // WHEN
        jobService.deleteJob(1L, "admin");

        // THEN: delete was called exactly once
        verify(jobRepository, times(1)).delete(testJob);
    }

    // ─────────────────────────────────────────────
    // TEST 9: Delete job — not found
    // ─────────────────────────────────────────────
    @Test
    void deleteJob_NotFound_ThrowsException() {
        // GIVEN: no job with id=99
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.deleteJob(99L, "admin"));

        assertEquals("Job not found", ex.getMessage());

        // Verify delete was NEVER called
        verify(jobRepository, never()).delete(any());
    }
}