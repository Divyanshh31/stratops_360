# StratOps 360 &mdash; Architectural & UI/UX Improvement Plan

## 1. Overview & Objective
Transform StratOps 360 into a polished, robust, enterprise-grade academic demonstration platform. Maintain 100% fidelity to Kenneth C. Laudon and Herbert A. Simon's Management Information System (MIS) curriculum, while modernizing the software architecture, user experience, data governance, security, and verification test suite.

---

## 2. Phased Improvement Strategy

### Phase 1: Architecture, Data Modeling & Dependencies (Priority: Critical)
* **Dependency Enhancements (`pom.xml`):**
  * Add `spring-boot-starter-validation`, `spring-boot-starter-cache`, `spring-boot-starter-actuator`.
  * Add `springdoc-openapi-starter-webmvc-ui` for interactive REST API exploration (`/swagger-ui.html`).
  * Add `spring-boot-starter-security` configured with CSRF tokens on all POST requests and secure HTTP headers.
  * Add `jacoco-maven-plugin` for code coverage auditing.
* **Constructor Injection & Service Hygiene:**
  * Refactor all controllers and services to use constructor injection (remove field `@Autowired`).
  * Add `@Transactional(readOnly = true)` to read operations.
* **Seed Data Decoupling:**
  * Extract all mock/seed data from service `@PostConstruct` methods into a dedicated `DataInitializer` implementing `CommandLineRunner`.
* **Domain Model & Entity Enhancements:**
  * Convert financial amounts in expert evaluation and simulation to `BigDecimal` with division-by-zero guards.
  * Implement clean `equals`, `hashCode`, and `toString` without Lombok.
  * Add audit entities: `KpiAuditLog`, `KpiHistorySnapshot` (for true historical time-series), and `SavedScenario`.
  * Add `RuleThresholdConfig` entity to allow runtime administrative customization of expert system thresholds.
* **DTOs & Form-Backing Validation Objects:**
  * Create dedicated DTOs with Bean Validation (`@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Positive`):
    * `KpiFormDto`, `DecisionInitiativeFormDto`, `AlternativeFormDto`, `CreditEvaluationFormDto`, `SimulationFormDto`.
* **Expert System Engine Refactoring:**
  * Replace the monolithic `if/else` block with an extensible, object-oriented Rule pattern:
    * `Rule<T>` interface and `RuleResult`.
    * Ordered rule pipeline: `IncomeThresholdRule`, `CarPaymentRatioRule`, `MortgagePaymentRatioRule`, `EmploymentTenureRule`, `SecondaryDebtRatioRule`.
    * Structured `InferenceStep` trace objects providing both machine-readable audit items and rich human-readable logs.
* **Exception Handling & Global Advice:**
  * Create `GlobalExceptionHandler` with custom 404 (`error/404.html`) and 500 (`error/500.html`) pages.
  * Define domain exceptions (`ResourceNotFoundException`, `InvalidDecisionTransitionException`).

### Phase 2: Profiles, Security, Robustness & Performance (Priority: High)
* **Configuration Profiles:**
  * `application.properties`: Shared base settings, Actuator, banner, default profile = `dev`.
  * `application-dev.properties`: In-memory H2, H2 console enabled, detailed logging.
  * `application-prod.properties`: File-backed H2, H2 console disabled, gzip compression enabled, asset cache headers.
* **Security & Post/Redirect/Get Pattern:**
  * Configure `SecurityConfig` to protect CSRF, inject CSRF tokens in Thymeleaf forms, configure CSP and frame options for H2 console.
  * Implement Post/Redirect/Get (PRG) across all controllers with `RedirectAttributes` flash messages (success/error alerts).
* **Caching & Performance:**
  * Add `@EnableCaching` with Spring Cache for scorecard summaries and KPI aggregations.
  * Add database indexes (`@Index`) on frequently queried columns (`perspective`, `code`, `status`, `stage`).

### Phase 3: Feature Enhancements & Advanced MIS Capabilities (Priority: High)
* **Dashboard & KPI Management:**
  * Add period filtering (Q1, Q2, Q3, Q4, Full Year).
  * Add full KPI CRUD: create new KPI, update existing with audit logs, and soft deactivate.
  * Historical time-series sparklines pulled from `KpiHistorySnapshot`.
* **Decision Pipeline Enhancements:**
  * Implement transition validation guards:
    * Cannot advance from Design to Choice without at least 2 alternatives.
    * Cannot advance from Choice to Implementation without an approved selected choice.
    * Cannot complete without implementation feedback.
  * Display Risk-Adjusted Return ($ROI / Risk$) side by side with Capex and Benefit.
  * Initiative timeline audit trail.
* **What-If Simulation Advanced Features:**
  * Save named scenarios with persistence (`SavedScenario`).
  * Multi-scenario comparison matrix (Side-by-side comparison of up to 3 scenarios).
  * Sensitivity tornado chart / breakdown showing which input has maximum leverage.
* **Expert System Admin & UI Enhancements:**
  * Dynamic rule threshold editing panel (persisted in DB).
  * Visual SVG Credit Flowchart with dynamic highlighting of the active decision path.
  * CSV Export and pagination for credit evaluations.
* **Reports Subsystem:**
  * Parameterized filtering by Department, Perspective, Status, Date Range.
  * Pagination, sorting, and CSV data export.
* **REST API Layer (`/api/v1/...`):**
  * REST endpoints for KPIs, Initiatives, Simulations, and Evaluations.
  * Swagger / OpenAPI 3 documentation at `/swagger-ui.html`.

### Phase 4: UI/UX Redesign, Accessibility & Viva Mode (Priority: High)
* **Design Token Overhaul & Local Assets:**
  * Bundle local offline copies of modern Bootstrap 5, FontAwesome, and Chart.js so the application runs with zero internet dependency during viva.
  * Modern CSS design system with CSS custom properties (`--primary`, `--surface`, `--text-primary`, `--radius`, `--shadow`).
  * Dark / Light mode toggle persisted via `localStorage`.
* **Modular Thymeleaf Fragments:**
  * Break down views into reusable fragments: navbar, footer, toast alerts, KPI cards, status badges, pagination controls, metric sparklines.
  * Move all inline JavaScript and CSS out of templates into dedicated external static files (`/static/js/app.js`, `/static/js/charts.js`).
* **Guided "Viva Demo Mode":**
  * Interactive step-by-step guided walkthrough with callouts highlighting the 6 presentation stages directly from the README.

### Phase 5: Verification, Testing & DevOps (Priority: Critical)
* **Comprehensive Test Suite:**
  * JUnit 5 unit tests for `ExpertSystemEngine` covering all branches and boundary values (e.g. exactly $50,000 income, 10% car debt, 20% mortgage, 4-year tenure, 5% secondary debt).
  * Unit tests for `SimulationService`, `KpiService`, and `DecisionService`.
  * Spring Boot `@WebMvcTest` and `@SpringBootTest` MockMvc integration tests verifying all routes, validation rejections, and security headers.
  * Target $\ge 70\%$ test coverage audited via JaCoCo.
* **DevOps Assets:**
  * Multi-stage `Dockerfile` and `docker-compose.yml`.
  * Comprehensive `.gitignore` and `.editorconfig`.
* **Documentation & Audit:**
  * Updated `README.md` with generic paths, architecture diagram, ER diagram, and viva presentation talking points.
  * Comprehensive `CHANGELOG.md` detailing every enhancement and architectural rationale.
