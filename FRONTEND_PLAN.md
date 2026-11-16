# StratOps 360 - Frontend Rebuild Plan (Calm Editorial Enterprise)

## Executive Summary
Rebuild the entire user interface of **StratOps 360** (Spring Boot 3.3.4 + Thymeleaf) to faithfully match the **Calm Editorial Enterprise** design system exported in `stitch_stratops_360_enterprise_mis/calm_editorial_enterprise/DESIGN.md`.

All fake/sample data in the Stitch HTML prototypes will be ignored; every screen binds to the real backend Spring Boot Thymeleaf models, DTOs, controllers, and validation rules. All existing test cases (including `SmokeAndRoutesTest`) and backend features will remain 100% intact.

---

## 1. Design System Tokens & Foundations

### Colors
- **Canvas / Background**: `#FAFAFA` (dark: `#111315`)
- **Surface / Cards**: `#FFFFFF` (dark: `#1A1D20`)
- **Structural Lines / Outlines**: `1px solid #EBEBEB` (dark: `1px solid #2A2E33`)
- **Primary Text**: `#111827` (dark: `#F3F4F6`)
- **Secondary / Muted Text**: `#6B7280` (dark: `#9CA3AF`)
- **Interactive Accent (Indigo)**: `#4F46E5` (Hover: `#4338CA`, Active: `#3730A3`, Dark: `#6366F1`)
- **Accent Subtle**: `#EEF2FF` / `#D9DFF5` (dark: `rgba(99, 102, 241, 0.15)`)

### Status Indicators (No Saturated Badges or Pills)
- **Rule**: Exclusively signaled via **6px circular dot** (`.status-dot`) accompanied by neutral label text (`#111827` or `#6B7280`).
- **Success (On Track)**: `#16A34A`
- **Warning**: `#D97706`
- **Critical / Danger**: `#DC2626`

### Typography & Radii
- **Font**: Inter with system fallbacks (`font-feature-settings: "tnum" 1` for all numbers/tables).
- **Scale**: Hero numbers 32-40px light (`300`/`400`), Page title 28px/600, Section title 16px/600, Body 14px, Labels 13px, minimum floor 12px.
- **Corner Radius**: 10px (`--radius: 10px`) for cards, buttons, input fields; 9999px for status dots.
- **Elevation**: Shadowless (`box-shadow: none`). Spatial depth is established solely through 1px hairline borders (`#EBEBEB`) and white surfaces on off-white canvas.

### Layout Architecture
- **Fixed Left Sidebar**: 220px wide (`bg-surface`, `border-right: 1px solid var(--border-hairline)`).
  - 6 navigation links: Dashboard, Decisions, Simulation, Expert System, Reports, Theory Guide.
  - Active item: indigo text on subtle background (`--primary-subtle`).
- **Fixed Top Bar**: 64px high, offset 220px to the right (`border-bottom: 1px solid var(--border-hairline)`).
  - Left: Contextual breadcrumbs (`Workspace > StratOps 360`).
  - Right: Quick Search input, "View as" role selector dropdown, and Avatar profile dropdown (with Dark Mode toggle, Viva Demo Tour trigger, and API Docs link).
- **Main Container**: Offset left by 220px, top by 64px. Max width 1440px, 32px padding on desktop, fully responsive.

---

## 2. Technical Stack & Local Offline Assets
- **No CDNs**: Eliminates external CDNs (Bootstrap CSS, FontAwesome, Google Fonts, Tailwind CDN).
- **Custom Hand-Crafted CSS**: `src/main/resources/static/css/style.css` containing complete reset, CSS custom properties, grid layouts, form styling, and dark mode overrides.
- **Local Chart.js**: Bundled locally at `/js/chart.umd.min.js` (205 KB).
- **SVG Icons**: Clean, lightweight inline SVG icons for sidebar, actions, status, and navigation.
- **Client Scripting**:
  - `/js/app.js`: Sidebar mobile drawer, dropdown toggles, dark mode localStorage persistence, toast notifications auto-dismiss, and interactive Viva Demo modal.
  - `/js/charts.js`: Scorecard Radar Chart and trend sparklines with thin 1.5px lines, `#EBEBEB` dashed grid lines, and zero fill gradients.

---

## 3. Template Mapping to Stitch Reference

| Existing Template | Stitch Reference Folder | Purpose & Real Data Bindings |
| :--- | :--- | :--- |
| `templates/fragments/layout.html` | `calm_editorial_enterprise` | Unified layout shell: 220px sidebar, 64px topbar, mobile drawer, toast container, viva modal. |
| `dashboard.html` | `dashboard_balanced_scorecard` | 4 stat blocks (Health, On Track, Warning, Critical), 60/40 row (Radar equilibrium + Needs Attention list), 2x2 perspective KPI grid with sparklines and status dots. |
| `decision_pipeline.html` | `decisions_1` & `decisions_2` | 4-column Herbert Simon board (Intelligence, Design, Choice, Implementation), search filter, priority dots, stage transition actions. |
| `decision_view.html` | `initiative_detail_init_104_...` | 4-stage stepper, alternatives evaluation matrix, add alternative form, advance stage validator guard, feedback log. |
| `decision_new.html` | `calm_editorial_enterprise` | Clean centered 10px-radius form for Stage 1 (Intelligence: Problem Discovery, KPI linkage, owner, budget). |
| `simulation.html` | `what_if_simulation` | Sensitivity sliders (Price, Marketing, Raw Materials, Training), DSS output cards with deltas, strategic insights, saved scenarios table. |
| `simulation_compare.html` | `what_if_simulation` | Multi-scenario comparison matrix side-by-side with metric deltas. |
| `expert_system.html` | `credit_expert_system` | Loan applicant parameters form, high-level decision banner (Approved/Restricted/Rejected with limit), forward-chaining trace steps, topology flowchart, evaluation history. |
| `expert_system_thresholds.html`| `credit_expert_system` | Rule parameter editor (debt-to-income, car-payment ratio, income minimums). |
| `reports.html` | `reports` | Parameterized filters (Department, Perspective, Status), summary counters, exportable data table with status dots, CSV export button. |
| `theory_guide.html` | `theory_guide` | MIS academic syllabus mapping (Herbert Simon, Balanced Scorecard, DSS, Expert Systems) with slide references and Viva Demo launcher. |
| `kpi_new.html` & `kpi_edit.html`| `edit_kpi` | KPI metric editor with inline validation, status preview dot, and historical audit trail. |
| `perspective_drilldown.html` | `dashboard_balanced_scorecard` | Perspective-specific deep dive with KPI cards and historical snapshots. |
| `search_results.html` | `calm_editorial_enterprise` | Global search results matching KPIs and strategic initiatives. |
| `error/404.html` & `error/500.html`| `calm_editorial_enterprise` | Clean, calm editorial error pages with recovery links. |

---

## 4. Verification & Testing Strategy
1. **Compilation**: Run `mvn clean compile` to ensure zero compilation or template syntax issues.
2. **Automated Unit & Integration Tests**: Run `mvn clean test` to ensure all 42 tests pass with zero regressions.
3. **Smoke & Endpoint Verification**: Ensure root `/`, `/dashboard`, `/decision/pipeline`, `/simulation`, `/expert-system`, `/reports`, `/theory-guide`, and all POST endpoints return 200 OK.
4. **Offline Resilience**: Verify zero external CDN dependencies in developer tools / network requests.
