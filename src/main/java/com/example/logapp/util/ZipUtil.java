package com.example.logapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtil {
    private ZipUtil() {}

    public static void zipFiles(List<Path> files, Path zipPath) throws IOException {
        Files.createDirectories(zipPath.getParent());
        try (OutputStream out = Files.newOutputStream(zipPath); ZipOutputStream zos = new ZipOutputStream(out)) {
            byte[] buffer = new byte[8192];
            for (Path file : files) {
                zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                try (InputStream in = Files.newInputStream(file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
    }
}
