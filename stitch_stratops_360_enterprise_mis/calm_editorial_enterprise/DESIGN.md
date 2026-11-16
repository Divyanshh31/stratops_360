---
name: Calm Editorial Enterprise
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#464555'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f0f1f1'
  outline: '#777587'
  outline-variant: '#c7c4d8'
  surface-tint: '#4d44e3'
  primary: '#3525cd'
  on-primary: '#ffffff'
  primary-container: '#4f46e5'
  on-primary-container: '#dad7ff'
  inverse-primary: '#c3c0ff'
  secondary: '#575e70'
  on-secondary: '#ffffff'
  secondary-container: '#d9dff5'
  on-secondary-container: '#5c6274'
  tertiary: '#7e3000'
  on-tertiary: '#ffffff'
  tertiary-container: '#a44100'
  on-tertiary-container: '#ffd2be'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#0f0069'
  on-primary-fixed-variant: '#3323cc'
  secondary-fixed: '#dce2f7'
  secondary-fixed-dim: '#c0c6db'
  on-secondary-fixed: '#141b2b'
  on-secondary-fixed-variant: '#404758'
  tertiary-fixed: '#ffdbcc'
  tertiary-fixed-dim: '#ffb695'
  on-tertiary-fixed: '#351000'
  on-tertiary-fixed-variant: '#7b2f00'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-hero:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '300'
    lineHeight: 48px
    letterSpacing: -0.02em
  display-hero-mobile:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '300'
    lineHeight: 40px
    letterSpacing: -0.015em
  metric-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
    letterSpacing: -0.015em
  page-title:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  page-title-mobile:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.015em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  section-title:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-default:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-medium:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0em
  label-muted:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0.005em
  label-medium:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.005em
  label-caption:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.01em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1.5rem
  gutter-mobile: 1rem
  margin: 2rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
  space-2xl: 2.5rem
---

## Brand & Style

This design system embodies quiet confidence, meticulous typography, and distilled clarity. Drawing inspiration from the craft of Linear, the document fluidity of Notion, the technical precision of Vercel, and the disciplined refinement of Apple, it treats enterprise tooling as an editorial workspace rather than an administrative console.

### Brand Personality
- **Restrained & Authoritative:** Communicates capability through structure and negative space rather than decoration.
- **Calm & Unhurried:** Eliminates chromatic noise and unnecessary visual artifacts to lower cognitive load during long working sessions.
- **Tactile & Precise:** Employs razor-thin hairline borders, strict typographic rhythm, and deliberate contrast to establish order.

### Design Aesthetic
The system relies on a **Minimalist Editorial** architecture: stark white surfaces resting on an off-white canvas, connected by crisp 1px borders, unyielding typographic hierarchies, and surgical micro-accents. Color is reserved almost exclusively for intentional interaction states and micro-status indications.

## Colors

The palette operates under absolute chromatic restraint. Primary surfaces remain paper-clean, letting structural text and data lead the user experience.

### Palette Architecture
- **Canvas / Background:** `#FAFAFA` — An ultra-light neutral ground that anchors the workspace without the clinical glare of pure white.
- **Surface / Cards:** `#FFFFFF` — Elevated base for interactive containers, forms, and data tables.
- **Structural Lines / Outlines:** `#EBEBEB` — 1px hairline delimiters that define containment without visual clutter.
- **Deep Text (High Contrast):** `#111827` — Pure legibility for primary headings, metric values, and active interface elements.
- **Muted Text (Secondary):** `#6B7280` — Mid-contrast graphite tone for secondary copy, column labels, and metadata.
- **Accent (Interactive Primary):** `#4F46E5` (Indigo) — Deliberately isolated for primary calls-to-action and focused interactive states.

### Status Indicators
Status signals must avoid saturated fills, pills, or alert blocks. Status is exclusively signaled via:
- **Small 6px indicator dots** or **1.5px hairline top/left accents**.
- **Success:** `#16A34A`
- **Warning / Pending:** `#D97706`
- **Critical / Danger:** `#DC2626`
- **Text Companion:** Status text alongside indicators must remain `#111827` or `#6B7280`, never colorized.

## Typography

The typographic hierarchy prioritizes editorial rhythm and scanning velocity using the **Inter** family.

### Rules of Application
- **Hero & Primary Metrics:** Large figures (32px to 40px) must be rendered in light (`300`) or regular (`400`) weights with tight negative tracking (`-0.02em`) to mirror luxury timepieces and editorial financial publications.
- **Section & Page Headings:** Set in semibold (`600`) to anchor structure against dense tabular layouts.
- **Scale Floor:** Typographic sizing must never fall below `12px`. Muted labels and secondary data attributes sit at `13px` to balance density with long-session eye comfort.
- **Tabular Figures:** All financial counters, metrics, and data tables must utilize `font-feature-settings: "tnum" 1` for consistent vertical column alignment.

## Layout & Spacing

The layout is constructed on an exact **8px baseline grid** governed by an ethos of uncompressed white space.

### Structural Rhythm
- **Canvas Margins:** Fixed at `2rem` (32px) on desktop viewpoints; compressed to `1rem` (16px) on mobile viewports.
- **Section Breaks:** Minimum inter-section separation is `32px` (`space-xl`), scaling up to `40px` (`space-2xl`) for major thematic shifts.
- **Container Padding:** Standard cards utilize an internal inset of `24px` (`space-lg`) on compact dashboards, scaling to `32px` (`space-xl`) on expansive editorial views.
- **Fluid Desktop Grid:** 12-column layout with 24px gutters, constrained to a maximum content width of 1440px to protect line-length readability.

## Elevation & Depth

This design system is entirely **shadowless**. 

### Surface Differentiation
- **Zero Drop Shadows:** Depth is produced through tonal separation and bounding lines rather than ambient blur or diffused elevations.
- **The Hairline Edge:** Surfaces (`#FFFFFF`) are layered over the canvas (`#FAFAFA`) using a uniform, razor-sharp `1px solid #EBEBEB` border.
- **Floating Overlays:** Menus, dropdowns, and modal dialogs retain the `#FFFFFF` ground and `1px solid #EBEBEB` border. Modals employ an unobtrusive dimming backdrop (`rgba(17, 24, 39, 0.2)`) with a gentle backdrop blur (`4px`) to preserve structural clarity without heavy scrims.

## Shapes

Containers, interactive elements, and layout surfaces share a standardized corner geometry.

### Radius Rules
- **Base Containers & Cards:** Strictly defined at **10px** (`ROUND_TEN`). This specific curvature softens the technical edge while preserving an architecturally disciplined silhouette.
- **Buttons & Input Controls:** Harmonized at **10px** for uniform visual rhythm across form clusters.
- **Status Markers & Dots:** True geometric circles (`50%` / 9999px) strictly for 6px status indicators.
- **Hairline Dividers:** 1px stroke with non-rounded, flush endpoints.

## Components

Every component adheres strictly to the system's low-noise, high-precision aesthetic.

### Buttons
- **Primary Button:** Solid `#4F46E5` fill, `#FFFFFF` text, `10px` radius, 14px Medium typography. Padding: `8px 16px`. Zero elevation/shadow. Hover: `#4338CA`. Active: `#3730A3`.
- **Secondary / Ghost Button:** Transparent background, `#111827` text, `1px solid #EBEBEB` border, `10px` radius. Hover: `#FAFAFA` background with `#111827` text.
- **Text Link Button:** No background, no border, 14px Medium. Text is `#6B7280`, transitioning to `#111827` on hover.

### Inputs & Form Fields
- **Container:** Background `#FFFFFF`, border `1px solid #EBEBEB`, `10px` border radius, height `40px`, padding `0 12px`.
- **Typography:** 14px Regular, text `#111827`. Placeholder text `#6B7280`.
- **Focus State:** Border shifts cleanly to `1px solid #4F46E5`. No glow rings or heavy drop shadows.

### Cards & Panels
- **Structure:** Background `#FFFFFF`, border `1px solid #EBEBEB`, corner radius `10px`.
- **Padding:** Desktop default is `24px` to `32px`.
- **Header Structure:** Section headers inside cards sit above clean dividing lines (`1px solid #EBEBEB`) or float within generous whitespace without separators.

### Data Visualizations & Charts
- **Lines:** Single-pixel to 1.5px hairline strokes. Absolutely no area gradients or filled fades beneath series.
- **Grid Lines:** `1px` horizontal grid lines colored `#EBEBEB`, dashed with a `4px 4px` pattern. No vertical grid lines.
- **Axes & Ticks:** 12px muted text (`#6B7280`). Ticks and axis lines are omitted or drawn with a subtle `1px solid #EBEBEB`.
- **Tooltips:** `#FFFFFF` background, `1px solid #EBEBEB` perimeter, `10px` radius, 13px typography. No drop shadow.

### Status Badges & Indicators
- **Standard Format:** An inline group containing a single 6px circular dot followed by 13px text (`#111827` or `#6B7280`).
- **Dot Colors:** Green (`#16A34A`), Amber (`#D97706`), or Red (`#DC2626`).
- **Prohibition:** Fully saturated badge fills, soft pill backgrounds, and loud colored tags are strictly forbidden.

### Selection Controls (Checkboxes & Radios)
- **Checkboxes:** 16x16px, `1px solid #EBEBEB` resting border, 4px border radius. Checked state fills with `#4F46E5` displaying a crisp white 1.5px checkmark icon.
- **Radio Buttons:** 16x16px circle, `1px solid #EBEBEB`. Selected state contains an inner 6px circle filled with `#4F46E5`.