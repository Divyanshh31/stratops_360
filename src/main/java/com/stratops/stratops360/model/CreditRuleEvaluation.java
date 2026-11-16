package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "credit_rule_evaluations", indexes = {
        @Index(name = "idx_eval_timestamp", columnList = "evaluationTimestamp"),
        @Index(name = "idx_eval_approved", columnList = "approved")
})
public class CreditRuleEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyCarPayment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyMortgagePayment;

    @Column(nullable = false)
    private Integer yearsEmployed;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyOtherDebt;

    private boolean approved;

    @Column(precision = 12, scale = 2)
    private BigDecimal grantedCreditLimit; // 10000.00, 3000.00, or 0.00

    private String outcomeState; // e.g. "STATE_F: Limit $10,000", "STATE_I: Limit $3,000", "EXIT: Rejected"

    @Column(length = 4000)
    private String ruleFiringLog; // Detailed step-by-step trace of forward chaining

    private LocalDateTime evaluationTimestamp = LocalDateTime.now();

    public CreditRuleEvaluation() {
    }

    public CreditRuleEvaluation(String applicantName, BigDecimal annualIncome, BigDecimal monthlyCarPayment,
                                BigDecimal monthlyMortgagePayment, Integer yearsEmployed, BigDecimal monthlyOtherDebt,
                                boolean approved, BigDecimal grantedCreditLimit, String outcomeState, String ruleFiringLog) {
        this.applicantName = applicantName;
        this.annualIncome = annualIncome;
        this.monthlyCarPayment = monthlyCarPayment;
        this.monthlyMortgagePayment = monthlyMortgagePayment;
        this.yearsEmployed = yearsEmployed;
        this.monthlyOtherDebt = monthlyOtherDebt;
        this.approved = approved;
        this.grantedCreditLimit = grantedCreditLimit;
        this.outcomeState = outcomeState;
        this.ruleFiringLog = ruleFiringLog;
        this.evaluationTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public BigDecimal getGrantedCreditLimit() {
        return grantedCreditLimit;
    }

    public void setGrantedCreditLimit(BigDecimal grantedCreditLimit) {
        this.grantedCreditLimit = grantedCreditLimit;
    }

    public String getOutcomeState() {
        return outcomeState;
    }

    public void setOutcomeState(String outcomeState) {
        this.outcomeState = outcomeState;
    }

    public String getRuleFiringLog() {
        return ruleFiringLog;
    }

    public void setRuleFiringLog(String ruleFiringLog) {
        this.ruleFiringLog = ruleFiringLog;
    }

    public LocalDateTime getEvaluationTimestamp() {
        return evaluationTimestamp;
    }

    public void setEvaluationTimestamp(LocalDateTime evaluationTimestamp) {
        this.evaluationTimestamp = evaluationTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreditRuleEvaluation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
