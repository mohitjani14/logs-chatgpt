package com.example.logapp.config;

public class ServerConfig {
    private String name;
    private String host;
    private int port = 22;
    private String username;
    private String passwordEncrypted;
    private String privateKeyPath;
    private String logDirectory;
    private String logPattern = "*.log";
    private boolean rotationSupported = true;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordEncrypted() { return passwordEncrypted; }
    public void setPasswordEncrypted(String passwordEncrypted) { this.passwordEncrypted = passwordEncrypted; }
    public String getPrivateKeyPath() { return privateKeyPath; }
    public void setPrivateKeyPath(String privateKeyPath) { this.privateKeyPath = privateKeyPath; }
    public String getLogDirectory() { return logDirectory; }
    public void setLogDirectory(String logDirectory) { this.logDirectory = logDirectory; }
    public String getLogPattern() { return logPattern; }
    public void setLogPattern(String logPattern) { this.logPattern = logPattern; }
    public boolean isRotationSupported() { return rotationSupported; }
    public void setRotationSupported(boolean rotationSupported) { this.rotationSupported = rotationSupported; }
}
