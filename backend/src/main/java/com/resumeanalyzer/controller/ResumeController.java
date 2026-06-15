package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.ResumeDTO;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.service.ResumeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeDTO.UploadResponse> uploadResume(
            @RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(resumeService.uploadAndParse(file));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(resumeService.getResume(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<ResumeDTO.AnalysisResponse> analyzeResume(
            @PathVariable Long id,
            @RequestBody ResumeDTO.AnalysisRequest request) {
        try {
            return ResponseEntity.ok(resumeService.analyzeResume(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/suggestions")
    public ResponseEntity<ResumeDTO.SuggestionsResponse> getSuggestions(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(resumeService.getSuggestions(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
