//Δεν την καλείς εσύ χειροκίνητα! Την καλεί το Spring Security αυτόματα κατά τη διαδικασία του Login
// ή όταν το JwtAuthFilter (που είδαμε πριν) προσπαθεί να επιβεβαιώσει τα στοιχεία ενός χρήστη

//Spring Security doesn't know about YOUR User entity!
//Spring Security speaks its own language → UserDetails
//YOUR app speaks your language → User entity
//
//UserDetailsServiceImpl is the TRANSLATOR between them
package com.christinamai.project.security;

import com.christinamai.project.entity.User;
import com.christinamai.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


//interface:Spring Security doesn't know HOW you store users
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    //Ο Logger καταγράφει γεγονότα την ώρα που τρέχει το πρόγραμμα. Είναι ο μόνος τρόπος να ξέρεις τι πήγε στραβά σε έναν server που τρέχει 24/7 χωρίς εσένα από πάνω
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired  //Needs UserRepository to search the databas
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException(
                            "User not found with email: " + email);
                });

        logger.info("User loaded successfully: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
