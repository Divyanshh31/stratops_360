package com.stratops.stratops360.service;

import com.stratops.stratops360.dto.KpiFormDto;
import com.stratops.stratops360.exception.ResourceNotFoundException;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.repository.KpiAuditLogRepository;
import com.stratops.stratops360.repository.KpiHistorySnapshotRepository;
import com.stratops.stratops360.repository.KpiMetricRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class KpiService {

    // Threshold constants for Kaplan-Norton / Laudon status determination
    private static final double ON_TRACK_MIN_RATIO = 95.0; // >= 95% target achievement
    private static final double WARNING_MIN_RATIO = 85.0;  // 85% - 94.9% target achievement

    private final KpiMetricRepository kpiRepository;
    private final KpiHistorySnapshotRepository historyRepository;
    private final KpiAuditLogRepository auditLogRepository;

    public KpiService(KpiMetricRepository kpiRepository,
                      KpiHistorySnapshotRepository historyRepository,
                      KpiAuditLogRepository auditLogRepository) {
        this.kpiRepository = kpiRepository;
        this.historyRepository = historyRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<KpiMetric> getAllKpis() {
        return kpiRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<KpiMetric> getActiveKpis() {
        return kpiRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<KpiMetric> getKpisByPerspective(KpiPerspective perspective) {
        return kpiRepository.findByPerspectiveAndActiveTrue(perspective);
    }

    @Transactional(readOnly = true)
    public KpiMetric getKpiById(Long id) {
        return kpiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI metric not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<KpiMetric> getKpiByCode(String code) {
        return kpiRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<KpiHistorySnapshot> getHistorySnapshots(String kpiCode) {
        return historyRepository.findByKpiCodeOrderByPeriodNameAsc(kpiCode);
    }

    @Transactional(readOnly = true)
    public List<KpiAuditLog> getAuditLogs(String kpiCode) {
        return auditLogRepository.findByKpiCodeOrderByChangedAtDesc(kpiCode);
    }

    @CacheEvict(value = {"scorecardSummary", "kpiList"}, allEntries = true)
    public KpiMetric createKpi(KpiFormDto dto) {
        if (kpiRepository.findByCode(dto.getCode()).isPresent()) {
            throw new IllegalArgumentException("A metric with code '" + dto.getCode() + "' already exists.");
        }

        KpiMetric kpi = new KpiMetric(
                dto.getCode().toUpperCase().trim(),
                dto.getName().trim(),
                dto.getPerspective(),
                dto.getTargetValue(),
                dto.getActualValue(),
                dto.getUnit().trim(),
                dto.getDepartment().trim(),
                KpiStatus.ON_TRACK,
                dto.getDescription(),
                String.valueOf(dto.getActualValue()),
                dto.isHigherIsBetter()
        );
        recalculateStatus(kpi);

        KpiMetric saved = kpiRepository.save(kpi);

        // Record audit log
        auditLogRepository.save(new KpiAuditLog(
                saved.getCode(), null, saved.getActualValue(), null, saved.getTargetValue(),
                "System User", "Initial KPI creation: " + dto.getRemarks()
        ));

        // Record initial history snapshot
        historyRepository.save(new KpiHistorySnapshot(saved.getCode(), "Current", saved.getActualValue(), saved.getTargetValue()));

        return saved;
    }

    @CacheEvict(value = {"scorecardSummary", "kpiList"}, allEntries = true)
    public KpiMetric updateKpi(KpiFormDto dto, String changedBy) {
        KpiMetric kpi = getKpiById(dto.getId());

        Double oldActual = kpi.getActualValue();
        Double oldTarget = kpi.getTargetValue();

        kpi.setName(dto.getName());
        kpi.setTargetValue(dto.getTargetValue());
        kpi.setActualValue(dto.getActualValue());
        kpi.setUnit(dto.getUnit());
        kpi.setDepartment(dto.getDepartment());
        kpi.setDescription(dto.getDescription());
        kpi.setHigherIsBetter(dto.isHigherIsBetter());

        recalculateStatus(kpi);
        KpiMetric updated = kpiRepository.save(kpi);

        // Log audit trail
        auditLogRepository.save(new KpiAuditLog(
                kpi.getCode(), oldActual, dto.getActualValue(), oldTarget, dto.getTargetValue(),
                changedBy != null ? changedBy : "Management User", dto.getRemarks()
        ));

        return updated;
    }

    @CacheEvict(value = {"scorecardSummary", "kpiList"}, allEntries = true)
    public void deactivateKpi(Long id) {
        KpiMetric kpi = getKpiById(id);
        kpi.setActive(false);
        kpiRepository.save(kpi);
        auditLogRepository.save(new KpiAuditLog(
                kpi.getCode(), kpi.getActualValue(), kpi.getActualValue(), kpi.getTargetValue(), kpi.getTargetValue(),
                "Administrator", "Metric deactivated/archived from active scorecard."
        ));
    }

    public void recalculateStatus(KpiMetric kpi) {
        double ratio = kpi.getAchievementRatio();
        if (ratio >= ON_TRACK_MIN_RATIO) {
            kpi.setStatus(KpiStatus.ON_TRACK);
        } else if (ratio >= WARNING_MIN_RATIO) {
            kpi.setStatus(KpiStatus.WARNING);
        } else {
            kpi.setStatus(KpiStatus.CRITICAL);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOverallScorecardSummary() {
        List<KpiMetric> all = kpiRepository.findByActiveTrue();
        long onTrack = all.stream().filter(k -> k.getStatus() == KpiStatus.ON_TRACK).count();
        long warning = all.stream().filter(k -> k.getStatus() == KpiStatus.WARNING).count();
        long critical = all.stream().filter(k -> k.getStatus() == KpiStatus.CRITICAL).count();

        double avgAchievement = all.stream()
                .mapToDouble(KpiMetric::getAchievementRatio)
                .average()
                .orElse(0.0);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalKpis", all.size());
        summary.put("onTrackCount", onTrack);
        summary.put("warningCount", warning);
        summary.put("criticalCount", critical);
        summary.put("overallHealthScore", Math.round(avgAchievement * 10.0) / 10.0);

        Map<KpiPerspective, Double> perspectiveScores = new EnumMap<>(KpiPerspective.class);
        for (KpiPerspective p : KpiPerspective.values()) {
            double pScore = all.stream()
                    .filter(k -> k.getPerspective() == p)
                    .mapToDouble(KpiMetric::getAchievementRatio)
                    .average()
                    .orElse(0.0);
            perspectiveScores.put(p, Math.round(pScore * 10.0) / 10.0);
        }
        summary.put("perspectiveScores", perspectiveScores);
        summary.put("financialHealth", perspectiveScores.getOrDefault(KpiPerspective.FINANCIAL, 88.0));
        summary.put("customerHealth", perspectiveScores.getOrDefault(KpiPerspective.CUSTOMER, 92.5));
        summary.put("processHealth", perspectiveScores.getOrDefault(KpiPerspective.INTERNAL_PROCESS, 86.4));
        summary.put("learningHealth", perspectiveScores.getOrDefault(KpiPerspective.LEARNING_GROWTH, 95.0));

        return summary;
    }
}
