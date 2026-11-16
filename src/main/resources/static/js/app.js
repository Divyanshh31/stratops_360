/**
 * StratOps 360 - Calm Editorial Enterprise Application Scripts
 * Implements: Theme Toggle, Mobile Sidebar, Profile Dropdown,
 * Toast Notifications, Perspective Filtering, and Guided Viva Demo Tour
 */

// 1. Theme Management (persisted in localStorage)
(function initTheme() {
    try {
        const savedTheme = localStorage.getItem('stratops_theme') || 
            (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
        document.documentElement.setAttribute('data-theme', savedTheme);
        document.documentElement.setAttribute('data-bs-theme', savedTheme);
        updateThemeUI(savedTheme);
    } catch (e) {
        console.warn('localStorage access error', e);
    }
})();

function toggleTheme() {
    try {
        const current = document.documentElement.getAttribute('data-theme') || 'light';
        const next = current === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        document.documentElement.setAttribute('data-bs-theme', next);
        localStorage.setItem('stratops_theme', next);
        updateThemeUI(next);
        
        if (window.onThemeChanged) {
            window.onThemeChanged(next);
        }
    } catch (e) {
        console.warn('Theme toggle error', e);
    }
}

function updateThemeUI(theme) {
    const textEl = document.getElementById('themeToggleText');
    if (textEl) {
        textEl.textContent = theme === 'dark' ? 'Light mode' : 'Dark mode';
    }
}

// 2. DOM Ready Listeners
document.addEventListener('DOMContentLoaded', () => {
    // Initialize subtle live ambient telemetry background (Magic UI & React Bits inspired)
    initLiveAmbientBackground();

    // Profile Dropdown Toggle
    const profileBtn = document.getElementById('profileMenuBtn');
    const profileDropdown = document.getElementById('profileDropdown');
    const roleDropdownMenu = document.getElementById('roleDropdownMenu');
    if (profileBtn && profileDropdown) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            if (roleDropdownMenu) roleDropdownMenu.classList.remove('show');
            profileDropdown.classList.toggle('show');
        });
        document.addEventListener('click', (e) => {
            if (!profileDropdown.contains(e.target) && !profileBtn.contains(e.target)) {
                profileDropdown.classList.remove('show');
            }
        });
    }

    // Role Dropdown Setup
    initRoleSelector();

    // Mobile Sidebar Drawer
    const mobileToggle = document.getElementById('mobileToggleBtn');
    const sidebar = document.getElementById('appSidebar');
    const overlay = document.getElementById('sidebarOverlay');
    if (mobileToggle && sidebar && overlay) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
            overlay.classList.toggle('open');
        });
        overlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            overlay.classList.remove('open');
        });
    }

    // Auto-dismiss Toasts after 5 seconds
    const toasts = document.querySelectorAll('.toast-calm');
    toasts.forEach(toast => {
        setTimeout(() => {
            toast.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(10px)';
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    });
});

// 2b. Role Selector (Enterprise View-as Switcher)
function initRoleSelector() {
    const roleBtn = document.getElementById('roleDropdownBtn');
    const roleMenu = document.getElementById('roleDropdownMenu');
    if (!roleBtn || !roleMenu) return;

    const currentLabel = document.getElementById('currentRoleLabel');
    const currentRole = currentLabel ? currentLabel.textContent.trim() : 'Manager';
    document.documentElement.setAttribute('data-role', currentRole.toLowerCase());

    roleBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        const profileDropdown = document.getElementById('profileDropdown');
        if (profileDropdown) profileDropdown.classList.remove('show');
        const isOpen = roleMenu.classList.toggle('show');
        roleBtn.setAttribute('aria-expanded', isOpen);
    });

    document.addEventListener('click', (e) => {
        if (!roleMenu.contains(e.target) && !roleBtn.contains(e.target)) {
            roleMenu.classList.remove('show');
            roleBtn.setAttribute('aria-expanded', 'false');
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            roleMenu.classList.remove('show');
            roleBtn.setAttribute('aria-expanded', 'false');
            const profileDropdown = document.getElementById('profileDropdown');
            if (profileDropdown) profileDropdown.classList.remove('show');
            closeRoleSwitchModal();
        }
    });
}

// 2b. Role Selector (Enterprise View-as Switcher & Restriction Modal)
const roleClearanceMatrix = {
    Executive: {
        role: 'Executive',
        title: 'Switch to Senior Executive',
        tier: 'Strategic Level (ESS)',
        tierClass: 'badge-role-exec',
        iconClass: 'exec',
        iconSvg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path></svg>',
        tierDesc: 'Executive decision support view for macro scorecard oversight and What-If scenario forecasting.',
        username: 'executive',
        password: 'executive123',
        permitted: [
            'Macro Balanced Scorecard aggregate overview across all 4 perspectives',
            'Cross-Perspective Radar Alignment diagnosis (Kaplan & Norton)',
            'Interactive What-If Sensitivity Forecasting and scenario simulations',
            'Strategic unstructured problem identification and decision review'
        ],
        restricted: [
            'Direct day-to-day KPI target editing (Requires Manager authority)',
            'Routine operational transaction processing (Handled by Operations)'
        ]
    },
    Manager: {
        role: 'Manager',
        title: 'Switch to Department Manager',
        tier: 'Tactical Level (MIS / DSS)',
        tierClass: 'badge-role-mgr',
        iconClass: 'mgr',
        iconSvg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 2 7 12 12 22 7 12 2"></polygon><polyline points="2 17 12 22 22 17"></polyline><polyline points="2 12 12 17 22 12"></polyline></svg>',
        tierDesc: 'Middle Management tactical control over KPI SLA targets, decision phases, and rule thresholds.',
        username: 'manager',
        password: 'manager123',
        permitted: [
            'Full KPI Administration: SLA target modification & new metric creation',
            'Herbert Simon 4-Stage Decision Pipeline: Stage advancement & alternatives',
            'Selection of winning solutions based on Risk-Adjusted ROI scores',
            'Inference Rule Engine runtime threshold customization (/thresholds)',
            'What-If Scenario Persistence & multi-scenario comparison matrices'
        ],
        restricted: [
            'Zero Restrictions: Full tactical management clearance granted'
        ]
    },
    Operations: {
        role: 'Operations',
        title: 'Switch to Operations Officer',
        tier: 'Operational Level (TPS) [Restricted Mode]',
        tierClass: 'badge-role-ops',
        iconClass: 'ops',
        iconSvg: '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>',
        tierDesc: 'Routine frontline operations & telemetry monitoring per Anthony’s Triangle (TPS Level).',
        username: 'operations',
        password: 'operations123',
        permitted: [
            'Routine Operational Telemetry Monitoring on Scorecard Dashboards',
            'Applicant Financial Fact Entry for Forward-Chaining Credit Scoring',
            'Standard Multi-Dimensional BI Performance Reports & CSV Export'
        ],
        restricted: [
            '🔒 Scorecard SLA Target Modification & Metric Deactivation (Locked)',
            '🔒 Registering New Scorecard Metrics & Initiatives (Disabled)',
            '🔒 Advancing Herbert Simon Decision Pipeline Stages (Locked)',
            '🔒 Formulating or Selecting Decision Alternatives (Locked)',
            '🔒 Tuning Knowledge Base Rule Engine Parameters / Thresholds (Locked)',
            '🔒 Saving What-If Simulation Scenarios (Executive/Manager Only)',
            '🚫 Backend Enforcement: Direct URL tampering blocked with 403 / Redirect'
        ]
    }
};

let activeModalRoleKey = 'Operations';

function openRoleSwitchModal(role) {
    const roleMenu = document.getElementById('roleDropdownMenu');
    if (roleMenu) roleMenu.classList.remove('show');

    const modal = document.getElementById('roleModalOverlay');
    if (!modal) {
        selectRole(role);
        return;
    }

    const config = roleClearanceMatrix[role] || roleClearanceMatrix.Manager;
    activeModalRoleKey = config.role;

    // Set Header & Banner
    document.getElementById('modalRoleTitle').textContent = config.title;
    const badgeEl = document.getElementById('modalTierBadge');
    badgeEl.className = config.tierClass;
    badgeEl.textContent = config.tier;
    document.getElementById('modalTierDescription').textContent = config.tierDesc;

    // Set Icon
    const iconBadge = document.getElementById('modalIconBadge');
    iconBadge.className = 'role-modal-icon-badge ' + (config.iconClass || '');
    iconBadge.innerHTML = config.iconSvg;

    // Populate Permitted List
    const permList = document.getElementById('modalPermittedList');
    permList.innerHTML = config.permitted.map(item => `
        <li>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="color: var(--status-green); flex-shrink: 0;"><polyline points="20 6 9 17 4 12"></polyline></svg>
            <span>${item}</span>
        </li>
    `).join('');

    // Populate Restricted List
    const restList = document.getElementById('modalRestrictedList');
    restList.innerHTML = config.restricted.map(item => `
        <li>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="color: var(--status-red); flex-shrink: 0;"><circle cx="12" cy="12" r="10"></circle><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line></svg>
            <span>${item}</span>
        </li>
    `).join('');

    // Pre-populate credentials form
    document.getElementById('modalTargetRoleInput').value = config.role;
    document.getElementById('modalRedirectUrlInput').value = window.location.pathname + window.location.search;
    document.getElementById('modalUsernameInput').value = config.username;
    document.getElementById('modalPasswordInput').value = config.password;

    modal.style.display = 'flex';
}

function closeRoleSwitchModal() {
    const modal = document.getElementById('roleModalOverlay');
    if (modal) modal.style.display = 'none';
}

function handleModalBackdropClick(event) {
    if (event.target === document.getElementById('roleModalOverlay')) {
        closeRoleSwitchModal();
    }
}

function autoFillModalCredentials() {
    const config = roleClearanceMatrix[activeModalRoleKey] || roleClearanceMatrix.Manager;
    document.getElementById('modalUsernameInput').value = config.username;
    document.getElementById('modalPasswordInput').value = config.password;
    showCalmToast('Auto-filled credentials for ' + config.role + ' (' + config.username + ')', 'info');
}

function selectRole(role) {
    try {
        localStorage.setItem('stratops_role', role);
    } catch (e) {}
    
    // Set immediate client-side data attribute
    document.documentElement.setAttribute('data-role', role.toLowerCase());
    
    // Switch server-side session and reload page with new role permissions
    const currentPath = window.location.pathname + window.location.search;
    window.location.href = '/role/switch?role=' + encodeURIComponent(role) + '&redirectUrl=' + encodeURIComponent(currentPath);
}

function updateRoleUI(role) {
    const labelEl = document.getElementById('currentRoleLabel');
    if (labelEl) {
        labelEl.textContent = role;
    }
    const items = document.querySelectorAll('.role-item');
    items.forEach(item => {
        const itemRole = item.getAttribute('data-role');
        const checkEl = item.querySelector('.role-check');
        if (itemRole === role) {
            item.classList.add('active');
            if (checkEl) {
                checkEl.innerHTML = '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>';
            }
        } else {
            item.classList.remove('active');
            if (checkEl) {
                checkEl.innerHTML = '';
            }
        }
    });
}

function showCalmToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = 'toast-calm';
    const dotColor = type === 'success' ? 'var(--status-green)' : (type === 'error' ? 'var(--status-red)' : 'var(--primary)');
    toast.style.borderLeft = `3px solid ${dotColor}`;
    const dotClass = type === 'success' ? 'dot-green' : (type === 'error' ? 'dot-red' : 'dot-indigo');
    toast.innerHTML = `<span class="status-dot ${dotClass}" style="background-color: ${dotColor};"></span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// 3. Perspective Filtering (Balanced Scorecard)
function filterPerspective(perspective, button) {
    document.querySelectorAll('.perspective-filter-btn').forEach(b => b.classList.remove('active'));
    if (button) button.classList.add('active');
    
    const items = document.querySelectorAll('.perspective-kpi-item');
    items.forEach(item => {
        const itemPersp = item.getAttribute('data-perspective');
        if (perspective === 'ALL' || itemPersp === perspective) {
            item.style.display = '';
            item.style.opacity = '0';
            setTimeout(() => {
                item.style.transition = 'opacity 0.2s ease';
                item.style.opacity = '1';
            }, 10);
        } else {
            item.style.display = 'none';
        }
    });
}

// 4. Guided Viva Demo Tour
const vivaSteps = [
    {
        title: "Step 1: Balanced Scorecard (ESS Dashboard)",
        subtitle: "Kaplan-Norton & Laudon Model (Slide 10)",
        content: "Demonstrate senior management alignment across the 4 perspectives (Financial, Customer, Process, Learning). Notice how overall health score aggregates all strategic SLAs.",
        url: "/dashboard"
    },
    {
        title: "Step 2: Herbert Simon's 4-Stage Decision Pipeline",
        subtitle: "Intelligence → Design → Choice → Implementation (Slide 11)",
        content: "A variance in the Scorecard triggers Stage 1 (Intelligence). Inspect an initiative to show Stage 2 (Design alternatives), Stage 3 (Choice with Risk-Adjusted ROI), and Stage 4 (Implementation feedback loop).",
        url: "/decision/pipeline"
    },
    {
        title: "Step 3: What-If Decision Support System (DSS)",
        subtitle: "Middle Management Sensitivity Modeling (Slide 12)",
        content: "Adjust strategic sliders (price, marketing, cost) to observe econometric ripples on revenue, churn, and delivery times. Save scenarios and view the multi-scenario comparison matrix.",
        url: "/simulation"
    },
    {
        title: "Step 4: Operational Credit Expert System",
        subtitle: "Forward-Chaining Rule-Based Inference (Slide 13)",
        content: "Show the production rules and step-by-step inference engine. Submit loan parameters to trace firing rules, conflict resolution, and definitive credit limits.",
        url: "/expert-system"
    },
    {
        title: "Step 5: Business Intelligence Parameterized Reports",
        subtitle: "Enterprise MIS Reporting & Export (Slide 14)",
        content: "Filter across departments, operational status, and perspectives. Export raw data to CSV for corporate compliance and external auditing.",
        url: "/reports"
    },
    {
        title: "Step 6: MIS Academic Curriculum Guide",
        subtitle: "Rigorous Syllabus & Theory Alignment (Slide 15)",
        content: "Direct verification that every single course slide (Herbert Simon stages, MIS hierarchy, Decision models) is implemented live.",
        url: "/theory-guide"
    }
];

let currentVivaIndex = 0;

function launchVivaTour(stepIndex = 0) {
    currentVivaIndex = stepIndex;
    showVivaModal();
}

function showVivaModal() {
    let modal = document.getElementById('vivaTourModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'vivaTourModal';
        modal.className = 'modal-backdrop-calm show';
        modal.innerHTML = `
            <div class="modal-box-calm" style="max-width: 540px;">
                <div class="flex items-center justify-between pb-3" style="border-bottom: 1px solid var(--border-hairline);">
                    <div>
                        <span class="label-caption" style="color: var(--primary); text-transform: uppercase; font-weight: 600;">Viva Presentation Mode</span>
                        <h3 id="vivaStepTitle" class="section-title mt-1" style="font-size: 17px;"></h3>
                        <p id="vivaStepSubtitle" class="label-muted" style="font-size: 12px;"></p>
                    </div>
                    <button type="button" class="btn-ghost" style="padding: 4px; border-radius: 50%;" onclick="closeVivaTour()">✕</button>
                </div>
                <div class="py-4">
                    <p id="vivaStepContent" class="body-default" style="color: var(--text-secondary); line-height: 1.6;"></p>
                </div>
                <div class="flex items-center justify-between pt-3" style="border-top: 1px solid var(--border-hairline);">
                    <div class="label-caption" id="vivaStepCounter">Step 1 of 6</div>
                    <div class="flex items-center gap-2">
                        <button type="button" class="btn btn-secondary btn-sm" id="vivaPrevBtn" onclick="prevVivaStep()">Previous</button>
                        <button type="button" class="btn btn-primary btn-sm" id="vivaNextBtn" onclick="nextVivaStep()">Next Step →</button>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    } else {
        modal.classList.add('show');
    }

    const step = vivaSteps[currentVivaIndex];
    document.getElementById('vivaStepTitle').innerText = step.title;
    document.getElementById('vivaStepSubtitle').innerText = step.subtitle;
    document.getElementById('vivaStepContent').innerText = step.content;
    document.getElementById('vivaStepCounter').innerText = `Step ${currentVivaIndex + 1} of ${vivaSteps.length}`;
    
    document.getElementById('vivaPrevBtn').style.visibility = currentVivaIndex === 0 ? 'hidden' : 'visible';
    const nextBtn = document.getElementById('vivaNextBtn');
    if (currentVivaIndex === vivaSteps.length - 1) {
        nextBtn.innerText = 'Finish Tour';
    } else {
        nextBtn.innerText = 'Go to Page →';
    }
}

function nextVivaStep() {
    const step = vivaSteps[currentVivaIndex];
    if (window.location.pathname !== step.url && currentVivaIndex < vivaSteps.length - 1) {
        window.location.href = step.url;
        return;
    }
    if (currentVivaIndex < vivaSteps.length - 1) {
        currentVivaIndex++;
        const nextStep = vivaSteps[currentVivaIndex];
        if (window.location.pathname !== nextStep.url) {
            window.location.href = nextStep.url;
        } else {
            showVivaModal();
        }
    } else {
        closeVivaTour();
    }
}

function prevVivaStep() {
    if (currentVivaIndex > 0) {
        currentVivaIndex--;
        const prevStep = vivaSteps[currentVivaIndex];
        if (window.location.pathname !== prevStep.url) {
            window.location.href = prevStep.url;
        } else {
            showVivaModal();
        }
    }
}

function closeVivaTour() {
    const modal = document.getElementById('vivaTourModal');
    if (modal) {
        modal.classList.remove('show');
    }
}

// 5. Live Ambient Telemetry Background (Magic UI & React Bits Inspired)
function initLiveAmbientBackground() {
    if (document.getElementById('liveAmbientBg')) return;

    const container = document.createElement('div');
    container.id = 'liveAmbientBg';
    container.className = 'live-ambient-container';
    container.setAttribute('aria-hidden', 'true');
    container.innerHTML = `
        <div class="live-ambient-grid"></div>
        <div class="live-ambient-spotlight" id="ambientSpotlight"></div>
        <div class="live-ambient-orb orb-1"></div>
        <div class="live-ambient-orb orb-2"></div>
        <div class="live-ambient-orb orb-3"></div>
        <canvas id="ambientTelemetryCanvas" class="live-ambient-canvas"></canvas>
    `;
    document.body.prepend(container);

    // Interactive Cursor Spotlight (Smooth Lerp via requestAnimationFrame)
    let mouseX = window.innerWidth / 2;
    let mouseY = window.innerHeight * 0.3;
    let targetX = mouseX;
    let targetY = mouseY;
    let ticking = false;

    window.addEventListener('mousemove', (e) => {
        targetX = e.clientX;
        targetY = e.clientY;
        if (!ticking) {
            window.requestAnimationFrame(() => {
                mouseX += (targetX - mouseX) * 0.15;
                mouseY += (targetY - mouseY) * 0.15;
                container.style.setProperty('--spotlight-x', `${mouseX.toFixed(1)}px`);
                container.style.setProperty('--spotlight-y', `${mouseY.toFixed(1)}px`);
                ticking = false;
            });
            ticking = true;
        }
    }, { passive: true });

    // Microscopic Telemetry Particle Network (Subtle, non-distracting)
    const canvas = document.getElementById('ambientTelemetryCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let width = canvas.width = window.innerWidth;
    let height = canvas.height = window.innerHeight;

    window.addEventListener('resize', () => {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
    }, { passive: true });

    const particleCount = 20;
    const particles = [];
    for (let i = 0; i < particleCount; i++) {
        particles.push({
            x: Math.random() * width,
            y: Math.random() * height,
            vx: (Math.random() - 0.5) * 0.25,
            vy: -0.12 - Math.random() * 0.20, // gentle upward drift
            size: 1 + Math.random() * 1.1,
            baseAlpha: 0.14 + Math.random() * 0.16
        });
    }

    function renderTelemetry() {
        if (document.hidden) {
            requestAnimationFrame(renderTelemetry);
            return;
        }

        ctx.clearRect(0, 0, width, height);
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        const pColor = isDark ? 'rgba(99, 102, 241, ' : 'rgba(79, 70, 229, ';
        const lineColor = isDark ? 'rgba(99, 102, 241, 0.05)' : 'rgba(79, 70, 229, 0.03)';

        for (let i = 0; i < particles.length; i++) {
            const p = particles[i];
            p.x += p.vx;
            p.y += p.vy;

            // Subtle gentle nudge around cursor
            const dx = p.x - targetX;
            const dy = p.y - targetY;
            const dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 110 && dist > 0) {
                const force = (110 - dist) / 110 * 0.35;
                p.x += (dx / dist) * force;
                p.y += (dy / dist) * force;
            }

            // Wrap edges smoothly
            if (p.x < 0) p.x = width;
            if (p.x > width) p.x = 0;
            if (p.y < 0) p.y = height;
            if (p.y > height) p.y = 0;

            // Draw particle dot
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
            ctx.fillStyle = pColor + (isDark ? p.baseAlpha : p.baseAlpha * 0.55) + ')';
            ctx.fill();

            // Connect nearest neighbors with faint hairline
            for (let j = i + 1; j < particles.length; j++) {
                const p2 = particles[j];
                const dX = p.x - p2.x;
                const dY = p.y - p2.y;
                const d = Math.sqrt(dX * dX + dY * dY);
                if (d < 80) {
                    ctx.beginPath();
                    ctx.moveTo(p.x, p.y);
                    ctx.lineTo(p2.x, p2.y);
                    ctx.strokeStyle = lineColor;
                    ctx.lineWidth = 0.75;
                    ctx.stroke();
                }
            }
        }

        requestAnimationFrame(renderTelemetry);
    }

    if (!window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        requestAnimationFrame(renderTelemetry);
    }
}
