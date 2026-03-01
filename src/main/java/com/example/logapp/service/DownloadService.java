package com.example.logapp.service;

import com.example.logapp.dto.DownloadRequest;
import com.example.logapp.model.JobStatus;
import com.example.logapp.queue.DownloadJob;
import com.example.logapp.queue.DownloadJobQueue;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DownloadService {
    private final DownloadJobQueue downloadJobQueue;

    public DownloadService(DownloadJobQueue downloadJobQueue) {
        this.downloadJobQueue = downloadJobQueue;
    }

    public String queueJob(String username, DownloadRequest request) {
        DownloadJob job = new DownloadJob(username, request.getProject(), request.getEnvironment(), request.getModule(), request.getServer(), request.getFrom(), request.getTo());
        downloadJobQueue.submit(job);
        return job.getId();
    }

    public Optional<DownloadJob> status(String jobId) { return downloadJobQueue.find(jobId); }

    public List<DownloadJob> userJobs(String username) {
        return downloadJobQueue.allJobs().values().stream()
                .filter(j -> j.getUsername().equalsIgnoreCase(username))
                .sorted(Comparator.comparing(DownloadJob::getCreatedAt).reversed())
                .toList();
    }

    public List<DownloadJob> allJobs() {
        return downloadJobQueue.allJobs().values().stream()
                .sorted(Comparator.comparing(DownloadJob::getCreatedAt).reversed())
                .toList();
    }

    public boolean cancel(String jobId, String requestedBy) {
        Optional<DownloadJob> job = downloadJobQueue.find(jobId);
        if (job.isEmpty()) {
            return false;
        }
        DownloadJob item = job.get();
        if (!item.getUsername().equalsIgnoreCase(requestedBy)) {
            return false;
        }
        item.setCancelRequested(true);
        if (item.getStatus() == JobStatus.QUEUED) {
            item.setStatus(JobStatus.CANCELLED);
        }
        return true;
    }
}
