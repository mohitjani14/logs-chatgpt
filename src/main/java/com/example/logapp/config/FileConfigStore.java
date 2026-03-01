package com.example.logapp.config;

import com.example.logapp.model.Role;
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
            ProjectConfig project = findProject(projectName).orElseGet(() -> {
                ProjectConfig p = new ProjectConfig();
                p.setName(projectName);
                projectsFile.getProjects().add(p);
                return p;
            });
            project.setName(projectName);
            writeYamlWithBackup("projects.yaml", projectsFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addEnvironment(String projectName, String environmentName) throws IOException {
        lock.writeLock().lock();
        try {
            ProjectConfig project = findProject(projectName).orElseThrow(() -> new IllegalArgumentException("Project not found"));
            boolean exists = project.getEnvironments().stream().anyMatch(e -> e.getName().equalsIgnoreCase(environmentName));
            if (!exists) {
                EnvironmentConfig env = new EnvironmentConfig();
                env.setName(environmentName);
                project.getEnvironments().add(env);
                writeYamlWithBackup("projects.yaml", projectsFile);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addModule(String projectName, String environmentName, String moduleName) throws IOException {
        lock.writeLock().lock();
        try {
            EnvironmentConfig env = findEnvironment(projectName, environmentName)
                    .orElseThrow(() -> new IllegalArgumentException("Environment not found"));
            boolean exists = env.getModules().stream().anyMatch(m -> m.getName().equalsIgnoreCase(moduleName));
            if (!exists) {
                ModuleConfig module = new ModuleConfig();
                module.setName(moduleName);
                env.getModules().add(module);
                writeYamlWithBackup("projects.yaml", projectsFile);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addServer(String projectName, String environmentName, String moduleName, ServerConfig serverConfig) throws IOException {
        lock.writeLock().lock();
        try {
            ModuleConfig module = findModule(projectName, environmentName, moduleName)
                    .orElseThrow(() -> new IllegalArgumentException("Module not found"));
            module.getServers().removeIf(s -> s.getName().equalsIgnoreCase(serverConfig.getName()));
            module.getServers().add(serverConfig);
            writeYamlWithBackup("projects.yaml", projectsFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateSystemSettings(int workerThreads, int queueCapacity, String downloadDirectory, int timeoutSeconds) throws IOException {
        lock.writeLock().lock();
        try {
            systemSettings.setWorkerThreads(workerThreads);
            systemSettings.setQueueCapacity(queueCapacity);
            systemSettings.setDownloadDirectory(downloadDirectory);
            systemSettings.setDownloadTimeoutSeconds(timeoutSeconds);
            writeYamlWithBackup("system.yaml", systemSettings);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addUser(String username, String rawPassword, String role) throws IOException {
        lock.writeLock().lock();
        try {
            usersFile.getUsers().removeIf(u -> u.getUsername().equalsIgnoreCase(username));
            UserRecord userRecord = new UserRecord();
            userRecord.setUsername(username);
            userRecord.setPasswordHash("{bcrypt}" + CryptoUtil.bcrypt(rawPassword));
            userRecord.setRole(Role.valueOf(role.toUpperCase()));
            usersFile.getUsers().add(userRecord);
            writeYamlWithBackup("users.yaml", usersFile);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void resetTemporaryAdminPassword(String temporaryPassword) throws IOException {
        lock.writeLock().lock();
        try {
            UserRecord admin = usersFile.getUsers().stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase("admin"))
                    .findFirst()
                    .orElseGet(() -> {
                        UserRecord user = new UserRecord();
                        user.setUsername("admin");
                        user.setRole(Role.ADMIN);
                        usersFile.getUsers().add(user);
                        return user;
                    });
            admin.setPasswordHash("{noop}" + temporaryPassword);
            admin.setRole(Role.ADMIN);
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

    private Optional<ProjectConfig> findProject(String project) {
        return projectsFile.getProjects().stream().filter(p -> p.getName().equalsIgnoreCase(project)).findFirst();
    }

    private Optional<EnvironmentConfig> findEnvironment(String project, String environment) {
        return findProject(project).stream()
                .flatMap(p -> p.getEnvironments().stream())
                .filter(e -> e.getName().equalsIgnoreCase(environment))
                .findFirst();
    }

    private Optional<ModuleConfig> findModule(String project, String environment, String moduleName) {
        return findEnvironment(project, environment).stream()
                .flatMap(e -> e.getModules().stream())
                .filter(m -> m.getName().equalsIgnoreCase(moduleName))
                .findFirst();
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
