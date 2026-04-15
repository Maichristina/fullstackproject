package com.christinamai.project.repository;

import com.christinamai.project.entity.Application;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUser(User user); //list-->return 0-many results

    List<Application> findByJob(Job job);

    Optional<Application> findByJobAndUser(Job job, User user);//Job table and name it job

    boolean existsByJobAndUser(Job job, User user);
}