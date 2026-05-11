package com.christinamai.project.repository;

import com.christinamai.project.entity.Application;
import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUser_Email(String email);

    List<Application> findByJob(Job job);

    boolean existsByJobAndUser(Job job, User user);
}