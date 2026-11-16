package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.KpiMetric;
import com.stratops.stratops360.model.KpiPerspective;
import com.stratops.stratops360.model.KpiStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KpiMetricRepository extends JpaRepository<KpiMetric, Long> {
    List<KpiMetric> findByActiveTrue();
    List<KpiMetric> findByPerspectiveAndActiveTrue(KpiPerspective perspective);
    List<KpiMetric> findByPerspective(KpiPerspective perspective);
    List<KpiMetric> findByStatus(KpiStatus status);
    List<KpiMetric> findByDepartment(String department);
    Optional<KpiMetric> findByCode(String code);
    boolean existsByCode(String code);

    Page<KpiMetric> findByActiveTrue(Pageable pageable);

    List<KpiMetric> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String nameQuery, String codeQuery);
}
