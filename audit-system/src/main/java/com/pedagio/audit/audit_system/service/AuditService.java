package com.pedagio.audit.audit_system.service;

import com.pedagio.audit.audit_system.model.Report;
import com.pedagio.audit.audit_system.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.time.LocalDateTime;

@Service
public class AuditService {

    private final WebClient webClient;
    private final ReportRepository reportRepository;

    public AuditService(WebClient webClient, ReportRepository reportRepository) {
        this.webClient = webClient;
        this.reportRepository = reportRepository;
    }

    public Report processAudit(MultipartFile file) {
        // 1. Criar e salvar o registro inicial (Status: PENDING)
        Report report = new Report();
        report.setFileName(file.getOriginalFilename());
        report.setUploadDate(LocalDateTime.now());
        report.setStatus("PENDING");
        report = reportRepository.save(report);

        try {
            // 2. Preparar envio para o Python
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            // 3. Chamar Python Service
            String pythonResponse = webClient.post()
                    .uri("/audit")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 4. Atualizar o registro com o resultado do Python (Status: PROCESSED)
            report.setAnalysisResult(pythonResponse);
            report.setStatus("PROCESSED");
            
        } catch (Exception e) {
            // 5. Se falhar, marcar como ERROR
            report.setStatus("ERROR");
            report.setAnalysisResult("Erro na integração: " + e.getMessage());
        }

        return reportRepository.save(report);
    }
}