package com.stratops.stratops360.service;

import com.stratops.stratops360.dto.KpiFormDto;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.repository.KpiAuditLogRepository;
import com.stratops.stratops360.repository.KpiHistorySnapshotRepository;
import com.stratops.stratops360.repository.KpiMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    private KpiMetricRepository kpiRepository;

    @Mock
    private KpiHistorySnapshotRepository snapshotRepository;

    @Mock
    private KpiAuditLogRepository auditLogRepository;

    private KpiService kpiService;

    @BeforeEach
    void setUp() {
        kpiService = new KpiService(kpiRepository, snapshotRepository, auditLogRepository);
    }

    @Test
    @DisplayName("Higher Is Better: Achievement ratio determines On Track / Warning / Critical")
    void testHigherIsBetterStatusCalculations() {
        KpiMetric kpi = new KpiMetric("FIN-01", "Operating Profit Margin", KpiPerspective.FINANCIAL,
                18.0, 18.0, "%", "Finance", KpiStatus.ON_TRACK, "Operating profitability", "16,17,18", true);

        // 100% achievement -> ON_TRACK
        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.ON_TRACK, kpi.getStatus());

        // 16.0 / 18.0 = 88.8% -> WARNING
        kpi.setActualValue(16.0);
        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.WARNING, kpi.getStatus());

        // 14.0 / 18.0 = 77.7% -> CRITICAL
        kpi.setActualValue(14.0);
        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.CRITICAL, kpi.getStatus());
    }

    @Test
    @DisplayName("Lower Is Better: Inverted achievement ratio (e.g. Cycle Time, Defect Rate)")
    void testLowerIsBetterStatusCalculations() {
        // Target = 4.5 days, Actual = 4.0 days -> ON_TRACK
        KpiMetric kpi = new KpiMetric("PROC-01", "Fulfillment Cycle Time", KpiPerspective.INTERNAL_PROCESS,
                4.5, 4.0, "Days", "Logistics", KpiStatus.ON_TRACK, "Order to delivery time", "5.0,4.5,4.0", false);

        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.ON_TRACK, kpi.getStatus());

        // Target = 4.5, Actual = 5.0 -> 90% achievement ratio -> WARNING
        kpi.setActualValue(5.0);
        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.WARNING, kpi.getStatus());

        // Target = 4.5, Actual = 6.8 -> 66.1% achievement ratio -> CRITICAL
        kpi.setActualValue(6.8);
        kpiService.recalculateStatus(kpi);
        assertEquals(KpiStatus.CRITICAL, kpi.getStatus());
    }

    @Test
    @DisplayName("Create KPI generates new entity and audit log")
    void testCreateKpi() {
        when(kpiRepository.findByCode("FIN-99")).thenReturn(Optional.empty());
        when(kpiRepository.save(any(KpiMetric.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(snapshotRepository.save(any(KpiHistorySnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KpiFormDto dto = new KpiFormDto();
        dto.setCode("FIN-99");
        dto.setName("New Financial Metric");
        dto.setPerspective(KpiPerspective.FINANCIAL);
        dto.setTargetValue(25.0);
        dto.setActualValue(24.0);
        dto.setUnit("%");
        dto.setDepartment("Finance");
        dto.setDescription("Testing metric");
        dto.setHigherIsBetter(true);

        KpiMetric created = kpiService.createKpi(dto);

        assertNotNull(created);
        assertEquals("FIN-99", created.getCode());
        verify(kpiRepository, times(1)).save(any(KpiMetric.class));
        verify(auditLogRepository, times(1)).save(any(KpiAuditLog.class));
    }

    @Test
    @DisplayName("Updating KPI saves revision audit trail")
    void testUpdateKpiRecordsAuditLog() {
        KpiMetric existing = new KpiMetric("FIN-01", "Operating Profit Margin", KpiPerspective.FINANCIAL,
                18.0, 15.4, "%", "Finance", KpiStatus.CRITICAL, "Operating profitability", "15.0,15.4", true);
        existing.setId(1L);

        when(kpiRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(kpiRepository.save(any(KpiMetric.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KpiFormDto dto = new KpiFormDto();
        dto.setId(1L);
        dto.setCode("FIN-01");
        dto.setName("Operating Profit Margin Updated");
        dto.setPerspective(KpiPerspective.FINANCIAL);
        dto.setTargetValue(18.0);
        dto.setActualValue(17.5);
        dto.setUnit("%");
        dto.setDepartment("Finance");
        dto.setDescription("Revised objective");
        dto.setHigherIsBetter(true);

        KpiMetric updated = kpiService.updateKpi(dto, "VP Finance");

        assertNotNull(updated);
        assertEquals(17.5, updated.getActualValue());
        verify(auditLogRepository, atLeastOnce()).save(any(KpiAuditLog.class));
    }

    @Test
    @DisplayName("Overall Scorecard Summary computes weighted health score correctly")
    void testOverallScorecardSummary() {
        KpiMetric k1 = new KpiMetric("FIN-01", "M1", KpiPerspective.FINANCIAL, 10.0, 10.0, "%", "D1", KpiStatus.ON_TRACK, "desc", "10", true);
        KpiMetric k2 = new KpiMetric("CUST-01", "M2", KpiPerspective.CUSTOMER, 10.0, 9.0, "%", "D2", KpiStatus.WARNING, "desc", "9", true);
        KpiMetric k3 = new KpiMetric("PROC-01", "M3", KpiPerspective.INTERNAL_PROCESS, 10.0, 7.5, "%", "D3", KpiStatus.CRITICAL, "desc", "7.5", true);
        KpiMetric k4 = new KpiMetric("LG-01", "M4", KpiPerspective.LEARNING_GROWTH, 10.0, 10.0, "%", "D4", KpiStatus.ON_TRACK, "desc", "10", true);

        when(kpiRepository.findByActiveTrue()).thenReturn(List.of(k1, k2, k3, k4));

        Map<String, Object> summary = kpiService.getOverallScorecardSummary();

        assertNotNull(summary);
        assertEquals(2L, summary.get("onTrackCount"));
        assertEquals(1L, summary.get("warningCount"));
        assertEquals(1L, summary.get("criticalCount"));
        assertTrue((Double) summary.get("overallHealthScore") > 80.0);
    }
}
