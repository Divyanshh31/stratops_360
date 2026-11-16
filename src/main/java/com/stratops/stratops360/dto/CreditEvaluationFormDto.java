package com.stratops.stratops360.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreditEvaluationFormDto {

    @NotBlank(message = "Applicant name is required")
    @Size(max = 120, message = "Name must be 120 characters or fewer")
    private String applicantName;

    @NotNull(message = "Annual income is required")
    @Positive(message = "Annual gross income must be strictly greater than $0")
    private BigDecimal annualIncome;

    @NotNull(message = "Monthly car payment is required")
    @PositiveOrZero(message = "Monthly car payment cannot be negative")
    private BigDecimal monthlyCarPayment;

    @NotNull(message = "Monthly mortgage/rent payment is required")
    @PositiveOrZero(message = "Monthly mortgage/rent payment cannot be negative")
    private BigDecimal monthlyMortgagePayment;

    @NotNull(message = "Years employed (tenure) is required")
    @Min(value = 0, message = "Years employed cannot be negative")
    @Max(value = 70, message = "Years employed must be realistic")
    private Integer yearsEmployed;

    @NotNull(message = "Monthly other debt is required")
    @PositiveOrZero(message = "Monthly other debt cannot be negative")
    private BigDecimal monthlyOtherDebt;

    public CreditEvaluationFormDto() {
    }

    public CreditEvaluationFormDto(String applicantName, BigDecimal annualIncome, BigDecimal monthlyCarPayment,
                                   BigDecimal monthlyMortgagePayment, Integer yearsEmployed, BigDecimal monthlyOtherDebt) {
        this.applicantName = applicantName;
        this.annualIncome = annualIncome;
        this.monthlyCarPayment = monthlyCarPayment;
        this.monthlyMortgagePayment = monthlyMortgagePayment;
        this.yearsEmployed = yearsEmployed;
        this.monthlyOtherDebt = monthlyOtherDebt;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(BigDecimal annualIncome) {
        this.annualIncome = annualIncome;
    }

    public BigDecimal getMonthlyCarPayment() {
        return monthlyCarPayment;
    }

    public void setMonthlyCarPayment(BigDecimal monthlyCarPayment) {
        this.monthlyCarPayment = monthlyCarPayment;
    }

    public BigDecimal getMonthlyMortgagePayment() {
        return monthlyMortgagePayment;
    }

    public void setMonthlyMortgagePayment(BigDecimal monthlyMortgagePayment) {
        this.monthlyMortgagePayment = monthlyMortgagePayment;
    }

    public Integer getYearsEmployed() {
        return yearsEmployed;
    }

    public void setYearsEmployed(Integer yearsEmployed) {
        this.yearsEmployed = yearsEmployed;
    }

    public BigDecimal getMonthlyOtherDebt() {
        return monthlyOtherDebt;
    }

    public void setMonthlyOtherDebt(BigDecimal monthlyOtherDebt) {
        this.monthlyOtherDebt = monthlyOtherDebt;
    }
}
