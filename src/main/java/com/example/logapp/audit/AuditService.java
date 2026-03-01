package com.example.logapp.audit;

import com.example.logapp.config.FileConfigStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;

@Service
public class AuditService {
    private final FileConfigStore fileConfigStore;

    public AuditService(FileConfigStore fileConfigStore) {
        this.fileConfigStore = fileConfigStore;
    }

    public void log(String username, String action, String project, String module, String server, String dateRange, String status, String ip) {
        String line = String.format("[%s] USER:%s ACTION:%s PROJECT:%s MODULE:%s SERVER:%s DATE:%s STATUS:%s IP:%s%n",
                Instant.now(), username, action, project, module, server, dateRange, status, ip);
        String file = fileConfigStore.getSystemSettings().getAuditFile();
        Path path = Path.of(file);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write audit log", e);
        }
    }

    public List<String> search(String contains) throws IOException {
        Path path = Path.of(fileConfigStore.getSystemSettings().getAuditFile());
        if (!Files.exists(path)) {
            return List.of();
        }
        try (var lines = Files.lines(path)) {
            return lines.filter(l -> l.contains(contains)).toList();
        }
    }
}
