package com.amani.amaniapirest.controllers.iaInsert;

import com.amani.amaniapirest.configuration.ia.DocumentIngestionService;
import com.amani.amaniapirest.dto.copiloto.AskRequest;
import com.amani.amaniapirest.dto.copiloto.ChunkResponseDTO;
import com.amani.amaniapirest.dto.copiloto.UploadResponse;
import com.amani.amaniapirest.models.iaInsert.ChunkRag;
import com.amani.amaniapirest.models.iaInsert.DocumentoRag;
import com.amani.amaniapirest.repository.iaInsert.ChunkRagRepository;
import com.amani.amaniapirest.repository.iaInsert.DocumentoRepository;
import com.amani.amaniapirest.services.extractorPDF.PdfExtractorService;
import com.amani.amaniapirest.services.iaInsert.RagSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final DocumentoRepository documentoRepo;
    private final RagSearchService searchService;
    private final DocumentIngestionService ingestionService;
    private final PdfExtractorService pdfExtractorService;
    private final ChunkRagRepository chunkRepo;

    // Patrones para detectar saludos y presentaciones
    private static final List<Pattern> GREETING_PATTERNS = List.of(
            Pattern.compile("(?i)^\\s*(hola|saludos|buenas|qué tal|como estas|hello|hi|hey|buenos días|buenas tardes|buenas noches)\\s*$"),
            Pattern.compile("(?i)^\\s*(hola|saludos|buenas|qué tal|como estas|hello|hi|hey)\\s*!+\\s*$"),
            Pattern.compile("(?i)\\b(presentate|quién eres|qué eres|qué sabes hacer|qué puedes hacer|cómo te llamas|presentación|quien eres|que eres|que sabes hacer|que puedes hacer|como te llamas)\\b")
    );

    // Mensaje de presentación
    private static final String GREETING_RESPONSE =
            "🌟 ¡Hola! Soy tu asistente virtual especializado en psicología clínica. 🌟\n\n" +
                    "📚 LO QUE PUEDO HACER POR TI:\n" +
                    "• Responder preguntas sobre psicología y salud mental\n" +
                    "• Explicar conceptos como ansiedad, depresión, estrés o duelo\n" +
                    "• Informarte sobre técnicas de autocuidado y herramientas psicológicas\n" +
                    "• Aclarar diferencias entre psicólogo, psiquiatra y otros profesionales\n" +
                    "• Resolver dudas sobre el proceso terapéutico\n\n" +
                    "💡 EJEMPLOS DE PREGUNTAS QUE PUEDES HACERME:\n" +
                    "• ¿Qué estudian los psicólogos?\n" +
                    "• ¿Qué es la ansiedad y cómo se trata?\n" +
                    "• ¿Cuánto dura una terapia psicológica?\n" +
                    "• ¿Qué técnicas de relajación recomiendas?\n" +
                    "• ¿Cuál es la diferencia entre psicólogo y psiquiatra?\n" +
                    "• ¿Qué es el duelo?\n" +
                    "• ¿Cómo puedo manejar el estrés?\n\n" +
                    "🔒 CONFIDENCIALIDAD: Recuerda que soy una herramienta informativa. " +
                    "No sustituyo a un profesional de la salud mental. " +
                    "Si tienes una emergencia psicológica, contacta con los servicios de urgencia de tu localidad (112 o 024 en España).\n\n" +
                    "✨ ¿En qué puedo ayudarte hoy?";

    // Mensaje cuando no se encuentra información
    private static final String NO_RESULTS_RESPONSE_TEMPLATE =
            "❌ No encontré información específica sobre: \"%s\"\n\n" +
                    "📝 ¿Puedes intentar reformular tu pregunta? Aquí hay algunos ejemplos:\n" +
                    "• ¿Qué estudian los psicólogos?\n" +
                    "• ¿Qué es la ansiedad?\n" +
                    "• ¿Cómo funciona la terapia psicológica?\n" +
                    "• ¿Qué diferencia hay entre psicólogo y psiquiatra?\n" +
                    "• ¿Qué técnicas de autocuidado existen?\n" +
                    "• ¿Qué es el duelo?\n\n" +
                    "💡 Si tu pregunta es sobre un tema específico, intenta usar palabras clave como: psicología, terapia, ansiedad, depresión, duelo, estrés, autocuidado, psicólogo, psiquiatra.";

    @PostMapping("/ask")
    public ResponseEntity<List<ChunkResponseDTO>> ask(@RequestBody AskRequest request) {

        String question = request.getQuestion();

        System.out.println("=== NUEVA CONSULTA ===");
        System.out.println("Pregunta: " + question);

        // VERIFICAR SI ES UN SALUDO O PRESENTACIÓN
        if (isGreeting(question)) {
            System.out.println("✅ Detectado como saludo/presentación");
            return ResponseEntity.ok(createGreetingResponse());
        }

        // VERIFICAR SI ES UNA PREGUNTA VACÍA O MUY CORTA
        if (question == null || question.trim().length() < 3) {
            System.out.println("⚠️ Pregunta demasiado corta o vacía");
            return ResponseEntity.ok(createNoResultsResponse(question));
        }

        // BÚSQUEDA NORMAL RAG
        System.out.println("🔍 Buscando en RAG...");
        List<ChunkRag> searchResults = searchService.search(question);

        if (searchResults == null || searchResults.isEmpty()) {
            System.out.println("❌ Sin resultados de búsqueda");
            return ResponseEntity.ok(createNoResultsResponse(question));
        }

        System.out.println("✅ Encontrados " + searchResults.size() + " resultados");

        List<ChunkResponseDTO> response = searchResults.stream()
                .map(chunk -> {
                    ChunkResponseDTO dto = new ChunkResponseDTO();
                    dto.setIdChunk(chunk.getIdChunk());
                    dto.setChunkIndex(chunk.getChunkIndex());
                    dto.setContenido(chunk.getContenido());
                    dto.setCreadoEn(chunk.getCreadoEn());

                    if (chunk.getDocumento() != null) {
                        dto.setDocumentoId(chunk.getDocumento().getIdDocumento());
                        dto.setNombreDocumento(chunk.getDocumento().getNombreArchivo());
                    }

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Detecta si el mensaje es un saludo o solicitud de presentación
     */
    private boolean isGreeting(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }

        String trimmed = message.trim().toLowerCase();

        // Saludos muy cortos y directos
        List<String> directGreetings = List.of(
                "hola", "hola!", "hola?", "buenas", "saludos", "hey", "hi", "hello",
                "que tal", "qué tal", "como estas", "cómo estás", "buenos días",
                "buenas tardes", "buenas noches", "hola como estas", "hola qué tal"
        );

        for (String greeting : directGreetings) {
            if (trimmed.equals(greeting) || trimmed.startsWith(greeting + " ")) {
                return true;
            }
        }

        // Verificar con patrones regex
        for (Pattern pattern : GREETING_PATTERNS) {
            if (pattern.matcher(message).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Crea la respuesta de presentación para saludos
     */
    private List<ChunkResponseDTO> createGreetingResponse() {
        List<ChunkResponseDTO> response = new ArrayList<>();
        ChunkResponseDTO dto = new ChunkResponseDTO();
        dto.setIdChunk(-1L);
        dto.setChunkIndex(0);
        dto.setContenido(GREETING_RESPONSE);
        dto.setDocumentoId(null);
        dto.setNombreDocumento("ASISTENTE");
        dto.setCreadoEn(LocalDateTime.now());
        response.add(dto);
        return response;
    }

    /**
     * Crea respuesta cuando no se encuentran resultados
     */
    private List<ChunkResponseDTO> createNoResultsResponse(String question) {
        List<ChunkResponseDTO> response = new ArrayList<>();
        ChunkResponseDTO dto = new ChunkResponseDTO();
        dto.setIdChunk(-2L);
        dto.setChunkIndex(0);
        dto.setContenido(String.format(NO_RESULTS_RESPONSE_TEMPLATE,
                question != null ? question : "vacío"));
        dto.setDocumentoId(null);
        dto.setNombreDocumento("ASISTENTE");
        dto.setCreadoEn(LocalDateTime.now());
        response.add(dto);
        return response;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new UploadResponse(null, "Archivo vacío")
                );
            }

            String nombreArchivo = file.getOriginalFilename();
            String titulo = nombreArchivo;

            if (nombreArchivo != null && nombreArchivo.contains(".")) {
                titulo = nombreArchivo.substring(0, nombreArchivo.lastIndexOf("."));
            }

            DocumentoRag documento = new DocumentoRag();
            documento.setTitulo(titulo);
            documento.setCategoria("PDF");
            documento.setFuente("UPLOAD");
            documento.setNombreArchivo(nombreArchivo);
            documento.setTotalChunks(0);
            documento = documentoRepo.save(documento);

            String text = pdfExtractorService.extractPdf(file);
            System.out.println("LONGITUD TEXTO: " + text.length());

            ingestionService.ingest(documento.getIdDocumento(), text);

            return ResponseEntity.ok(new UploadResponse(
                    documento.getIdDocumento(),
                    "Documento procesado correctamente"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    new UploadResponse(null, "Error procesando PDF: " + e.getMessage())
            );
        }
    }

    @GetMapping("/debug/chunks")
    public ResponseEntity<Map<String, Object>> debugChunks() {
        List<ChunkRag> allChunks = chunkRepo.findAll();

        Map<String, Object> debug = new HashMap<>();
        debug.put("totalChunks", allChunks.size());
        debug.put("timestamp", LocalDateTime.now());

        List<Map<String, Object>> chunkInfo = new ArrayList<>();
        for (ChunkRag chunk : allChunks) {
            Map<String, Object> info = new HashMap<>();
            info.put("id", chunk.getIdChunk());
            info.put("index", chunk.getChunkIndex());
            info.put("documento", chunk.getDocumento() != null ? chunk.getDocumento().getNombreArchivo() : "sin doc");
            info.put("longitud", chunk.getContenido().length());

            String preview = chunk.getContenido().length() > 300 ?
                    chunk.getContenido().substring(0, 300) + "..." :
                    chunk.getContenido();
            info.put("preview", preview);
            chunkInfo.add(info);
        }
        debug.put("chunks", chunkInfo);

        return ResponseEntity.ok(debug);
    }

    @GetMapping("/debug/greeting-test")
    public ResponseEntity<Map<String, Object>> testGreeting(@RequestParam String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("isGreeting", isGreeting(message));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reprocess/{idDocumento}")
    public ResponseEntity<Map<String, String>> reprocessDocument(@PathVariable Long idDocumento) {
        DocumentoRag doc = documentoRepo.findById(idDocumento).orElse(null);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        // Eliminar chunks existentes usando el método correcto del repositorio
        List<ChunkRag> existingChunks = chunkRepo.findByDocumento_IdDocumento(idDocumento);

        if (existingChunks != null && !existingChunks.isEmpty()) {
            chunkRepo.deleteAll(existingChunks);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Documento reprocesado. Los chunks antiguos han sido eliminados. Sube el documento nuevamente para generar nuevos chunks.");
        response.put("documentoId", idDocumento.toString());
        response.put("chunksEliminados", String.valueOf(existingChunks != null ? existingChunks.size() : 0));

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/documents/{idDocumento}")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long idDocumento) {

        DocumentoRag documento = documentoRepo.findById(idDocumento)
                .orElse(null);

        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        documentoRepo.delete(documento);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Documento eliminado correctamente");
        response.put("idDocumento", idDocumento.toString());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/documents")
    public ResponseEntity<List<Map<String, Object>>> getDocuments() {

        List<Map<String, Object>> documents = documentoRepo.findAll()
                .stream()
                .map(doc -> {
                    Map<String, Object> item = new HashMap<>();

                    item.put("idDocumento", doc.getIdDocumento());
                    item.put("titulo", doc.getTitulo());
                    item.put("categoria", doc.getCategoria());
                    item.put("fuente", doc.getFuente());
                    item.put("nombreArchivo", doc.getNombreArchivo());
                    item.put("totalChunks", doc.getTotalChunks());
                    item.put("creadoEn", doc.getCreadoEn());

                    return item;
                })
                .toList();

        return ResponseEntity.ok(documents);
    }
}