package com.stratops.stratops360.model;

public enum KpiStatus {
    ON_TRACK("On Track", "success", "check-circle"),
    WARNING("Warning", "warning", "exclamation-triangle"),
    CRITICAL("Critical", "danger", "times-circle");

    private final String label;
    private final String cssClass;
    private final String icon;

    KpiStatus(String label, String cssClass, String icon) {
        this.label = label;
        this.cssClass = cssClass;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getIcon() {
        return icon;
    }
}
