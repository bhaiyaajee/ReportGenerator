package com.example.report.reportGenerator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransformationRulesConfig {

    @Value("${transformation.rule.outfield1}")
    private String outfield1Rule;

    @Value("${transformation.rule.outfield2}")
    private String outfield2Rule;

    @Value("${transformation.rule.outfield3}")
    private String outfield3Rule;

    @Value("${transformation.rule.outfield4}")
    private String outfield4Rule;

    @Value("${transformation.rule.outfield5}")
    private String outfield5Rule;

    public String getOutfield1Rule() {
        return outfield1Rule;
    }

    public String getOutfield2Rule() {
        return outfield2Rule;
    }

    public String getOutfield3Rule() {
        return outfield3Rule;
    }

    public String getOutfield4Rule() {
        return outfield4Rule;
    }

    public String getOutfield5Rule() {
        return outfield5Rule;
    }
}