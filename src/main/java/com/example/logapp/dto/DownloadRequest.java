package com.example.logapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DownloadRequest {
    @NotBlank
    private String project;
    @NotBlank
    private String environment;
    @NotBlank
    private String module;
    @NotBlank
    private String server;
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }
    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }
    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
}
