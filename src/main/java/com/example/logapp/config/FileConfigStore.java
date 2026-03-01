package com.example.logapp.config;

import com.example.logapp.util.CryptoUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class FileConfigStore {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Value("${app.config.dir:config}")
    private String configDir;

    private volatile ProjectsFile projectsFile = new ProjectsFile();
    private volatile UsersFile usersFile = new UsersFile();
    private volatile SystemSettings systemSettings = new SystemSettings();

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(configDir));
        reloadAll();
    }

    @Scheduled(fixedDelayString = "${app.config.reload-ms:15000}")
    public void reloadScheduler() throws IOException {
        reloadAll();
    }

    public void reloadAll() throws IOException {
        lock.writeLock().lock();
        try {
            projectsFile = readYaml("projects.yaml", ProjectsFile.class, new ProjectsFile());
            usersFile = readYaml("users.yaml", UsersFile.class, new UsersFile());
            systemSettings = readYaml("system.yaml", SystemSettings.class, new SystemSettings());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<ProjectConfig> getProjects() {
        lock.readLock().lock();
        try {
            return projectsFile.getProjects();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<UserRecord> getUsers() {
        lock.readLock().lock();
        try {
            return usersFile.getUsers();
        } finally {
            lock.readLock().unlock();
        }
    }

    public SystemSettings getSystemSettings() {
        lock.readLock().lock();
        try {
            return systemSettings;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addProject(String projectName) throws IOException {
        lock.writeLock().lock();
        try {
            projectsFile.getProjects().add(newProject(projectName));
            writeYamlWithBackup("projects.yaml", projectsFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addUser(String username, String rawPassword, String role) throws IOException {
        lock.writeLock().lock();
        try {
            UserRecord userRecord = new UserRecord();
            userRecord.setUsername(username);
            userRecord.setPasswordHash(CryptoUtil.bcrypt(rawPassword));
            userRecord.setRole(Enum.valueOf(com.example.logapp.model.Role.class, role.toUpperCase()));
            usersFile.getUsers().add(userRecord);
            writeYamlWithBackup("users.yaml", usersFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<ServerConfig> findServer(String project, String environment, String module, String server) {
        return getProjects().stream()
                .filter(p -> p.getName().equalsIgnoreCase(project))
                .flatMap(p -> p.getEnvironments().stream())
                .filter(e -> e.getName().equalsIgnoreCase(environment))
                .flatMap(e -> e.getModules().stream())
                .filter(m -> m.getName().equalsIgnoreCase(module))
                .flatMap(m -> m.getServers().stream())
                .filter(s -> s.getName().equalsIgnoreCase(server))
                .findFirst();
    }

    private ProjectConfig newProject(String name) {
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setName(name);
        return projectConfig;
    }

    private <T> T readYaml(String fileName, Class<T> type, T defaultValue) throws IOException {
        Path path = Path.of(configDir, fileName);
        if (!Files.exists(path)) {
            writeYamlWithBackup(fileName, defaultValue);
            return defaultValue;
        }
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(type, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        try (InputStream in = Files.newInputStream(path)) {
            T data = yaml.load(in);
            return data == null ? defaultValue : data;
        }
    }

    public void writeYamlWithBackup(String fileName, Object object) throws IOException {
        Path path = Path.of(configDir, fileName);
        if (Files.exists(path)) {
            String backupName = fileName + "." + Instant.now().toEpochMilli() + ".bak";
            Files.copy(path, path.resolveSibling(backupName), StandardCopyOption.REPLACE_EXISTING);
        }
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer(options);
        Yaml yaml = new Yaml(representer, options);
        try (Writer writer = Files.newBufferedWriter(path)) {
            yaml.dump(object, writer);
        }
    }
}
