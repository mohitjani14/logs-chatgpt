package com.example.logapp.queue;

import com.example.logapp.config.FileConfigStore;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DownloadJobQueue {
    private final BlockingQueue<DownloadJob> queue;
    private final Map<String, DownloadJob> jobs = new ConcurrentHashMap<>();

    public DownloadJobQueue(FileConfigStore fileConfigStore) {
        int capacity = fileConfigStore.getSystemSettings().getQueueCapacity();
        this.queue = new ArrayBlockingQueue<>(Math.max(capacity, 100));
    }

    public void submit(DownloadJob job) {
        jobs.put(job.getId(), job);
        if (!queue.offer(job)) {
            throw new IllegalStateException("Queue capacity reached");
        }
    }

    public DownloadJob take() throws InterruptedException {
        return queue.take();
    }

    public Optional<DownloadJob> find(String id) {
        return Optional.ofNullable(jobs.get(id));
    }

    public Map<String, DownloadJob> allJobs() {
        return jobs;
    }
}
