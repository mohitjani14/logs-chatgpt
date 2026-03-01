package com.example.logapp.queue;

import com.example.logapp.config.FileConfigStore;
import com.example.logapp.worker.DownloadWorker;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DownloadJobDispatcher {
    private final DownloadJobQueue queue;
    private final DownloadWorker worker;
    private final ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService pool;

    public DownloadJobDispatcher(DownloadJobQueue queue, DownloadWorker worker, FileConfigStore configStore) {
        this.queue = queue;
        this.worker = worker;
        int threads = configStore.getSystemSettings().getWorkerThreads();
        this.pool = Executors.newFixedThreadPool(Math.max(threads, 4));
    }

    public void start() {
        dispatcherExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DownloadJob job = queue.take();
                    pool.submit(() -> worker.process(job));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        dispatcherExecutor.shutdownNow();
        pool.shutdownNow();
    }
}
