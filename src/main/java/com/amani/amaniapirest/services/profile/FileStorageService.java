package com.amani.amaniapirest.services.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Guarda un archivo y devuelve la URL relativa para acceder a él.
     */
    public String storeFile(MultipartFile file) {
        try {
            // Crear carpeta si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único
            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + extension;

            // Guardar archivo
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // Devolver URL relativa (el frontend puede construir la ruta completa)
            return "/files/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error guardando el archivo", e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf(".");
        return (dot >= 0) ? filename.substring(dot + 1) : "";
    }
}