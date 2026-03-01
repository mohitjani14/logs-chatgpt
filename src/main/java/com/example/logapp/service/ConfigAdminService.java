package com.example.logapp.service;

import com.example.logapp.config.FileConfigStore;
import com.example.logapp.config.ProjectConfig;
import com.example.logapp.config.ServerConfig;
import com.example.logapp.config.SystemSettings;
import com.example.logapp.config.UserRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ConfigAdminService {
    private final FileConfigStore fileConfigStore;

    public ConfigAdminService(FileConfigStore fileConfigStore) {
        this.fileConfigStore = fileConfigStore;
    }

    public List<ProjectConfig> listProjects() { return fileConfigStore.getProjects(); }
    public List<UserRecord> listUsers() { return fileConfigStore.getUsers(); }
    public SystemSettings settings() { return fileConfigStore.getSystemSettings(); }

    public void addProject(String name) throws IOException { fileConfigStore.addProject(name); }
    public void addEnvironment(String project, String environment) throws IOException { fileConfigStore.addEnvironment(project, environment); }
    public void addModule(String project, String environment, String module) throws IOException { fileConfigStore.addModule(project, environment, module); }
    public void addServer(String project, String environment, String module, ServerConfig serverConfig) throws IOException { fileConfigStore.addServer(project, environment, module, serverConfig); }
    public void addUser(String username, String password, String role) throws IOException { fileConfigStore.addUser(username, password, role); }
    public void updateSettings(int workerThreads, int queueCapacity, String downloadDirectory, int timeoutSeconds) throws IOException {
        fileConfigStore.updateSystemSettings(workerThreads, queueCapacity, downloadDirectory, timeoutSeconds);
    }
    public void resetTemporaryAdminPassword(String temporaryPassword) throws IOException { fileConfigStore.resetTemporaryAdminPassword(temporaryPassword); }
    public void reloadConfig() throws IOException { fileConfigStore.reloadAll(); }
}
