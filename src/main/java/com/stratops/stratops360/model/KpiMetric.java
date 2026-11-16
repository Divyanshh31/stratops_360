package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "kpi_metrics", indexes = {
        @Index(name = "idx_kpi_code", columnList = "code", unique = true),
        @Index(name = "idx_kpi_perspective", columnList = "perspective"),
        @Index(name = "idx_kpi_status", columnList = "status"),
        @Index(name = "idx_kpi_department", columnList = "department")
})
public class KpiMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KpiPerspective perspective;

    @Column(nullable = false)
    private Double targetValue;

    @Column(nullable = false)
    private Double actualValue;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KpiStatus status;

    @Column(length = 1000)
    private String description;

    // Comma-separated 6-month historical values
    private String historicalTrend;

    private boolean higherIsBetter = true;

    private boolean active = true;

    public KpiMetric() {
    }

    public KpiMetric(String code, String name, KpiPerspective perspective, Double targetValue, Double actualValue,
                     String unit, String department, KpiStatus status, String description, String historicalTrend, boolean higherIsBetter) {
        this.code = code;
        this.name = name;
        this.perspective = perspective;
        this.targetValue = targetValue;
        this.actualValue = actualValue;
        this.unit = unit;
        this.department = department;
        this.status = status;
        this.description = description;
        this.historicalTrend = historicalTrend;
        this.higherIsBetter = higherIsBetter;
        this.active = true;
    }

    public double getVariancePercentage() {
        if (targetValue == null || targetValue == 0.0) return 0.0;
        double actual = actualValue != null ? actualValue : 0.0;
        return ((actual - targetValue) / targetValue) * 100.0;
    }

    public double getAchievementRatio() {
        if (targetValue == null || targetValue == 0.0) return 0.0;
        double actual = actualValue != null ? actualValue : 0.0;
        if (actual == 0.0 && !higherIsBetter) return 100.0;
        if (higherIsBetter) {
            return (actual / targetValue) * 100.0;
        } else {
            return actual > 0.0 ? (targetValue / actual) * 100.0 : 100.0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KpiPerspective getPerspective() {
        return perspective;
    }

    public void setPerspective(KpiPerspective perspective) {
        this.perspective = perspective;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public Double getActualValue() {
        return actualValue;
    }

    public void setActualValue(Double actualValue) {
        this.actualValue = actualValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public KpiStatus getStatus() {
        return status;
    }

    public void setStatus(KpiStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistoricalTrend() {
        return historicalTrend;
    }

    public String getHistoricalTrendCsv() {
        return historicalTrend;
    }

    public void setHistoricalTrend(String historicalTrend) {
        this.historicalTrend = historicalTrend;
    }

    public boolean isHigherIsBetter() {
        return higherIsBetter;
    }

    public void setHigherIsBetter(boolean higherIsBetter) {
        this.higherIsBetter = higherIsBetter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiMetric kpiMetric)) return false;
        return Objects.equals(code, kpiMetric.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "KpiMetric{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", perspective=" + perspective +
                ", actual=" + actualValue +
                ", target=" + targetValue +
                '}';
    }
}
