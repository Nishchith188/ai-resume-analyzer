package com.resumeanalyzer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfParserService {

    private static final List<String> KNOWN_SKILLS = List.of(
        "Java", "Python", "JavaScript", "TypeScript", "React", "Spring Boot",
        "Node.js", "PostgreSQL", "MySQL", "MongoDB", "Docker", "Kubernetes",
        "AWS", "GCP", "Azure", "Git", "REST API", "GraphQL", "Redis",
        "Kafka", "RabbitMQ", "Hibernate", "JPA", "Maven", "Gradle",
        "JUnit", "Mockito", "CI/CD", "Jenkins", "Linux", "HTML", "CSS",
        "Machine Learning", "TensorFlow", "PyTorch", "Pandas", "NumPy"
    );

    public String extractText(MultipartFile file) throws IOException {
        // PDFBox 3.x uses Loader.loadPDF() instead of PDDocument.load()
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public String extractName(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() < 50 && !line.contains("@")) {
                return line;
            }
        }
        return "Unknown";
    }

    public String extractEmail(String text) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }

    public String extractPhone(String text) {
        Pattern pattern = Pattern.compile("(\\+?\\d[\\d\\s\\-().]{7,}\\d)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group().trim() : "";
    }

    public List<String> extractSkills(String text) {
        List<String> found = new ArrayList<>();
        String lowerText = text.toLowerCase();
        for (String skill : KNOWN_SKILLS) {
            if (lowerText.contains(skill.toLowerCase())) {
                found.add(skill);
            }
        }
        return found;
    }

    public String extractSection(String text, String sectionName) {
        String[] sectionHeaders = {
            sectionName.toUpperCase(),
            sectionName.toLowerCase(),
            sectionName
        };

        for (String header : sectionHeaders) {
            int start = text.indexOf(header);
            if (start != -1) {
                int end = findNextSection(text, start + header.length());
                return text.substring(start, end).trim();
            }
        }
        return "";
    }

    private int findNextSection(String text, int from) {
        String[] commonSections = {
            "EDUCATION", "EXPERIENCE", "SKILLS", "PROJECTS",
            "CERTIFICATIONS", "AWARDS", "SUMMARY", "OBJECTIVE"
        };

        int nextSection = text.length();
        for (String section : commonSections) {
            int pos = text.indexOf(section, from);
            if (pos != -1 && pos < nextSection) {
                nextSection = pos;
            }
        }
        return nextSection;
    }
}
