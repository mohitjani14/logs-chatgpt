package com.example.logapp.service;

import com.example.logapp.dto.DownloadRequest;
import com.example.logapp.queue.DownloadJob;
import com.example.logapp.queue.DownloadJobQueue;
import org.springframework.stereotype.Service;

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

    public Optional<DownloadJob> status(String jobId) {
        return downloadJobQueue.find(jobId);
    }
}
