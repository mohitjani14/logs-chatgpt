package com.example.logapp.queue;

import com.example.logapp.model.JobStatus;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class DownloadJob {
    private final String id = UUID.randomUUID().toString();
    private final String username;
    private final String project;
    private final String environment;
    private final String module;
    private final String server;
    private final LocalDate from;
    private final LocalDate to;
    private final Instant createdAt = Instant.now();
    private volatile JobStatus status = JobStatus.QUEUED;
    private volatile String error;
    private volatile Path zipPath;

    public DownloadJob(String username, String project, String environment, String module, String server, LocalDate from, LocalDate to) {
        this.username = username;
        this.project = project;
        this.environment = environment;
        this.module = module;
        this.server = server;
        this.from = from;
        this.to = to;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getProject() { return project; }
    public String getEnvironment() { return environment; }
    public String getModule() { return module; }
    public String getServer() { return server; }
    public LocalDate getFrom() { return from; }
    public LocalDate getTo() { return to; }
    public Instant getCreatedAt() { return createdAt; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public Path getZipPath() { return zipPath; }
    public void setZipPath(Path zipPath) { this.zipPath = zipPath; }
}
