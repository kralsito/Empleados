package com.gestion.empleados.shared.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "proofs");

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    /**
     * Guarda el archivo en ./uploads/proofs/ y devuelve el nombre del archivo generado.
     * Devuelve null si el archivo es nulo o está vacío.
     */
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + contentType);
        }

        Files.createDirectories(UPLOAD_DIR);

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;

        Path destination = UPLOAD_DIR.resolve(filename);
        file.transferTo(destination.toFile());

        return filename;
    }

    /**
     * Devuelve el Path absoluto del archivo a partir de su nombre.
     */
    public Path resolve(String filename) {
        return UPLOAD_DIR.resolve(filename).toAbsolutePath().normalize();
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        return dot >= 0 ? originalFilename.substring(dot) : "";
    }
}
