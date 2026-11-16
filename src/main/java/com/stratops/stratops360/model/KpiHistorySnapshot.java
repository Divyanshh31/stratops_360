package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "kpi_history_snapshots", indexes = {
        @Index(name = "idx_kpi_snapshot_code", columnList = "kpiCode"),
        @Index(name = "idx_kpi_snapshot_period", columnList = "periodName")
})
public class KpiHistorySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kpiCode;

    @Column(nullable = false)
    private String periodName; // e.g. "2026-Q1", "2026-Q2", "2026-Q3"

    @Column(nullable = false)
    private Double actualValue;

    @Column(nullable = false)
    private Double targetValue;

    private LocalDateTime recordedAt = LocalDateTime.now();

    public KpiHistorySnapshot() {
    }

    public KpiHistorySnapshot(String kpiCode, String periodName, Double actualValue, Double targetValue) {
        this.kpiCode = kpiCode;
        this.periodName = periodName;
        this.actualValue = actualValue;
        this.targetValue = targetValue;
        this.recordedAt = LocalDateTime.now();
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

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public Double getActualValue() {
        return actualValue;
    }

    public void setActualValue(Double actualValue) {
        this.actualValue = actualValue;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiHistorySnapshot that)) return false;
        return Objects.equals(id, that.id) ||
                (Objects.equals(kpiCode, that.kpiCode) && Objects.equals(periodName, that.periodName));
    }

    @Override
    public int hashCode() {
        return Objects.hash(kpiCode, periodName);
    }
}
