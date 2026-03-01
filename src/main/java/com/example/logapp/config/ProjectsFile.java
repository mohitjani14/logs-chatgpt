package com.example.logapp.config;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFile {
    private List<ProjectConfig> projects = new ArrayList<>();

    public List<ProjectConfig> getProjects() { return projects; }
    public void setProjects(List<ProjectConfig> projects) { this.projects = projects; }
}
