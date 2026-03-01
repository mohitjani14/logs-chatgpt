package com.example.logapp.sftp;

import com.example.logapp.config.ServerConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface SftpDownloadClient {
    List<Path> downloadLogs(ServerConfig serverConfig, LocalDate from, LocalDate to, Path targetDir) throws IOException;
}
