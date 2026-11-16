package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.CreditRuleEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRuleEvaluationRepository extends JpaRepository<CreditRuleEvaluation, Long> {
    List<CreditRuleEvaluation> findAllByOrderByEvaluationTimestampDesc();
    Page<CreditRuleEvaluation> findAllByOrderByEvaluationTimestampDesc(Pageable pageable);
    Page<CreditRuleEvaluation> findByApproved(boolean approved, Pageable pageable);
    List<CreditRuleEvaluation> findByApplicantNameContainingIgnoreCase(String query);
}
