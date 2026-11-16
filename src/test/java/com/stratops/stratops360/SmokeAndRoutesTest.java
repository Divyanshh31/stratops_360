package com.stratops.stratops360;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SmokeAndRoutesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Root route '/' loads Balanced Scorecard Dashboard with 200 OK")
    void testRootDashboardRoute() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Balanced Scorecard (ESS Dashboard)")))
                .andExpect(content().string(containsString("Overall Scorecard Health")));
    }

    @Test
    @DisplayName("Route '/dashboard' loads with period parameter")
    void testDashboardWithPeriod() throws Exception {
        mockMvc.perform(get("/dashboard").param("period", "2026-Q2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2026-Q2")));
    }

    @Test
    @DisplayName("Route '/perspective/FINANCIAL' loads financial drilldown")
    void testPerspectiveDrilldown() throws Exception {
        mockMvc.perform(get("/perspective/FINANCIAL"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Financial Perspective")))
                .andExpect(content().string(containsString("Operating Profit Margin")));
    }

    @Test
    @DisplayName("Route '/decision/pipeline' loads Simon 4-Stage Pipeline")
    void testDecisionPipelineRoute() throws Exception {
        mockMvc.perform(get("/decision/pipeline"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Herbert Simon’s Decision-Making Pipeline")))
                .andExpect(content().string(containsString("1. INTELLIGENCE")))
                .andExpect(content().string(containsString("2. DESIGN")))
                .andExpect(content().string(containsString("3. CHOICE")))
                .andExpect(content().string(containsString("4. IMPLEMENTATION")));
    }

    @Test
    @DisplayName("Route '/decision/new' loads Stage 1 Form")
    void testDecisionNewRoute() throws Exception {
        mockMvc.perform(get("/decision/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Problem Discovery (Intelligence Stage)")));
    }

    @Test
    @DisplayName("Route '/simulation' loads What-If DSS Simulator")
    void testSimulationRoute() throws Exception {
        mockMvc.perform(get("/simulation"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Interactive \"What-If\" Scenario Simulator")))
                .andExpect(content().string(containsString("Product Pricing Variation")));
    }

    @Test
    @DisplayName("Route '/simulation/compare' loads comparison matrix")
    void testSimulationCompareRoute() throws Exception {
        mockMvc.perform(get("/simulation/compare"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Multi-Scenario DSS Comparison Matrix")));
    }

    @Test
    @DisplayName("Route '/expert-system' loads Forward-Chaining Engine & SVG Flowchart")
    void testExpertSystemRoute() throws Exception {
        mockMvc.perform(get("/expert-system"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Operational Expert System")))
                .andExpect(content().string(containsString("Knowledge Base Rule Topology")));
    }

    @Test
    @DisplayName("Route '/expert-system/thresholds' loads admin panel")
    void testExpertSystemThresholdsRoute() throws Exception {
        mockMvc.perform(get("/expert-system/thresholds"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Knowledge Base Rule Engine Parameters")));
    }

    @Test
    @DisplayName("Route '/reports' loads BI Parameterized Report")
    void testReportsRoute() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Business Intelligence (BI) Parameterized Reports")))
                .andExpect(content().string(containsString("Parameterized Filtering Controls")));
    }

    @Test
    @DisplayName("Route '/reports/export-csv' returns CSV attachment")
    void testReportsCsvExport() throws Exception {
        mockMvc.perform(get("/reports/export-csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/csv")))
                .andExpect(header().string("Content-Disposition", containsString("filename=\"scorecard_reports.csv\"")));
    }

    @Test
    @DisplayName("Route '/theory-guide' loads MIS curriculum alignment guide")
    void testTheoryGuideRoute() throws Exception {
        mockMvc.perform(get("/theory-guide"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MIS Curriculum Mapping")))
                .andExpect(content().string(containsString("Launch Guided Viva Demo Tour")));
    }

    @Test
    @DisplayName("Route '/search?q=margin' returns search results")
    void testGlobalSearchRoute() throws Exception {
        mockMvc.perform(get("/search").param("q", "margin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Global Enterprise Search")));
    }

    @Test
    @DisplayName("REST API '/api/v1/kpis' returns JSON array of metrics")
    void testRestApiKpis() throws Exception {
        mockMvc.perform(get("/api/v1/kpis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("REST API '/api/v1/summary' returns JSON scorecard summary")
    void testRestApiSummary() throws Exception {
        mockMvc.perform(get("/api/v1/summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallHealthScore").isNumber());
    }

    @Test
    @DisplayName("Validation Error Handling: POST /kpi/create with missing code re-renders kpi_new with 200 OK")
    void testKpiFormValidationErrorHandling() throws Exception {
        mockMvc.perform(post("/kpi/create")
                        .with(csrf())
                        .param("name", "Test Metric")
                        .param("targetValue", "10.0")
                        .param("actualValue", "9.0")
                        .param("unit", "%")
                        .param("department", "Finance"))
                .andExpect(status().isOk())
                .andExpect(view().name("kpi_new"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Expert System evaluation via POST /expert-system/evaluate executes and returns 200 OK")
    void testExpertSystemEvaluatePost() throws Exception {
        mockMvc.perform(post("/expert-system/evaluate")
                        .with(csrf())
                        .param("applicantName", "Morgan Vance")
                        .param("annualIncome", "85000.00")
                        .param("monthlyCarPayment", "400.00")
                        .param("monthlyMortgagePayment", "1200.00")
                        .param("yearsEmployed", "5")
                        .param("monthlyOtherDebt", "100.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("expert_system"))
                .andExpect(content().string(containsString("DECISION: APPROVED")));
    }
}
