package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.KpiAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiAuditLogRepository extends JpaRepository<KpiAuditLog, Long> {
    List<KpiAuditLog> findByKpiCodeOrderByChangedAtDesc(String kpiCode);
    Page<KpiAuditLog> findAllByOrderByChangedAtDesc(Pageable pageable);
}
