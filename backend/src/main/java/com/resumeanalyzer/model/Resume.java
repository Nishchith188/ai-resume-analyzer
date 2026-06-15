package com.resumeanalyzer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String candidateName;
    private String email;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    @ElementCollection
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "skill")
    private List<String> skills;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String projects;

    private Integer atsScore;

    @Column(columnDefinition = "TEXT")
    private String skillGapAnalysis;

    @Column(columnDefinition = "TEXT")
    private String aiSuggestions;

    @Column(columnDefinition = "TEXT")
    private String recommendedRoles;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }

    public Integer getAtsScore() { return atsScore; }
    public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }

    public String getSkillGapAnalysis() { return skillGapAnalysis; }
    public void setSkillGapAnalysis(String skillGapAnalysis) { this.skillGapAnalysis = skillGapAnalysis; }

    public String getAiSuggestions() { return aiSuggestions; }
    public void setAiSuggestions(String aiSuggestions) { this.aiSuggestions = aiSuggestions; }

    public String getRecommendedRoles() { return recommendedRoles; }
    public void setRecommendedRoles(String recommendedRoles) { this.recommendedRoles = recommendedRoles; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
