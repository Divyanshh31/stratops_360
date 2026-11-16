package com.stratops.stratops360.model;

public enum KpiPerspective {
    FINANCIAL("Financial Perspective", "How do we look to shareholders?", "badge-primary", "dollar-sign"),
    CUSTOMER("Customer Perspective", "How do customers see us?", "badge-info", "users"),
    INTERNAL_PROCESS("Internal Business Processes", "What business processes must we excel at?", "badge-warning", "cogs"),
    LEARNING_GROWTH("Learning and Growth", "Can we continue to improve, innovate, and create value?", "badge-success", "graduation-cap");

    private final String displayName;
    private final String coreQuestion;
    private final String badgeClass;
    private final String icon;

    KpiPerspective(String displayName, String coreQuestion, String badgeClass, String icon) {
        this.displayName = displayName;
        this.coreQuestion = coreQuestion;
        this.badgeClass = badgeClass;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCoreQuestion() {
        return coreQuestion;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getIcon() {
        return icon;
    }
}
