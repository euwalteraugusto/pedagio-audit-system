package com.pedagio.audit.audit_system.repository;

import com.pedagio.audit.audit_system.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Aqui você pode adicionar métodos de busca customizados no futuro,
    // como: List<Report> findByStatus(String status);
}