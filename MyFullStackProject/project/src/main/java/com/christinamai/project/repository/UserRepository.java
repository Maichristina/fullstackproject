package com.christinamai.project.repository;

import com.christinamai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; //return a value or nothing
// code talks to the users table

@Repository
//By extending JpaRepository you get free methods instantly:
//
//save(user) → INSERT or UPDATE
//findById(id) → SELECT WHERE id=?
//findAll() → SELECT *
//deleteById(id) → DELETE WHERE id=?
public interface UserRepository extends JpaRepository<User, Long> {  //the table we want and the type of id

    Optional<User> findByUsername(String username); //SELECT * FROM users WHERE username = ?

    Optional<User> findByEmail(String email);//optional-->safety no crush if no user

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}