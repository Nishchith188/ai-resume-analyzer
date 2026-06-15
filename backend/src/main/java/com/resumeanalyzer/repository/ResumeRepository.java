package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByCandidateNameContainingIgnoreCase(String name);
    List<Resume> findByAtsScoreGreaterThanEqual(Integer score);
}
