package com.christinamai.project.service;

import com.christinamai.project.dto.ApplicationRequest;
import com.christinamai.project.dto.ApplicationResponse;
import com.christinamai.project.entity.Application;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.ApplicationRepository;
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
class ApplicationServiceTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private JobRepository         jobRepository;
    @Mock private UserRepository        userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User               testUser;
    private User               adminUser;
    private Job                testJob;
    private Application        testApplication;
    private ApplicationRequest applicationRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setEmail("john@test.com");
        testUser.setPassword("encoded");
        testUser.setRole(User.Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("encoded");
        adminUser.setRole(User.Role.ROLE_ADMIN);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Java Developer");
        testJob.setDescription("Spring Boot job");
        testJob.setLocation("Athens");
        testJob.setSalary(2500.0);
        testJob.setPostedDate(LocalDateTime.now());
        testJob.setPostedBy(adminUser);

        testApplication = new Application();
        testApplication.setId(1L);
        testApplication.setJob(testJob);
        testApplication.setUser(testUser);
        testApplication.setStatus(Application.Status.PENDING);
        testApplication.setAppliedDate(LocalDateTime.now());

        applicationRequest = new ApplicationRequest();
        applicationRequest.setJobId(1L);
    }

    // ─────────────────────────────────────────────
    // TEST 1: Apply to job successfully
    // ─────────────────────────────────────────────
    @Test
    void applyToJob_Success() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(testJob));
        when(applicationRepository.existsByJobAndUser(testJob, testUser))
                .thenReturn(false);
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(testApplication);

        ApplicationResponse result =
                applicationService.applyToJob(applicationRequest, "john");

        assertNotNull(result);
        assertEquals("Java Developer",           result.getJobTitle());
        assertEquals("john",                     result.getApplicantUsername());
        assertEquals(Application.Status.PENDING, result.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    // ─────────────────────────────────────────────
    // TEST 2: Apply twice — already applied
    // ─────────────────────────────────────────────
    @Test
    void applyToJob_AlreadyApplied_ThrowsException() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(testJob));
        when(applicationRepository.existsByJobAndUser(testJob, testUser))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.applyToJob(applicationRequest, "john"));

        assertEquals("You have already applied to this job", ex.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 3: Apply — job not found
    // ─────────────────────────────────────────────
    @Test
    void applyToJob_JobNotFound_ThrowsException() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(jobRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.applyToJob(applicationRequest, "john"));

        assertEquals("Job not found", ex.getMessage());
    }

    // ─────────────────────────────────────────────
    // TEST 4: Apply — user not found
    // ─────────────────────────────────────────────
    @Test
    void applyToJob_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.applyToJob(applicationRequest, "unknown"));

        assertEquals("User not found", ex.getMessage());
    }

    // ─────────────────────────────────────────────
    // TEST 5: Get my applications — returns list
    // ✅ FIXED: findByUser(user) — matches ApplicationService.java
    // ─────────────────────────────────────────────
    @Test
    void getMyApplications_ReturnsUserApplications() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUser_Username("john"))
                .thenReturn(List.of(testApplication));

        List<ApplicationResponse> result =
                applicationService.getMyApplications("john");

        assertNotNull(result);
        assertEquals(1,                result.size());
        assertEquals("Java Developer", result.get(0).getJobTitle());
        assertEquals("john",           result.get(0).getApplicantUsername());
    }

    // ─────────────────────────────────────────────
    // TEST 6: Get my applications — empty
    // ✅ FIXED: findByUser(user) — matches ApplicationService.java
    // ─────────────────────────────────────────────
    @Test
    void getMyApplications_Empty() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUser_Username("john"))
                .thenReturn(List.of());

        List<ApplicationResponse> result =
                applicationService.getMyApplications("john");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ─────────────────────────────────────────────
    // TEST 7: Delete my application successfully
    // ─────────────────────────────────────────────
    @Test
    void deleteMyApplication_Success() {
        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(testApplication));

        applicationService.deleteMyApplication(1L, "john");

        verify(applicationRepository, times(1)).delete(testApplication);
    }

    // ─────────────────────────────────────────────
    // TEST 8: Delete someone else's application — denied
    // ─────────────────────────────────────────────
    @Test
    void deleteMyApplication_NotOwner_ThrowsException() {
        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(testApplication));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.deleteMyApplication(1L, "maria"));

        assertEquals("You can only delete your own applications",
                ex.getMessage());
        verify(applicationRepository, never()).delete(any());
    }

    // ─────────────────────────────────────────────
    // TEST 9: Delete — application not found
    // ─────────────────────────────────────────────
    @Test
    void deleteMyApplication_NotFound_ThrowsException() {
        when(applicationRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> applicationService.deleteMyApplication(99L, "john"));

        assertEquals("Application not found", ex.getMessage());
        verify(applicationRepository, never()).delete(any());
    }

    // ─────────────────────────────────────────────
    // TEST 10: Admin — get all applications
    // ─────────────────────────────────────────────
    @Test
    void getAllApplications_ReturnsAll() {
        Application app2 = new Application();
        app2.setId(2L);
        app2.setJob(testJob);
        app2.setUser(adminUser);
        app2.setStatus(Application.Status.ACCEPTED);
        app2.setAppliedDate(LocalDateTime.now());

        when(applicationRepository.findAll())
                .thenReturn(List.of(testApplication, app2));

        List<ApplicationResponse> result =
                applicationService.getAllApplications();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ─────────────────────────────────────────────
    // TEST 11: Admin — update status to ACCEPTED
    // ─────────────────────────────────────────────
    @Test
    void updateStatus_ToAccepted_Success() {
        Application acceptedApp = new Application();
        acceptedApp.setId(1L);
        acceptedApp.setJob(testJob);
        acceptedApp.setUser(testUser);
        acceptedApp.setStatus(Application.Status.ACCEPTED);
        acceptedApp.setAppliedDate(LocalDateTime.now());

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(acceptedApp);

        ApplicationResponse result =
                applicationService.updateStatus(1L, "ACCEPTED", "admin");

        assertNotNull(result);
        assertEquals(Application.Status.ACCEPTED, result.getStatus());
        verify(applicationRepository, times(1)).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 12: Admin — update status to REJECTED
    // ─────────────────────────────────────────────
    @Test
    void updateStatus_ToRejected_Success() {
        Application rejectedApp = new Application();
        rejectedApp.setId(1L);
        rejectedApp.setJob(testJob);
        rejectedApp.setUser(testUser);
        rejectedApp.setStatus(Application.Status.REJECTED);
        rejectedApp.setAppliedDate(LocalDateTime.now());

        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(rejectedApp);

        ApplicationResponse result =
                applicationService.updateStatus(1L, "REJECTED", "admin");

        assertEquals(Application.Status.REJECTED, result.getStatus());
    }

    // ─────────────────────────────────────────────
    // TEST 13: Admin — invalid status throws exception
    // ─────────────────────────────────────────────
    @Test
    void updateStatus_InvalidStatus_ThrowsException() {
        when(applicationRepository.findById(1L))
                .thenReturn(Optional.of(testApplication));

        // "BANANA" is not PENDING/ACCEPTED/REJECTED → throws!
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.updateStatus(1L, "BANANA", "admin"));
    }
}