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


@ExtendWith(MockitoExtension.class)
class JobServiceTest {


    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;


    private User adminUser;
    private Job  testJob;
    private JobRequest jobRequest;

    @BeforeEach
    void setUp() {

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("encoded_password");
        adminUser.setRole(User.Role.ROLE_ADMIN);


        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Java Developer");
        testJob.setDescription("Spring Boot developer needed");
        testJob.setLocation("Athens");
        testJob.setSalary(2500.0);
        testJob.setPostedDate(LocalDateTime.now());
        testJob.setPostedBy(adminUser);

        jobRequest = new JobRequest();
        jobRequest.setTitle("Java Developer");
        jobRequest.setDescription("Spring Boot developer needed");
        jobRequest.setLocation("Athens");
        jobRequest.setSalary(2500.0);
    }

    @Test
    void createJob_Success() {

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);


        JobResponse result = jobService.createJob(jobRequest, "admin");


        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals("Athens",         result.getLocation());
        assertEquals(2500.0,           result.getSalary());
        assertEquals("admin",          result.getPostedByUsername());

        verify(jobRepository, times(1)).save(any(Job.class));
    }


    @Test
    void createJob_UserNotFound_ThrowsException() {

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.createJob(jobRequest, "unknown"));

        assertEquals("User not found", ex.getMessage());


        verify(jobRepository, never()).save(any());
    }


    @Test
    void getAllJobs_ReturnsListOfJobs() {

        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Frontend Developer");
        job2.setDescription("React developer needed");
        job2.setLocation("Thessaloniki");
        job2.setSalary(2000.0);
        job2.setPostedDate(LocalDateTime.now());
        job2.setPostedBy(adminUser);

        when(jobRepository.findAll()).thenReturn(List.of(testJob, job2));


        List<JobResponse> result = jobService.getAllJobs();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java Developer",     result.get(0).getTitle());
        assertEquals("Frontend Developer", result.get(1).getTitle());
    }


    @Test
    void getAllJobs_EmptyList() {
        // GIVEN: DB has no jobs
        when(jobRepository.findAll()).thenReturn(List.of());


        List<JobResponse> result = jobService.getAllJobs();


        assertNotNull(result);
        assertEquals(0, result.size());
    }


    @Test
    void getJobById_Success() {

        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        JobResponse result = jobService.getJobById(1L);


        assertNotNull(result);
        assertEquals(1L,               result.getId());
        assertEquals("Java Developer", result.getTitle());
    }


    @Test
    void getJobById_NotFound_ThrowsException() {

        when(jobRepository.findById(99L)).thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.getJobById(99L));

        assertEquals("Job not found", ex.getMessage());
    }


    @Test
    void updateJob_Success() {

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


        JobResponse result = jobService.updateJob(1L, updateRequest, "admin");


        assertNotNull(result);
        assertEquals("Senior Java Developer", result.getTitle());
        assertEquals("Remote",                result.getLocation());
        assertEquals(3500.0,                  result.getSalary());
    }


    @Test
    void deleteJob_Success() {

        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        jobService.deleteJob(1L, "admin");


        verify(jobRepository, times(1)).delete(testJob);
    }

    @Test
    void deleteJob_NotFound_ThrowsException() {

        when(jobRepository.findById(99L)).thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.deleteJob(99L, "admin"));

        assertEquals("Job not found", ex.getMessage());


        verify(jobRepository, never()).delete(any());
    }
}