package com.christinamai.project.service;

import com.christinamai.project.entity.User;
import com.christinamai.project.entity.UserProfile;
import com.christinamai.project.repository.UserProfileRepository;
import com.christinamai.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public UserProfile getProfile(String email) {
        return userProfileRepository.findByUser_Email(email)
                .orElse(new UserProfile()); // return empty if not found
    }

    public UserProfile saveProfile(String email, UserProfile incoming) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update existing or create new
        UserProfile profile = userProfileRepository
                .findByUser_Email(email)
                .orElse(new UserProfile());

        profile.setUser(user);
        profile.setFirstName(incoming.getFirstName());
        profile.setLastName(incoming.getLastName());
        profile.setPhone(incoming.getPhone());
        profile.setCity(incoming.getCity());
        profile.setBirthDate(incoming.getBirthDate());
        profile.setEducation(incoming.getEducation());
        profile.setLanguages(incoming.getLanguages());
        profile.setTechnicalSkills(incoming.getTechnicalSkills());
        profile.setSoftSkills(incoming.getSoftSkills());
        profile.setExperience(incoming.getExperience());
        profile.setAbout(incoming.getAbout());
        profile.setCvName(incoming.getCvName());

        return userProfileRepository.save(profile);
    }
}