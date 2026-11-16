# CHANGELOG: StratOps 360 Enterprise Refactoring & Modernization

All notable changes to the **StratOps 360** Management Information System (MIS) enterprise application are documented in this file.

---

## [2.3.0] - Role-Based Access Control (RBAC) & MIS Organizational Hierarchy Access Restrictions

### 1. Executive Summary
Implemented end-to-end Role-Based Access Control (RBAC) grounded in **Robert Anthony's Triangle** and **Kenneth C. Laudon's MIS Management Hierarchy** (Units 2 & 4). The user can seamlessly toggle between organizational tiers (`Executive`, `Manager`, `Operations`) with persistent session + cookie authorization, visual indicator badges, and strict backend permission gates that prevent lower tiers from executing strategic actions.

### 2. Management Level Permissions Breakdown

| Role | MIS Management Level | System Classification | Permitted Capabilities | Enforced Restrictions |
|---|---|---|---|---|
| **Executive** | Strategic Level | Executive Support System (ESS) | Macro scorecard performance review, high-level deviation monitoring, What-If sensitivity forecasting & strategic scenario planning. | Read-only tactical operations; strategic target tuning. |
| **Manager** | Tactical Level | Management Information System (MIS) & Decision Support System (DSS) | Full administration: KPI target modification, registering new scorecard metrics, advancing Herbert Simon 4-stage decision pipeline, adding alternatives, credit parameter tuning. | None (Full tactical authorization). |
| **Operations** | Operational Level | Transaction Processing System (TPS) | Day-to-day operational monitoring, routine applicant financial fact entry for forward-chaining credit inference. | **Strictly Restricted**: Locked from creating/editing KPIs, locked from advancing decision pipeline stages or adding alternatives, locked from tuning expert engine rule thresholds, locked from saving simulation scenarios. |

### 3. Key Components Implemented
* **`RoleContextAdvice.java` (`@ControllerAdvice`):**
  * Injects `currentRole`, `isOperations`, `isManager`, `isExecutive`, and MIS tier metadata (`misTierTitle`, `misTierDescription`) into every Thymeleaf template.
  * Synchronizes across session attributes and persistent `stratops_role` cookies.
* **`RoleSwitchController.java`:**
  * Handles `/role/switch` (GET & POST) and `/role/api/switch`.
  * Normalizes role selections, issues flash notifications, sets 30-day cookies, and redirects back to the originating page.
* **Backend Security Guards:**
  * `DashboardController.java`: Rejects Operations role on `/kpi/new`, `/kpi/create`, `/kpi/edit/{id}`, `/kpi/save`, and `/kpi/deactivate/{id}`.
  * `DecisionController.java`: Rejects Operations role on `/decision/new`, `/decision/create`, `/decision/add-alternative`, `/decision/select-alternative`, `/decision/advance/{id}`, and `/decision/feedback/{id}`.
  * `ExpertSystemController.java`: Rejects Operations role on `/expert-system/thresholds` and `/expert-system/thresholds/save`.
  * `SimulationController.java`: Rejects Operations role on `/simulation/run` (when saving scenarios).
* **UI/UX & Design Consistency:**
  * High-contrast dropdown menu with distinct selection indicators in both light and dark themes.
  * Topbar active role badge (`Operational (Restricted)` in red, `Tactical (MIS/DSS)` in indigo, `Strategic (ESS)` in purple).
  * Prominent red TPS Warning Banner on restricted pages explaining Anthony's Triangle principles.
  * Action buttons (`+ New KPI`, `+ New Initiative`) rendered disabled with `cursor: not-allowed`.
  * Scorecard KPI rows render `🔒 Locked` pill badges instead of `Edit` links in Operations mode.
  * Expert System "Knowledge Base Rule Topology" flowchart refined with dark charcoal node fills (`#22262B`), subtle borders (`#363B42`), and semantic status lines/markers.

---

## [2.0.0] - Comprehensive Architectural, Security, Feature & UI/UX Overhaul

### 1. Executive Summary
StratOps 360 has been elevated from an academic proof-of-concept into an enterprise-grade, demo-ready Decision Support System (DSS) and Balanced Scorecard platform. The modernization strictly preserves 100% academic fidelity to the MIS theoretical frameworks (Kenneth C. Laudon & Herbert A. Simon), while adhering to clean software engineering principles: constructor injection, Bean Validation DTOs, modular rule engines, database-level indices and audit trails, multi-profile security, and responsive UI design tokens.

---

### 2. Detailed Changes by Functional Category

#### A. Code Quality & Software Architecture
* **Dependency & Constructor Injection:**
  * Replaced all field-level `@Autowired` injections across all controllers and services (`DashboardController`, `DecisionController`, `SimulationController`, `ExpertSystemController`, `ReportsController`, `RestApiController`, `KpiService`, `DecisionService`, `SimulationService`, `ExpertSystemEngine`) with clean, final constructor injection.
  * Marked read operations with `@Transactional(readOnly = true)` to avoid unnecessary Hibernate dirty checking and improve query concurrency.
* **Separation of Concerns & Seed Data Decoupling:**
  * Extracted all inline mock data from service `@PostConstruct` methods into a centralized `DataInitializer` class implementing Spring Boot's `CommandLineRunner`.
  * Initializer pre-seeds realistic enterprise data across all four Kaplan-Norton perspectives, Simon decision initiatives, sample alternatives, credit evaluations, and default rule thresholds.
* **DTO Layer & Form-Backing Objects:**
  * Created dedicated Data Transfer Objects backed by Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, `@Digits`):
    * `KpiFormDto`: Validates KPI code, name, target, current value, perspective, unit, and directionality.
    * `DecisionInitiativeFormDto`: Enforces problem formulation and SLA gap definitions for Stage 1 (Intelligence).
    * `AlternativeFormDto`: Form-backing object for Stage 2 (Design) alternatives with capex, benefit, and risk scores.
    * `CreditEvaluationFormDto`: Validates annual income, monthly debt payments, and employment tenure.
    * `SimulationFormDto`: Bounded sliders (-30% to +30%) for What-If scenario forecasting.
    * `RuleThresholdConfigFormDto`: Admin-backed runtime threshold customization.
* **Modular Rule Engine Architecture (`com.stratops.stratops360.rule`):**
  * Refactored `ExpertSystemEngine` from a monolithic procedural `if-else` block into an extensible, object-oriented Forward-Chaining Rule Engine:
    * `CreditRule` interface with methods `getCode()`, `getDescription()`, `evaluate()`, and `getOrder()`.
    * Dedicated rule implementations: `IncomeThresholdRule`, `CarPaymentRatioRule`, `MortgagePaymentRatioRule`, `EmploymentTenureRule`, `SecondaryDebtRatioRule`.
    * `EvaluationContext` encapsulates applicant inputs and dynamic thresholds.
    * `InferenceStep` records structured execution data (rule code, premise evaluated, outcome, fired timestamp, human explanation).
    * Preserves exact Slide 9 credit decision logic, thresholds, and terminal states ($A \rightarrow B \rightarrow C \rightarrow D \rightarrow E \rightarrow F/I$).
* **Domain Financial Hygiene:**
  * Replaced floating-point representations with `BigDecimal` in monetary and ratio calculations within the Expert System and Simulator.
  * Added division-by-zero guards across all ratio calculations (e.g., zero annual income guard).
* **Entity Integrity (No Lombok):**
  * Authored clean, standard getters, setters, and explicit `equals()` and `hashCode()` implementations based on business keys across all entities (`KpiMetric`, `DecisionInitiative`, `DecisionAlternative`, `CreditRuleEvaluation`, `RuleThresholdConfig`, `KpiHistorySnapshot`, `KpiAuditLog`, `SavedScenario`).
* **Global Exception Handling:**
  * Created `GlobalExceptionHandler` annotated with `@ControllerAdvice`.
  * Handles `ResourceNotFoundException`, `InvalidDecisionTransitionException`, and validation errors with user-friendly error views (`templates/error/404.html` and `templates/error/500.html`).

#### B. Validation, Security & Robustness
* **Spring Security & CSRF Protection:**
  * Integrated `spring-boot-starter-security` with a custom `SecurityConfig`.
  * Configured CSRF protection on all mutating POST forms.
  * Configured security headers (Content Security Policy, HTTP Strict Transport Security, X-Frame-Options set to `sameOrigin` to permit developer H2 console access).
* **Multi-Environment Spring Profiles:**
  * `application.properties`: Shared base settings, Actuator endpoint configuration, default profile set to `dev`.
  * `application-dev.properties`: In-memory H2 database (`jdbc:h2:mem:stratopsdb`), H2 console enabled at `/h2-console`, debug logging.
  * `application-prod.properties`: File-backed H2 storage (`jdbc:h2:file:./data/stratopsdb`), H2 console disabled, gzip compression enabled (`server.compression.enabled=true`), static asset caching headers.
* **Post/Redirect/Get (PRG) & User Alerts:**
  * Standardized all controller POST actions to adhere strictly to the Post/Redirect/Get pattern.
  * Integrated `RedirectAttributes` flash alerts rendered through a global Bootstrap toast container (`successMessage`, `errorMessage`).
* **Offline Viva Resilience:**
  * Pinned external CDN versions with Subresource Integrity (SRI) attributes for Bootstrap 5.3, FontAwesome 6.4.2, and Chart.js 4.4.1.

#### C. Enterprise Features & MIS Academic Depth
* **Executive Balanced Scorecard (`/dashboard`):**
  * Added period filter dropdown (`2025-Q3`, `2025-Q4`, `2026-Q1`, `2026-Q2`, `Full Year 2026`).
  * Integrated multi-axial Radar Alignment Chart visualizing cross-perspective organizational health.
  * Historical sparklines indicating multi-quarter trajectory for each KPI.
  * Accessible color-coded status badges utilizing both icons and text (WCAG AA compliant).
* **KPI Management & Governance:**
  * Full CRUD workflow: "+ New KPI" creation modal/page (`/kpi/new`), update page (`/kpi/edit/{id}`), and soft deactivation/archiving.
  * Comprehensive audit logging (`KpiAuditLog`) capturing user modifications, old values, new values, and revision timestamps.
* **Herbert Simon 4-Stage Decision Pipeline (`/decision/pipeline`):**
  * Interactive Kanban-style board representing Simon's stages: *1. Intelligence*, *2. Design*, *3. Choice*, *4. Implementation*.
  * Enforced rigorous academic transition guards:
    * **Stage 1 to 2:** Cannot advance without problem description and quantifiable target discrepancy.
    * **Stage 2 to 3:** Cannot advance to Choice without at least 2 distinct alternatives formulated.
    * **Stage 3 to 4:** Cannot advance to Implementation without an approved, selected winning alternative.
    * **Stage 4 to Complete:** Cannot close the initiative without documenting empirical operational feedback.
  * Decision alternative analytics displaying **Risk-Adjusted Score** ($\text{ROI} / \text{Risk}$) side-by-side with capex and net financial benefit.
* **DSS What-If Sensitivity Simulator (`/simulation`):**
  * Bounded sliders controlling 4 strategic levers: Price Adjustment, Marketing Spend, Raw Material Shock, Employee Training.
  * Scenario persistence: ability to name, describe, and save What-If scenarios into database storage (`SavedScenario`).
  * **Side-by-Side Scenario Comparison Matrix (`/simulation/compare`):** Multi-column comparison of up to 3 distinct scenarios evaluating revenue, churn, operating profit, and delivery impacts.
  * Sensitivity and model assumptions documentation grounded in microeconomic principles (e.g., price elasticity of demand = -1.2, marketing coefficient = +0.45).
* **Credit Evaluation Expert System (`/expert-system`):**
  * Dynamic SVG Credit Flowchart with CSS firing-path highlighting indicating the active route traversed during evaluation.
  * Paginated evaluation audit history with filtering and one-click CSV export (`/expert-system/export-csv`).
  * Administrative threshold configuration panel (`/expert-system/thresholds`) allowing runtime tuning of credit limits, income ceilings, and debt ratios.
* **BI Reports Subsystem (`/reports`):**
  * Parameterized filtering across Perspective, Department, and Operational Status.
  * Dynamic calculation of perspective averages and risk metrics.
  * One-click CSV export (`/reports/export-csv`) streaming formatted tabular records.
* **Enterprise Global Search (`/search`):**
  * Cross-functional keyword search querying KPIs, Simon decision initiatives, and historical credit evaluations simultaneously.
* **REST API Layer & OpenAPI Specification (`/api/v1/...`):**
  * Implemented REST controllers exposing JSON endpoints for scorecard summary, KPI listing, decision pipeline, and credit evaluation.
  * Interactive Swagger UI documentation available at `/swagger-ui.html` and `/v3/api-docs`.

#### D. UI/UX Design System & Accessibility
* **Theme & Design Tokens (`/static/css/style.css`):**
  * Modern CSS custom property tokens: primary brand colors, dark navy tones, soft status tints, card elevations, border radii.
  * Full Dark/Light mode toggle persisted in browser `localStorage`.
  * WCAG AA accessibility compliance: high-contrast text, clear focus rings, semantic markup.
  * Print-friendly stylesheet (`@media print`) optimizing dashboards and BI reports for paper export.
* **Frontend Architecture (`/static/js/`):**
  * Decoupled script files: `app.js` (UI listeners, toast alerts, theme persistence, viva tour) and `charts.js` (Chart.js life-cycle management, destroy/re-render handlers).
  * **Guided "Viva Demo Mode":** A 6-step interactive walkthrough guiding external evaluators through the core theoretical frameworks directly from the presentation tips.

#### E. Testing & Quality Assurance
* **42 Unit & Integration Tests (100% Pass Rate):**
  * `DecisionServiceTest` (6 tests): Validates Simon stage transition guards, multi-alternative requirements, selection rules, and feedback requirements.
  * `ExpertSystemEngineTest` (10 tests): Validates all forward-chaining branches, exact Slide 9 boundaries ($50,000 income, 10% car debt, 20% mortgage debt, 4-year tenure, 5% secondary debt), division-by-zero handling, and CSV formatting.
  * `KpiServiceTest` (5 tests): Validates higher-is-better and lower-is-better status transitions, audit log recording, and scorecard summary weighting.
  * `SimulationServiceTest` (4 tests): Validates baseline forecasting, elasticity impacts, marketing expansion, and scenario persistence.
  * `SmokeAndRoutesTest` (17 tests): Full MockMvc integration suite verifying all GET web routes, POST form submissions, validation failure rejections, and REST API endpoints.
* **Code Coverage Verification:**
  * JaCoCo test coverage audit: **74% line coverage** and **72% instruction coverage** on services (exceeding the 70% project target), with 93% coverage on rule components.

#### F. DevOps & Containerization
* **Multi-Stage Dockerfile:**
  * Multi-stage build with `maven:3.9.6-eclipse-temurin-21-alpine` builder and `eclipse-temurin:21-jre-alpine` runtime.
  * Runs as an unprivileged non-root user (`appuser:appgroup`).
  * Built-in container health check against Spring Boot Actuator (`/actuator/health`).
* **Docker Compose (`docker-compose.yml`):**
  * Container definition with production environment overrides and memory/CPU limits.
* **Continuous Integration (`.github/workflows/ci.yml`):**
  * Automated GitHub Actions pipeline building, testing, and archiving JaCoCo coverage reports on Java 21.

---

### 3. Decisions & Academic Assumptions
1. **Rule Engine Integrity:** All mathematical formulas, decision branching, and terminal states ($A \rightarrow B \rightarrow C \rightarrow D \rightarrow E \rightarrow F/I$) from Unit 5 Slide 9 were maintained verbatim. The refactoring strictly improved code modularity without altering credit qualification logic.
2. **Simon Pipeline Guards:** In accordance with Herbert Simon's decision science, moving to the "Choice" phase is logically invalid without alternative solutions to choose from; hence a minimum of 2 alternatives is enforced in code. Similarly, closing an initiative requires post-implementation feedback to close the cybernetic loop.
3. **What-If Elasticity Coefficients:** Microeconomic elasticity parameters (price elasticity = -1.2, marketing responsiveness = +0.45) were preserved from standard managerial economics models to ensure realistic sensitivity trade-offs.

---

## [2.1.0] - Calm Editorial Enterprise UI/UX Redesign & Multi-Viewport Audit

### 1. Visual & Front-End Transformation
* **Calm Editorial Enterprise Design Tokens:** Rebuilt the entire user interface based on the Stitch Calm Editorial Enterprise system (`stitch_stratops_360_enterprise_mis`):
  * Crisp 1px hairline borders (`#EBEBEB` light / `#2A2E33` dark), shadowless cards, 10px corner radius, and generous 32px desktop padding.
  * Calm status indicators: replaced heavy saturated background pills with discrete 6px color dots + text labels (`On Track`, `Warning`, `Critical`).
  * 100% offline self-containment: eliminated all CDN links (Bootstrap, Font Awesome, Google Fonts) in favor of bundled local Inter font, locally served Chart.js 4.4.4, and embedded inline SVGs.
* **Persistent Dark Mode:** Theme switching persisted in browser `localStorage` with immediate attribute hydration (`data-theme="dark"`).

### 2. Multi-Viewport & Responsive Audit Remediation
* **Flexbox Child Minimum Widths (`min-width: 0`):** Fixed mobile horizontal scrollbar blowout where table and SVG children forced parent `.card` containers to stretch beyond the 390px viewport width.
* **Mobile Topbar Reflow:** Streamlined breadcrumb tokens and role selectors at `@media (max-width: 640px)`, eliminating header overflow.
* **Spring Security Eager CSRF Evaluation (`CsrfTokenEagerFilter`):** Resolved `IllegalStateException: Cannot create a session after the response has been committed` on large pages (`expert_system.html` and `decision_view.html`). Eagerly initializes CSRF tokens before template streaming flushes.
* **Spring Boot 3 404 Route Handling:** Explicitly mapped `NoResourceFoundException` and `NoHandlerFoundException` to HTTP 404 in `GlobalExceptionHandler` returning `error/404.html`.
* **Perspective Alias Support:** Mapped plural/alternative path aliases in `DashboardController` (`/perspective/INTERNAL_PROCESSES` -> `INTERNAL_PROCESS`).
* **Typography Enforcement:** Eliminated all sub-12px text across templates, SVGs, and Chart.js scales (enforcing 12px minimum everywhere).
* **Brand Favicon:** Added SVG brand badge icon (`favicon.svg`) and valid `favicon.ico` in `/static/`.

---

## [2.1.1] - Enterprise Role Dropdown & Dark Mode Contrast Fix

### 1. View-as Role Switcher Modernization
* **Replaced Native OS `<select>` with Calm Editorial Dropdown:** Converted the top bar role selector from a native browser `<select>` element to a fully styled enterprise dropdown component (`.view-as-btn`, `.dropdown-menu-calm`, and `.role-item`), matching the Stitch Calm Editorial design specification.
* **Resolved Dark Mode Contrast Bug:** Fixed an issue on Windows Chromium/Edge where native OS `<option>` popups opened with a white background while inheriting light text (`#F3F4F6`), causing unselected options ("Manager", "Operations") to appear as white-on-white invisible text.
* **Active Role State & Checkmark:** Added active state indicators (`.active`), soft indigo tinting (`--primary-subtle`), bold font weight, and SVG checkmarks for the selected role.
* **Interactive Switching & Toasts:** Integrated role state persistence via `localStorage.getItem('stratops_role')`, automatic label updates, and toast feedback notifications (`"Switched to [Role] view"`).
* **Global Form Dark Mode Color Scheme:** Added `color-scheme: light;` to `:root` and `color-scheme: dark;` to `[data-theme="dark"]`, along with explicit `select option { background-color: var(--bg-surface); color: var(--text-primary); }` rules in `style.css` so all native dropdowns throughout the app (reports, KPI forms, period filters) remain high-contrast in dark mode.

### 2. Knowledge Base Rule Topology Flowchart Overhaul
* **Dark Mode Theming:** Replaced hardcoded `fill="#FFFFFF"` on SVG nodes with semantic CSS classes (`.rule-node-box`, `.rule-exit-box`, `.rule-state-f-box`, `.rule-state-i-box`). In Dark Mode, rule nodes render on dark charcoal surfaces (`#22262B`) with subtle borders (`#363B42`), eliminating harsh white glare.
* **Eliminated Serif Font Fallback:** Bound SVG `<text>` elements to `var(--font-family) !important` in `style.css`, ensuring clean sans-serif typography across all browsers without Times New Roman fallback.
* **Continuous Orthogonal Connectors:** Replaced broken/floating lines between `Rule E (Tenure)` and `Rule H (Debt)` with a single continuous orthogonal elbow path.
* **Collision-Free Branch Paths:** Re-routed the `Rule H -> State F` pathway through the open channel between nodes, preventing overlapping lines across `State I`.
* **Semantic Colored Arrowheads:** Added custom colored SVG markers: green for approvals (`State F`), red for reject exits, amber for secondary branches, and muted gray for sequential transitions.
* **Crisp Branch Labels:** Added high-contrast `YES (Tenure ≥ 4)`, `NO`, `YES`, and `NO` path labels to mirror the Slide 9 credit decision logic.


