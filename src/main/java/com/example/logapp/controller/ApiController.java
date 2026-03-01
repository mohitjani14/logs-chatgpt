package com.example.logapp.controller;

import com.example.logapp.audit.AuditService;
import com.example.logapp.queue.DownloadJob;
import com.example.logapp.service.DownloadService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                        "error", job.getError() == null ? "" : job.getError())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> file(@PathVariable String jobId) {
        DownloadJob job = downloadService.status(jobId).orElseThrow();
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
