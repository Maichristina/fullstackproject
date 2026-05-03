package com.christinamai.project.controller;

import com.christinamai.project.entity.UserProfile;
import com.christinamai.project.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // GET /api/profile — load my profile
    @GetMapping
    public ResponseEntity<UserProfile> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(
                userProfileService.getProfile(authentication.getName())
        );
    }

    // PUT /api/profile — save my profile
    @PutMapping
    public ResponseEntity<UserProfile> saveMyProfile(
            @RequestBody UserProfile profile,
            Authentication authentication) {
        return ResponseEntity.ok(
                userProfileService.saveProfile(authentication.getName(), profile)
        );
    }
}