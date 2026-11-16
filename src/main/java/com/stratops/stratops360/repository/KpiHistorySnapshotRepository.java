package com.stratops.stratops360.repository;

import com.stratops.stratops360.model.KpiHistorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiHistorySnapshotRepository extends JpaRepository<KpiHistorySnapshot, Long> {
    List<KpiHistorySnapshot> findByKpiCodeOrderByPeriodNameAsc(String kpiCode);
    List<KpiHistorySnapshot> findByPeriodName(String periodName);
}
