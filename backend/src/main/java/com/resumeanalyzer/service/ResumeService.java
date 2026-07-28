package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ResumeDTO;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final PdfParserService pdfParserService;
    private final AiAnalysisService aiAnalysisService;

    public ResumeService(ResumeRepository resumeRepository,
                         PdfParserService pdfParserService,
                         AiAnalysisService aiAnalysisService) {
        this.resumeRepository = resumeRepository;
        this.pdfParserService = pdfParserService;
        this.aiAnalysisService = aiAnalysisService;
    }

    public ResumeDTO.UploadResponse uploadAndParse(MultipartFile file) throws IOException {
        String rawText = pdfParserService.extractText(file);

        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());
        resume.setRawText(rawText);
        resume.setCandidateName(pdfParserService.extractName(rawText));
        resume.setEmail(pdfParserService.extractEmail(rawText));
        resume.setPhone(pdfParserService.extractPhone(rawText));
        resume.setSkills(pdfParserService.extractSkills(rawText));
        resume.setEducation(pdfParserService.extractSection(rawText, "Education"));
        resume.setExperience(pdfParserService.extractSection(rawText, "Experience"));
        resume.setProjects(pdfParserService.extractSection(rawText, "Projects"));

        Resume saved = resumeRepository.save(resume);

        ResumeDTO.UploadResponse response = new ResumeDTO.UploadResponse();
        response.setId(saved.getId());
        response.setCandidateName(saved.getCandidateName());
        response.setEmail(saved.getEmail());
        response.setPhone(saved.getPhone());
        response.setSkills(saved.getSkills());
        response.setEducation(saved.getEducation());
        response.setExperience(saved.getExperience());
        response.setProjects(saved.getProjects());
        response.setMessage("Resume parsed successfully");
        return response;
    }

    public ResumeDTO.AnalysisResponse analyzeResume(Long id, ResumeDTO.AnalysisRequest request) {
        Resume resume = resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));

        List<String> requiredSkills = request.getRequiredSkills();
        List<String> resumeSkills = resume.getSkills();

        int atsScore = aiAnalysisService.calculateAtsScore(resumeSkills, requiredSkills);
        List<String> missingSkills = aiAnalysisService.getMissingSkills(resumeSkills, requiredSkills);
        List<String> matchedSkills = resumeSkills.stream()
            .filter(s -> requiredSkills.stream().anyMatch(r -> r.equalsIgnoreCase(s)))
            .toList();

        String skillGap = aiAnalysisService.generateSkillGapAnalysis(
            resume.getRawText(), request.getJobDescription());
        String suggestions = aiAnalysisService.generateResumeSuggestions(resume.getRawText());
        String roles = aiAnalysisService.recommendJobRoles(resumeSkills, resume.getExperience());

        resume.setAtsScore(atsScore);
        resume.setSkillGapAnalysis(skillGap);
        resume.setAiSuggestions(suggestions);
        resume.setRecommendedRoles(roles);
        resumeRepository.save(resume);

        ResumeDTO.AnalysisResponse response = new ResumeDTO.AnalysisResponse();
        response.setResumeId(id);
        response.setAtsScore(atsScore);
        response.setMatchedSkills(matchedSkills);
        response.setMissingSkills(missingSkills);
        response.setSkillGapAnalysis(skillGap);
        response.setAiSuggestions(suggestions);
        response.setRecommendedRoles(List.of(roles.split("\n")));
        return response;
    }

    public ResumeDTO.SuggestionsResponse getSuggestions(Long id) {
        Resume resume = resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));

        ResumeDTO.SuggestionsResponse response = new ResumeDTO.SuggestionsResponse();
        response.setResumeId(id);
        response.setAiSuggestions(resume.getAiSuggestions());
        response.setRecommendedRoles(List.of(resume.getRecommendedRoles() != null
            ? resume.getRecommendedRoles().split("\n")
            : new String[]{}));

        if (resume.getAiSuggestions() != null) {
            List<String> tips = Arrays.stream(resume.getAiSuggestions().split("\n"))
                .filter(line -> line.matches("^\\d+\\..*"))
                .toList();
            response.setImprovementTips(tips);
        }
        return response;
    }

    public Resume getResume(Long id) {
        return resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));
    }
}
