package com.resumeanalyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiAnalysisService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    public int calculateAtsScore(List<String> resumeSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 0;
        long matched = resumeSkills.stream()
            .filter(skill -> requiredSkills.stream()
                .anyMatch(req -> req.equalsIgnoreCase(skill)))
            .count();
        return (int) ((matched * 100.0) / requiredSkills.size());
    }

    public List<String> getMissingSkills(List<String> resumeSkills, List<String> requiredSkills) {
        List<String> missing = new ArrayList<>();
        for (String required : requiredSkills) {
            boolean found = resumeSkills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(required));
            if (!found) missing.add(required);
        }
        return missing;
    }

    public String generateSkillGapAnalysis(String resumeText, String jobDescription) {
        String prompt = String.format(
            "Analyze the following resume against the job description and provide:\n" +
            "1. Key skill gaps\n2. Areas of strength\n3. Specific recommendations\n\n" +
            "Resume:\n%s\n\nJob Description:\n%s\n\n" +
            "Provide a concise structured analysis in 200 words or less.",
            resumeText, jobDescription);
        return callGroqApi(prompt);
    }

    public String generateResumeSuggestions(String resumeText) {
        String prompt = String.format(
            "Review this resume and provide 5 specific, actionable improvement suggestions.\n" +
            "Focus on: quantifying achievements, stronger action verbs, formatting,\n" +
            "keyword optimization, and missing sections.\n\nResume:\n%s\n\n" +
            "Format as a numbered list. Be concise and specific.", resumeText);
        return callGroqApi(prompt);
    }

    public String recommendJobRoles(List<String> skills, String experience) {
        String prompt = String.format(
            "Based on these skills: %s\nAnd this experience: %s\n\n" +
            "Recommend 5 suitable job roles with role title and why it's a good fit (1 sentence each).\n" +
            "Format as a numbered list.",
            String.join(", ", skills), experience);
        return callGroqApi(prompt);
    }

    private String callGroqApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", 1000);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                GROQ_URL, entity, Map.class);

            if (response.getBody() != null) {
                List choices = (List) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map messageResponse = (Map) choice.get("message");
                    return (String) messageResponse.get("content");
                }
            }
            return "No response from AI";
        } catch (Exception e) {
            return "AI analysis unavailable: " + e.getMessage();
        }
    }
}
