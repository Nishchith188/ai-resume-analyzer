package com.resumeanalyzer.dto;

import java.util.List;

public class ResumeDTO {

    public static class UploadResponse {
        private Long id;
        private String candidateName;
        private String email;
        private String phone;
        private List<String> skills;
        private String education;
        private String experience;
        private String projects;
        private String message;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String v) { this.candidateName = v; }
        public String getEmail() { return email; }
        public void setEmail(String v) { this.email = v; }
        public String getPhone() { return phone; }
        public void setPhone(String v) { this.phone = v; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> v) { this.skills = v; }
        public String getEducation() { return education; }
        public void setEducation(String v) { this.education = v; }
        public String getExperience() { return experience; }
        public void setExperience(String v) { this.experience = v; }
        public String getProjects() { return projects; }
        public void setProjects(String v) { this.projects = v; }
        public String getMessage() { return message; }
        public void setMessage(String v) { this.message = v; }
    }

    public static class AnalysisRequest {
        private String jobDescription;
        private List<String> requiredSkills;

        public String getJobDescription() { return jobDescription; }
        public void setJobDescription(String v) { this.jobDescription = v; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> v) { this.requiredSkills = v; }
    }

    public static class AnalysisResponse {
        private Long resumeId;
        private Integer atsScore;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private String skillGapAnalysis;
        private String aiSuggestions;
        private List<String> recommendedRoles;

        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long v) { this.resumeId = v; }
        public Integer getAtsScore() { return atsScore; }
        public void setAtsScore(Integer v) { this.atsScore = v; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> v) { this.matchedSkills = v; }
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> v) { this.missingSkills = v; }
        public String getSkillGapAnalysis() { return skillGapAnalysis; }
        public void setSkillGapAnalysis(String v) { this.skillGapAnalysis = v; }
        public String getAiSuggestions() { return aiSuggestions; }
        public void setAiSuggestions(String v) { this.aiSuggestions = v; }
        public List<String> getRecommendedRoles() { return recommendedRoles; }
        public void setRecommendedRoles(List<String> v) { this.recommendedRoles = v; }
    }

    public static class SuggestionsResponse {
        private Long resumeId;
        private String aiSuggestions;
        private List<String> recommendedRoles;
        private List<String> improvementTips;

        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long v) { this.resumeId = v; }
        public String getAiSuggestions() { return aiSuggestions; }
        public void setAiSuggestions(String v) { this.aiSuggestions = v; }
        public List<String> getRecommendedRoles() { return recommendedRoles; }
        public void setRecommendedRoles(List<String> v) { this.recommendedRoles = v; }
        public List<String> getImprovementTips() { return improvementTips; }
        public void setImprovementTips(List<String> v) { this.improvementTips = v; }
    }
}
