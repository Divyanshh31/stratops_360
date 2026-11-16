package com.stratops.stratops360.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Encapsulates baseline corporate metrics from Slide 12 BI forecasting model.
 */
@Configuration
@ConfigurationProperties(prefix = "stratops.simulation")
public class SimulationProperties {

    private BigDecimal baseOperatingMargin = new BigDecimal("15.4");
    private BigDecimal baseNetProfit = new BigDecimal("44.5"); // in Millions USD
    private BigDecimal baseCustomerRetention = new BigDecimal("81.5");
    private BigDecimal baseNps = new BigDecimal("68.0");
    private BigDecimal baseCycleTime = new BigDecimal("6.8"); // in Days
    private BigDecimal baseDefectRate = new BigDecimal("1.3"); // in %
    private BigDecimal baseEmployeeRetention = new BigDecimal("92.8"); // in %

    public BigDecimal getBaseOperatingMargin() {
        return baseOperatingMargin;
    }

    public void setBaseOperatingMargin(BigDecimal baseOperatingMargin) {
        this.baseOperatingMargin = baseOperatingMargin;
    }

    public BigDecimal getBaseNetProfit() {
        return baseNetProfit;
    }

    public void setBaseNetProfit(BigDecimal baseNetProfit) {
        this.baseNetProfit = baseNetProfit;
    }

    public BigDecimal getBaseCustomerRetention() {
        return baseCustomerRetention;
    }

    public void setBaseCustomerRetention(BigDecimal baseCustomerRetention) {
        this.baseCustomerRetention = baseCustomerRetention;
    }

    public BigDecimal getBaseNps() {
        return baseNps;
    }

    public void setBaseNps(BigDecimal baseNps) {
        this.baseNps = baseNps;
    }

    public BigDecimal getBaseCycleTime() {
        return baseCycleTime;
    }

    public void setBaseCycleTime(BigDecimal baseCycleTime) {
        this.baseCycleTime = baseCycleTime;
    }

    public BigDecimal getBaseDefectRate() {
        return baseDefectRate;
    }

    public void setBaseDefectRate(BigDecimal baseDefectRate) {
        this.baseDefectRate = baseDefectRate;
    }

    public BigDecimal getBaseEmployeeRetention() {
        return baseEmployeeRetention;
    }

    public void setBaseEmployeeRetention(BigDecimal baseEmployeeRetention) {
        this.baseEmployeeRetention = baseEmployeeRetention;
    }
}
