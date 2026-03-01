package com.example.logapp.config;

import java.util.ArrayList;
import java.util.List;

public class ProjectConfig {
    private String name;
    private List<EnvironmentConfig> environments = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<EnvironmentConfig> getEnvironments() { return environments; }
    public void setEnvironments(List<EnvironmentConfig> environments) { this.environments = environments; }
}
