package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.DecisionInitiative;
import com.stratops.stratops360.model.DecisionStage;
import com.stratops.stratops360.model.KpiPerspective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionInitiativeRepository extends JpaRepository<DecisionInitiative, Long> {
    List<DecisionInitiative> findByCurrentStage(DecisionStage stage);
    List<DecisionInitiative> findByPerspective(KpiPerspective perspective);
    List<DecisionInitiative> findAllByOrderByCreatedAtDesc();
    Page<DecisionInitiative> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<DecisionInitiative> findByTitleContainingIgnoreCaseOrProblemStatementContainingIgnoreCase(String q1, String q2);
}
