package com.example.logapp.config;

public class SystemSettings {
    private int queueCapacity = 500;
    private int workerThreads = 12;
    private String downloadDirectory = "downloads";
    private String auditFile = "logs/audit/audit.log";
    private int downloadTimeoutSeconds = 300;

    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
    public int getWorkerThreads() { return workerThreads; }
    public void setWorkerThreads(int workerThreads) { this.workerThreads = workerThreads; }
    public String getDownloadDirectory() { return downloadDirectory; }
    public void setDownloadDirectory(String downloadDirectory) { this.downloadDirectory = downloadDirectory; }
    public String getAuditFile() { return auditFile; }
    public void setAuditFile(String auditFile) { this.auditFile = auditFile; }
    public int getDownloadTimeoutSeconds() { return downloadTimeoutSeconds; }
    public void setDownloadTimeoutSeconds(int downloadTimeoutSeconds) { this.downloadTimeoutSeconds = downloadTimeoutSeconds; }
}
