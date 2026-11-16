package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "kpi_audit_logs", indexes = {
        @Index(name = "idx_audit_kpi_code", columnList = "kpiCode"),
        @Index(name = "idx_audit_changed_at", columnList = "changedAt")
})
public class KpiAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kpiCode;

    private Double oldActual;
    private Double newActual;

    private Double oldTarget;
    private Double newTarget;

    private String changedBy;
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(length = 1000)
    private String remarks;

    public KpiAuditLog() {
    }

    public KpiAuditLog(String kpiCode, Double oldActual, Double newActual, Double oldTarget, Double newTarget,
                       String changedBy, String remarks) {
        this.kpiCode = kpiCode;
        this.oldActual = oldActual;
        this.newActual = newActual;
        this.oldTarget = oldTarget;
        this.newTarget = newTarget;
        this.changedBy = changedBy;
        this.remarks = remarks;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public Double getOldActual() {
        return oldActual;
    }

    public void setOldActual(Double oldActual) {
        this.oldActual = oldActual;
    }

    public Double getNewActual() {
        return newActual;
    }

    public void setNewActual(Double newActual) {
        this.newActual = newActual;
    }

    public Double getOldTarget() {
        return oldTarget;
    }

    public void setOldTarget(Double oldTarget) {
        this.oldTarget = oldTarget;
    }

    public Double getNewTarget() {
        return newTarget;
    }

    public void setNewTarget(Double newTarget) {
        this.newTarget = newTarget;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiAuditLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
