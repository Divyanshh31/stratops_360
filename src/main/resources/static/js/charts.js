/**
 * StratOps 360 - Chart.js Visualizations (Calm Editorial Enterprise)
 * Thin 1.5px hairline strokes, no area gradients, 10px radius tooltips with 1px border.
 */

let scorecardRadarChart = null;
let trendLineChart = null;

function isDarkMode() {
    return document.documentElement.getAttribute('data-theme') === 'dark' ||
           document.documentElement.getAttribute('data-bs-theme') === 'dark';
}

function getEditorialChartColors() {
    const dark = isDarkMode();
    return {
        text: dark ? '#9CA3AF' : '#6B7280',
        textPrimary: dark ? '#F3F4F6' : '#111827',
        grid: dark ? '#2A2E33' : '#EBEBEB',
        baseline: dark ? '#575E70' : '#C7C4D8',
        radarFill: dark ? 'rgba(99, 102, 241, 0.08)' : 'rgba(79, 70, 229, 0.06)',
        radarBorder: dark ? '#6366F1' : '#4F46E5',
        primary: dark ? '#6366F1' : '#4F46E5',
        tooltipBg: dark ? '#1A1D20' : '#FFFFFF',
        tooltipBorder: dark ? '#2A2E33' : '#EBEBEB'
    };
}

// 1. Balanced Scorecard Equilibrium Radar Chart
function initScorecardRadar(canvasId, financialVal, customerVal, processVal, learningVal) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    
    if (scorecardRadarChart) {
        scorecardRadarChart.destroy();
    }
    
    const colors = getEditorialChartColors();
    const ctx = canvas.getContext('2d');
    
    scorecardRadarChart = new Chart(ctx, {
        type: 'radar',
        data: {
            labels: ['Financial', 'Customer', 'Internal Process', 'Learning & Growth'],
            datasets: [
                {
                    label: 'Target Baseline',
                    data: [85, 85, 85, 85],
                    borderColor: colors.baseline,
                    borderWidth: 1,
                    borderDash: [4, 4],
                    backgroundColor: 'transparent',
                    pointRadius: 0,
                    pointHoverRadius: 0
                },
                {
                    label: 'Current Health',
                    data: [financialVal, customerVal, processVal, learningVal],
                    backgroundColor: colors.radarFill,
                    borderColor: colors.radarBorder,
                    borderWidth: 1.5,
                    pointBackgroundColor: colors.radarBorder,
                    pointBorderColor: colors.tooltipBg,
                    pointBorderWidth: 1.5,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    angleLines: { color: colors.grid, lineWidth: 1 },
                    grid: { color: colors.grid, lineWidth: 1 },
                    pointLabels: {
                        color: colors.textPrimary,
                        font: { family: 'Inter', size: 12, weight: '500' }
                    },
                    suggestedMin: 40,
                    suggestedMax: 100,
                    ticks: {
                        backdropColor: 'transparent',
                        color: colors.text,
                        stepSize: 20,
                        font: { family: 'Inter', size: 12 }
                    }
                }
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: colors.tooltipBg,
                    titleColor: colors.textPrimary,
                    bodyColor: colors.text,
                    borderColor: colors.tooltipBorder,
                    borderWidth: 1,
                    cornerRadius: 10,
                    padding: 10,
                    boxPadding: 4,
                    usePointStyle: true,
                    callbacks: {
                        label: function(context) {
                            return ` ${context.dataset.label}: ${context.raw}%`;
                        }
                    }
                }
            }
        }
    });
}

function initScorecardRadarChart(canvasId, labels, values) {
    if (Array.isArray(values) && values.length >= 4) {
        initScorecardRadar(canvasId, values[0], values[1], values[2], values[3]);
    }
}

// 2. Perspective Drilldown Historical Trend Line Chart
function initTrendLineChart(canvasId, labels, dataPoints, targetVal, unit) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    
    if (trendLineChart) {
        trendLineChart.destroy();
    }
    
    const colors = getEditorialChartColors();
    const ctx = canvas.getContext('2d');
    
    const targetDataset = {
        label: `Target (${targetVal} ${unit || ''})`,
        data: labels.map(() => targetVal),
        borderColor: colors.baseline,
        borderWidth: 1,
        borderDash: [4, 4],
        fill: false,
        pointRadius: 0
    };
    
    trendLineChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                targetDataset,
                {
                    label: `Actual Value (${unit || ''})`,
                    data: dataPoints,
                    borderColor: colors.primary,
                    borderWidth: 1.5,
                    backgroundColor: 'transparent',
                    fill: false,
                    tension: 0.2,
                    pointBackgroundColor: colors.primary,
                    pointBorderColor: colors.tooltipBg,
                    pointBorderWidth: 1.5,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { color: colors.text, font: { family: 'Inter', size: 12 } },
                    border: { color: colors.grid }
                },
                y: {
                    grid: { color: colors.grid, lineWidth: 1, borderDash: [4, 4] },
                    ticks: { color: colors.text, font: { family: 'Inter', size: 12 } },
                    border: { display: false }
                }
            },
            plugins: {
                legend: {
                    labels: { color: colors.textPrimary, font: { family: 'Inter', size: 12 } }
                },
                tooltip: {
                    backgroundColor: colors.tooltipBg,
                    titleColor: colors.textPrimary,
                    bodyColor: colors.text,
                    borderColor: colors.tooltipBorder,
                    borderWidth: 1,
                    cornerRadius: 10,
                    padding: 10
                }
            }
        }
    });
}

// 3. Mini Sparklines for KPI Cards
function initKpiSparkline(canvasId, values) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !values || values.length === 0) return;
    
    const colors = getEditorialChartColors();
    const ctx = canvas.getContext('2d');
    
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: values.map((_, i) => i + 1),
            datasets: [{
                data: values,
                borderColor: colors.primary,
                borderWidth: 1.5,
                fill: false,
                tension: 0.3,
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: { display: false },
                y: { display: false }
            },
            plugins: {
                legend: { display: false },
                tooltip: { enabled: false }
            }
        }
    });
}

window.onThemeChanged = function() {
    if (scorecardRadarChart) {
        const colors = getEditorialChartColors();
        scorecardRadarChart.options.scales.r.grid.color = colors.grid;
        scorecardRadarChart.options.scales.r.angleLines.color = colors.grid;
        scorecardRadarChart.options.scales.r.pointLabels.color = colors.textPrimary;
        scorecardRadarChart.update();
    }
};
