package com.example.logapp.controller;

import com.example.logapp.audit.AuditService;
import com.example.logapp.model.JobStatus;
import com.example.logapp.queue.DownloadJob;
import com.example.logapp.service.DownloadService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final DownloadService downloadService;
    private final AuditService auditService;

    public ApiController(DownloadService downloadService, AuditService auditService) {
        this.downloadService = downloadService;
        this.auditService = auditService;
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> status(@PathVariable String jobId) {
        return downloadService.status(jobId)
                .<ResponseEntity<?>>map(job -> ResponseEntity.ok(Map.of(
                        "id", job.getId(),
                        "status", job.getStatus(),
                        "error", job.getError() == null ? "" : job.getError(),
                        "progress", job.getProgress())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/jobs/{jobId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String jobId, Authentication authentication) {
        boolean cancelled = downloadService.cancel(jobId, authentication.getName());
        return cancelled ? ResponseEntity.ok(Map.of("status", JobStatus.CANCELLED)) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/jobs")
    public List<Map<String, Object>> userJobs(Authentication authentication) {
        return downloadService.userJobs(authentication.getName()).stream().map(j -> Map.of(
                "id", j.getId(),
                "project", j.getProject(),
                "environment", j.getEnvironment(),
                "module", j.getModule(),
                "server", j.getServer(),
                "status", j.getStatus(),
                "progress", j.getProgress())).toList();
    }

    @GetMapping("/admin/jobs")
    public List<Map<String, Object>> allJobs() {
        return downloadService.allJobs().stream().map(j -> Map.of(
                "id", j.getId(),
                "user", j.getUsername(),
                "status", j.getStatus(),
                "project", j.getProject(),
                "server", j.getServer(),
                "progress", j.getProgress())).toList();
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> file(@PathVariable String jobId) {
        DownloadJob job = downloadService.status(jobId).orElseThrow();
        if (job.getStatus() == JobStatus.DOWNLOADED || job.getStatus() == JobStatus.COMPLETED) {
            job.setStatus(JobStatus.DOWNLOADED);
        }
        Resource resource = new FileSystemResource(job.getZipPath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs-" + jobId + ".zip")
                .body(resource);
    }

    @GetMapping("/admin/audit")
    public List<String> audit(@RequestParam(defaultValue = "") String q) throws IOException {
        return auditService.search(q);
    }
}
