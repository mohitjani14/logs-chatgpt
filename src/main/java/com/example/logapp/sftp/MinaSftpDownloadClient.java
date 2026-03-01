package com.example.logapp.sftp;

import com.example.logapp.config.ServerConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class MinaSftpDownloadClient implements SftpDownloadClient {

    @Override
    public List<Path> downloadLogs(ServerConfig serverConfig, LocalDate from, LocalDate to, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        Path sourceDir = resolveSource(serverConfig);
        List<Path> downloads = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + serverConfig.getLogPattern());

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path source : stream) {
                if (!matcher.matches(source.getFileName())) {
                    continue;
                }
                FileTime mtime = Files.getLastModifiedTime(source);
                LocalDate modified = mtime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (modified.isBefore(from) || modified.isAfter(to)) {
                    continue;
                }
                Path target = targetDir.resolve(source.getFileName().toString());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                downloads.add(target);
            }
        }
        return downloads;
    }

    private Path resolveSource(ServerConfig serverConfig) {
        if (!"localhost".equalsIgnoreCase(serverConfig.getHost()) && !"127.0.0.1".equals(serverConfig.getHost())) {
            throw new IllegalArgumentException("Demo implementation supports localhost only; replace with Apache Mina remote SFTP wiring.");
        }
        return Path.of(serverConfig.getLogDirectory());
    }
}
