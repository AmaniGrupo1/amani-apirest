package com.amani.amaniapirest.services.profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    // Extensiones de imagen permitidas
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    // Tamaño máximo: 5 MB
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024L;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Guarda un archivo de imagen y devuelve la URL relativa para acceder a él.
     *
     * @throws IllegalArgumentException si el tipo de archivo no está permitido o supera el tamaño máximo
     * @throws RuntimeException         si ocurre un error de I/O al guardar el archivo
     */
    public String storeFile(MultipartFile file) {

        // --- Validación: archivo vacío ---
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        // --- Validación: tamaño máximo (5 MB) ---
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "El archivo supera el tamaño máximo permitido de 5 MB");
        }

        // --- Validación: extensión permitida ---
        String extension = getExtension(file.getOriginalFilename()).toLowerCase();
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido. Solo se aceptan: jpg, jpeg, png, gif, webp");
        }

        try {
            // Resolver siempre en ruta ABSOLUTA para que Files.copy funcione correctamente
            // (transferTo con ruta relativa falla en Spring Boot porque usa el directorio
            //  temporal del contenedor, no el directorio de trabajo de la aplicación)
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Crear carpeta si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Nombre único para evitar colisiones
            String filename = UUID.randomUUID() + "." + extension;

            // Guardar usando Files.copy (más fiable que transferTo con rutas relativas)
            Path destino = uploadPath.resolve(filename).normalize();
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return "/files/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en disco", e);
        }
    }

    /**
     * Elimina un archivo del disco a partir de su URL relativa.
     * Si el archivo no existe, no lanza excepción (operación idempotente).
     *
     * @param fileUrl URL relativa con formato "/files/nombre.ext"
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            // Extraer solo el nombre de archivo de la URL: "/files/uuid.jpg" → "uuid.jpg"
            String filename = Paths.get(fileUrl).getFileName().toString();
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Error no crítico: se registra pero no interrumpe el flujo
            throw new RuntimeException("Error al eliminar el archivo: " + fileUrl, e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf(".");
        return (dot >= 0) ? filename.substring(dot + 1) : "";
    }
}