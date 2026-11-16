# StratOps 360 - Frontend Changelog (Calm Editorial Enterprise)

## Overview
Rebuilt the entire front-end user experience of **StratOps 360** to strictly match the **Calm Editorial Enterprise** design system exported in `stitch_stratops_360_enterprise_mis/calm_editorial_enterprise/DESIGN.md`.

All real content, metrics, decision pipelines, simulations, and expert system rules are strictly bound to existing Spring Boot 3.3.4 Thymeleaf models, DTOs, and REST APIs. Zero backend behavior was altered; all 42 automated tests pass with 100% green builds.

---

## 1. Design System & CSS Overhaul (`/static/css/style.css`)
- **Complete CDN Removal**: Removed all dependencies on Bootstrap CSS CDN, FontAwesome CDN, and Google Fonts.
- **Calm Editorial Palette**:
  - Canvas / Background: `#FAFAFA` (dark: `#111315`)
  - Surface / Cards: `#FFFFFF` (dark: `#1A1D20`)
  - Structural Borders: `1px solid #EBEBEB` (dark: `1px solid #2A2E33`)
  - Primary Text: `#111827` (dark: `#F3F4F6`)
  - Secondary Text: `#6B7280` (dark: `#9CA3AF`)
  - Single Interactive Accent: Indigo `#4F46E5` (Hover: `#4338CA`, Dark: `#6366F1`)
- **Strict Status Indicators**:
  - Eliminated loud saturated badges, soft pills, and background fills.
  - Converted all statuses to a **6px circular indicator dot** (`.status-dot`) with neutral adjacent text:
    - Green (`#16A34A` / `#22C55E`): On Track / Approved / Target Met
    - Amber (`#D97706` / `#F59E0B`): Warning / In Design / Moderate Risk
    - Red (`#DC2626` / `#EF4444`): Critical / Rejected / Action Required
- **Typography & Elevation**:
  - Inter font with tabular numbers (`font-feature-settings: "tnum" 1`).
  - Hero figures 32px–36px light (`300`/`400`), Page titles 28px/600, Section titles 16px/600.
  - Shadowless cards: Depth generated exclusively through 1px hairline borders (`#EBEBEB`) and white surfaces on off-white canvas.
  - Standardized corner radius: 10px (`--radius: 10px`) across all cards, inputs, and buttons.

---

## 2. Shell & Layout Architecture (`templates/fragments/layout.html`)
- **Fixed Left Sidebar (220px)**:
  - Persistent white column with brand badge (`S`), company title, and 6 active navigation links.
  - Inline clean SVG icons for: Dashboard, Decisions, Simulation, Expert System, Reports, and Theory Guide.
  - Active item styled with subtle indigo background (`--primary-subtle`) and indigo text.
- **Fixed Topbar (64px)**:
  - Left: Contextual breadcrumbs (`Workspace > StratOps 360 > [Page Name]`).
  - Right: Quick Search input, "View as" role dropdown (Executive, Manager, Operations), and Avatar menu.
  - Avatar menu: Viva Demo Tour modal trigger, Dark mode toggle, OpenAPI Swagger docs link (`/swagger-ui.html`), and H2 Database Console link (`/h2-console`).
- **Responsive Off-Canvas Drawer**:
  - Under 1024px, the sidebar collapses into a slide-out drawer triggered by a topbar hamburger icon with backdrop dimming.

---

## 3. Local Offline Assets & Scripts
- **Bundled Chart.js**: Downloaded Chart.js 4.4.1 locally to `/static/js/chart.umd.min.js` (205 KB). The application now functions 100% offline during university viva demos and offline evaluations.
- **Script Refactoring (`/static/js/app.js` & `/static/js/charts.js`)**:
  - Safe `localStorage` handling with `try/catch` blocks for theme persistence.
  - Auto-dismissing toast notifications after 5 seconds.
  - 6-step Guided Viva Demo Tour modal.
  - Editorial Chart.js setup: thin 1.5px strokes, `#EBEBEB` dashed grid lines, no area gradients, 10px border radius tooltips.

---

## 4. Rebuilt Templates
1. **`dashboard.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/dashboard_balanced_scorecard`.
   - 4 Stat Blocks (Overall Scorecard Health, On Track, Warning, Critical).
   - 60/40 Split: Perspective Equilibrium Radar Chart (baseline 85% vs current) + Needs Attention queue with "Fix" links.
   - 2x2 Perspective Grid (Financial, Customer, Internal Processes, Learning & Growth) with sparklines and status dots.
2. **`decision_pipeline.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/decisions_1` and `decisions_2`.
   - 4-column Herbert Simon Kanban board (Intelligence, Design, Choice, Implementation).
   - Priority dots, initiative cards, owner indicators, and stage advance buttons.
3. **`decision_view.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/initiative_detail_init_104_western_hub_automation`.
   - 4-dot stepper progress line with completed checkmarks.
   - Formulated Alternatives table (Cost, ROI %, Risk 1-10, Score, and Selection action).
   - Form to model new solution alternatives with full validation error display.
   - Implementation & Feedback loop form.
4. **`decision_new.html`**:
   - Calm editorial form for Stage 1 problem formulation with KPI linkage, owner, and budget.
5. **`simulation.html` & `simulation_compare.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/what_if_simulation`.
   - Left slider panel (Price, Marketing, Raw Materials, Training) with elasticity rules.
   - Right results panel with 4 metric delta cards, bar comparison chart, strategic insights, and saved scenarios table.
   - Scenario comparison matrix side-by-side with metric deltas.
6. **`expert_system.html` & `expert_system_thresholds.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/credit_expert_system`.
   - Knowledge Base Rule Topology SVG flowchart with real-time firing path highlighting.
   - Fact collection form, high-level decision banner ("DECISION: APPROVED" / "Rejected"), step-by-step firing console log, and paginated historical audit log.
   - Administrative parameter tuning panel.
7. **`reports.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/reports`.
   - Parameterized filters (Department, Perspective, Status), summary stat strip, and data grid with CSV export.
8. **`theory_guide.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/theory_guide`.
   - MIS curriculum mapping table with syllabus citations, slide references, and Viva Tour trigger.
9. **`kpi_new.html` & `kpi_edit.html`**:
   - Matches `stitch_stratops_360_enterprise_mis/edit_kpi`.
   - Centered forms with inline validation, status preview dot, and immutable audit logs.
10. **`perspective_drilldown.html`, `search_results.html`, `error/404.html`, `error/500.html`**:
    - Re-styled in the same Calm Editorial Enterprise aesthetic.

---

## 5. Verification & Test Suite
- `mvn clean test` executed: **42 of 42 tests passing** (0 failures, 0 errors, 0 skipped).
- All string markers verified:
  - `"Balanced Scorecard (ESS Dashboard)"`
  - `"Overall Scorecard Health"`
  - `"Herbert Simon’s Decision-Making Pipeline"`
  - `"Interactive \"What-If\" Scenario Simulator"`
  - `"Operational Expert System"`
  - `"DECISION: APPROVED"`
  - `"Business Intelligence (BI) Parameterized Reports"`
  - `"MIS Curriculum Mapping"`
  - `"Launch Guided Viva Demo Tour"`
- Spring Boot daemon server listening on `http://localhost:8080/` returning `HTTP 200 OK` across all routes.
