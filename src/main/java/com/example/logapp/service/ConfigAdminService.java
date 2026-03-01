package com.example.logapp.service;

import com.example.logapp.config.FileConfigStore;
import com.example.logapp.config.ProjectConfig;
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

    public List<ProjectConfig> listProjects() {
        return fileConfigStore.getProjects();
    }

    public List<UserRecord> listUsers() {
        return fileConfigStore.getUsers();
    }

    public void addProject(String name) throws IOException {
        fileConfigStore.addProject(name);
    }

    public void addUser(String username, String password, String role) throws IOException {
        fileConfigStore.addUser(username, password, role);
    }

    public void reloadConfig() throws IOException {
        fileConfigStore.reloadAll();
    }
}
