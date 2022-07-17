package com.snji.storage.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findTopByJobKeyOrderByJobValueDesc(String jobKey);
}