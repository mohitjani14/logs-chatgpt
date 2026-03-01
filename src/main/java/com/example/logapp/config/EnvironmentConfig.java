package com.example.logapp.config;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentConfig {
    private String name;
    private List<ModuleConfig> modules = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ModuleConfig> getModules() { return modules; }
    public void setModules(List<ModuleConfig> modules) { this.modules = modules; }
}
