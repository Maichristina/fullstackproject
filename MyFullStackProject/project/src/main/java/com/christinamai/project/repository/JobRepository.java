package com.christinamai.project.repository;

import com.christinamai.project.entity.Job;
import com.christinamai.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository; //ives inside the Spring Framework librarywas downloaded into your project via pom.xml (Maven). It's not YOUR code — it's Spring's code that lives in your project's dependencies folder.
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedBy(User postedBy);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);
}