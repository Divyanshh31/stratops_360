package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.SavedScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedScenarioRepository extends JpaRepository<SavedScenario, Long> {
    List<SavedScenario> findAllByOrderByCreatedAtDesc();
}
