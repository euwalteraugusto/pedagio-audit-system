package com.pedagio.audit.audit_system.controller;

import com.pedagio.audit.audit_system.model.Report;
import com.pedagio.audit.audit_system.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final AuditService auditService;

    public ReportController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Report> upload(@RequestParam("file") MultipartFile file) {
        Report processedReport = auditService.processAudit(file);
        return ResponseEntity.ok(processedReport);
    }   
}