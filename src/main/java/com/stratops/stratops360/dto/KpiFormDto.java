package com.stratops.stratops360.dto;

import com.stratops.stratops360.model.KpiPerspective;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class KpiFormDto {

    private Long id;

    @NotBlank(message = "KPI Code is required (e.g. FIN-01)")
    @Size(max = 20, message = "Code must be 20 characters or fewer")
    private String code;

    @NotBlank(message = "Metric name is required")
    @Size(max = 120, message = "Name must be 120 characters or fewer")
    private String name;

    @NotNull(message = "Scorecard perspective is required")
    private KpiPerspective perspective;

    @NotNull(message = "Target SLA value is required")
    private Double targetValue;

    @NotNull(message = "Current actual value is required")
    private Double actualValue;

    @NotBlank(message = "Unit of measure is required (e.g. %, $M, Days)")
    private String unit;

    @NotBlank(message = "Department ownership is required")
    private String department;

    private String description;

    private boolean higherIsBetter = true;

    private String remarks = "Manual management adjustment";

    public KpiFormDto() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHigherIsBetter() {
        return higherIsBetter;
    }

    public void setHigherIsBetter(boolean higherIsBetter) {
        this.higherIsBetter = higherIsBetter;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
