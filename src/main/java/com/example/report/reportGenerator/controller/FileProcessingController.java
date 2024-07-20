package com.example.report.reportGenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.report.reportGenerator.service.FileProcessorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class FileProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessingController.class);

    @Autowired
    private FileProcessorService fileProcessorService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received file upload request: {}", file.getOriginalFilename());
            fileProcessorService.processFile(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            logger.error("Error processing file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PostMapping("/generate-report")
    public ResponseEntity<String> generateReport(@RequestParam("outputPath") String outputPath) {
        try {
            logger.info("Received request to generate report");
            fileProcessorService.generateReport(outputPath);
            return ResponseEntity.ok("Report generated successfully at " + outputPath);
        } catch (Exception e) {
            logger.error("Error generating report", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate report: " + e.getMessage());
        }
    }
}
