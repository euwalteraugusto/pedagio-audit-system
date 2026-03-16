package com.pedagio.audit.audit_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private LocalDateTime uploadDate;
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String analysisResult;
}