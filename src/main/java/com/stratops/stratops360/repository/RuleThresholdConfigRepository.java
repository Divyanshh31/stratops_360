package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.RuleThresholdConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleThresholdConfigRepository extends JpaRepository<RuleThresholdConfig, Long> {
    Optional<RuleThresholdConfig> findFirstByOrderByIdAsc();
}
