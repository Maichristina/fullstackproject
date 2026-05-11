package com.christinamai.project.repository;

import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedBy(User postedBy);


}