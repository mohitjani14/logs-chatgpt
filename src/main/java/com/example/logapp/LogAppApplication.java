package com.example.logapp;

import com.example.logapp.queue.DownloadJobDispatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LogAppApplication {

    private final DownloadJobDispatcher downloadJobDispatcher;

    public LogAppApplication(DownloadJobDispatcher downloadJobDispatcher) {
        this.downloadJobDispatcher = downloadJobDispatcher;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogAppApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        downloadJobDispatcher.start();
    }
}
