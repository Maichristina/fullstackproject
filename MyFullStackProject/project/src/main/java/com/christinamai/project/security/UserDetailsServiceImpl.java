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
    //Όταν κάποιος πάει να κάνει login, το Spring Security του δίνει ένα username.
    //userRepository.findByUsername(username): Πηγαίνει στη βάση δεδομένων και ψάχνει αν υπάρχει αυτός ο χρήστης
    //orElseThrow: Αν δεν τον βρει, "πετάει" μια εξαίρεση UsernameNotFoundException. Πριν την πετάξει, ο Logger καταγράφει το σφάλμα (logger.error)
    // για να ξέρεις εσύ ότι κάποιος προσπάθησε να μπει με ανύπαρκτο όνομα.
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Step 1: Search the database for the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException(
                            "User not found with username: " + username);
                });

        logger.info("User loaded successfully: {}", username);

        // Step 2: Convert our User entity into a Spring Security UserDetails object
        //Αν ο χρήστης βρεθεί Δίνουμε το όνομα. Δίνουμε το κρυπτογραφημένο password Εδώ μετατρέπουμε το Role που έχεις στη βάση (π.χ. ADMIN) σε "Δικαίωμα" (Authority)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))  //role->enum  .name->make string
        );
    }
}
