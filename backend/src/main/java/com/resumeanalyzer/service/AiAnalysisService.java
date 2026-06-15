package com.resumeanalyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiAnalysisService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calculates ATS score by comparing resume skills to job requirements.
     * Returns a score from 0-100.
     */
    public int calculateAtsScore(List<String> resumeSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 0;

        long matched = resumeSkills.stream()
            .filter(skill -> requiredSkills.stream()
                .anyMatch(req -> req.equalsIgnoreCase(skill)))
            .count();

        return (int) ((matched * 100.0) / requiredSkills.size());
    }

    /**
     * Identifies missing skills between resume and job description.
     */
    public List<String> getMissingSkills(List<String> resumeSkills, List<String> requiredSkills) {
        List<String> missing = new ArrayList<>();
        for (String required : requiredSkills) {
            boolean found = resumeSkills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(required));
            if (!found) missing.add(required);
        }
        return missing;
    }

    /**
     * Calls Gemini API to generate skill gap analysis.
     */
    public String generateSkillGapAnalysis(String resumeText, String jobDescription) {
        String prompt = String.format("""
            Analyze the following resume against the job description and provide:
            1. Key skill gaps
            2. Areas of strength
            3. Specific recommendations to bridge the gaps
            
            Resume:
            %s
            
            Job Description:
            %s
            
            Provide a concise, structured analysis in 200 words or less.
            """, resumeText, jobDescription);

        return callGeminiApi(prompt);
    }

    /**
     * Calls Gemini API to generate resume improvement suggestions.
     */
    public String generateResumeSuggestions(String resumeText) {
        String prompt = String.format("""
            Review this resume and provide 5 specific, actionable improvement suggestions.
            Focus on: quantifying achievements, stronger action verbs, formatting, 
            keyword optimization, and missing sections.
            
            Resume:
            %s
            
            Format as a numbered list. Be concise and specific.
            """, resumeText);

        return callGeminiApi(prompt);
    }

    /**
     * Calls Gemini API to recommend job roles.
     */
    public String recommendJobRoles(List<String> skills, String experience) {
        String prompt = String.format("""
            Based on these skills: %s
            And this experience: %s
            
            Recommend 5 suitable job roles with:
            - Role title
            - Why it's a good fit (1 sentence)
            - Average salary range (USD)
            
            Format as JSON array with fields: title, reason, salaryRange
            """, String.join(", ", skills), experience);

        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();

            part.put("text", prompt);
            content.put("parts", List.of(part));
            requestBody.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                GEMINI_URL + geminiApiKey,
                entity,
                Map.class
            );

            // Extract text from Gemini response
            if (response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map contentMap = (Map) candidate.get("content");
                    List parts = (List) contentMap.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            return "AI analysis unavailable: " + e.getMessage();
        }
        return "No response from AI";
    }
}
