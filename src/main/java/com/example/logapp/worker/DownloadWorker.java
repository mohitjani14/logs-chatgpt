package com.example.logapp.worker;

import com.example.logapp.audit.AuditService;
import com.example.logapp.config.FileConfigStore;
import com.example.logapp.config.ServerConfig;
import com.example.logapp.model.JobStatus;
import com.example.logapp.queue.DownloadJob;
import com.example.logapp.sftp.SftpDownloadClient;
import com.example.logapp.util.ZipUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class DownloadWorker {
    private static final int RETRIES = 3;

    private final FileConfigStore configStore;
    private final SftpDownloadClient sftpDownloadClient;
    private final AuditService auditService;

    public DownloadWorker(FileConfigStore configStore, SftpDownloadClient sftpDownloadClient, AuditService auditService) {
        this.configStore = configStore;
        this.sftpDownloadClient = sftpDownloadClient;
        this.auditService = auditService;
    }

    public void process(DownloadJob job) {
        job.setStatus(JobStatus.PROCESSING);
        Instant start = Instant.now();
        String dateRange = job.getFrom() + ".." + job.getTo();

        try {
            ServerConfig serverConfig = configStore.findServer(job.getProject(), job.getEnvironment(), job.getModule(), job.getServer())
                    .orElseThrow(() -> new IllegalArgumentException("Server config not found"));

            IOException last = null;
            for (int i = 1; i <= RETRIES; i++) {
                try {
                    Path jobDir = Path.of(configStore.getSystemSettings().getDownloadDirectory(), job.getId());
                    Files.createDirectories(jobDir);
                    List<Path> files = sftpDownloadClient.downloadLogs(serverConfig, job.getFrom(), job.getTo(), jobDir);
                    Path zipPath = jobDir.resolve("logs.zip");
                    ZipUtil.zipFiles(files, zipPath);
                    job.setZipPath(zipPath);
                    job.setStatus(JobStatus.COMPLETED);
                    auditService.log(job.getUsername(), "DOWNLOAD", job.getProject(), job.getModule(), job.getServer(), dateRange, "SUCCESS", "N/A");
                    return;
                } catch (IOException ex) {
                    last = ex;
                }
            }
            throw last != null ? last : new IOException("Unknown download failure");
        } catch (Exception ex) {
            job.setStatus(JobStatus.FAILED);
            job.setError(ex.getMessage());
            auditService.log(job.getUsername(), "DOWNLOAD", job.getProject(), job.getModule(), job.getServer(), dateRange, "FAILED", "N/A");
            cleanup(job);
        } finally {
            long timeout = configStore.getSystemSettings().getDownloadTimeoutSeconds();
            if (Duration.between(start, Instant.now()).toSeconds() > timeout) {
                job.setStatus(JobStatus.FAILED);
                job.setError("Timeout exceeded");
                cleanup(job);
            }
        }
    }

    private void cleanup(DownloadJob job) {
        try {
            Path dir = Path.of(configStore.getSystemSettings().getDownloadDirectory(), job.getId());
            if (Files.exists(dir)) {
                try (var stream = Files.walk(dir)) {
                    stream.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                    });
                }
            }
        } catch (IOException ignored) {
        }
    }
}
