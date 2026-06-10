package com.amani.amaniapirest.services.iaInsert;

import com.amani.amaniapirest.models.iaInsert.ChunkRag;
import com.amani.amaniapirest.repository.iaInsert.ChunkRagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagSearchService {

    private final EmbeddingService embeddingService;
    private final ChunkRagRepository chunkRepo;

    @Value("${rag.search.max-results:10}")
    private int maxResults;

    @Value("${rag.search.similarity-threshold:0.60}")
    private double similarityThreshold;

    public RagSearchService(EmbeddingService embeddingService,
                            ChunkRagRepository chunkRepo) {
        this.embeddingService = embeddingService;
        this.chunkRepo = chunkRepo;
    }

    /**
     * Búsqueda principal con expansión de consulta
     */
    public List<ChunkRag> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        System.out.println("=== BÚSQUEDA RAG ===");
        System.out.println("Consulta original: " + query);

        // PASO 1: Expandir la consulta con sinónimos y variantes
        List<String> expandedQueries = expandQuery(query);
        System.out.println("Consultas expandidas: " + expandedQueries.size());

        // PASO 2: Generar embedding para CADA consulta expandida
        Map<ChunkRag, Double> allScores = new HashMap<>();

        for (String expandedQuery : expandedQueries) {
            List<Double> embedding = embeddingService.getEmbedding(expandedQuery);
            if (embedding == null || embedding.isEmpty()) {
                continue;
            }

            // Buscar chunks similares
            List<ChunkRag> allChunks = chunkRepo.findAll();

            for (ChunkRag chunk : allChunks) {
                if (chunk.getEmbeddingId() == null || chunk.getEmbeddingId().isBlank()) {
                    continue;
                }

                List<Double> chunkEmbedding = parseEmbedding(chunk.getEmbeddingId());
                double score = cosineSimilarity(embedding, chunkEmbedding);

                // Guardar la mejor puntuación para cada chunk
                if (score >= similarityThreshold) {
                    allScores.merge(chunk, score, Math::max);
                }
            }
        }

        // PASO 3: Si no hay resultados, hacer búsqueda por palabras clave (fallback)
        if (allScores.isEmpty()) {
            System.out.println("Sin resultados semánticos, usando fallback por keywords...");
            return keywordSearch(query);
        }

        // PASO 4: Ordenar y limitar resultados
        List<ChunkRag> results = allScores.entrySet().stream()
                .sorted(Map.Entry.<ChunkRag, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("Resultados encontrados: " + results.size());
        if (!results.isEmpty()) {
            System.out.println("Mejor puntuación: " + allScores.get(results.get(0)));
        }

        return results;
    }

    /**
     * Expande la consulta con sinónimos, variantes y correcciones
     */
    private List<String> expandQuery(String query) {
        List<String> expanded = new ArrayList<>();

        // Añadir la consulta original
        expanded.add(query);

        String lowerQuery = query.toLowerCase();

        // Diccionario de sinónimos y variantes para psicología
        Map<String, List<String>> synonyms = new HashMap<>();

        // Términos principales y sus variantes
        synonyms.put("psicologo", List.of("psicólogo", "psicologos", "psicólogos", "psicologia", "psicología", "terapeuta", "profesional de la salud mental"));
        synonyms.put("estudian", List.of("estudio", "estudia", "estudiar", "estudiaban", "investigan", "analizan", "examinan"));
        synonyms.put("ansiedad", List.of("ansioso", "nervios", "preocupacion", "estres", "angustia", "miedo", "temor"));
        synonyms.put("depresion", List.of("deprimido", "tristeza", "apatia", "desanimo", "melancolia", "triste"));
        synonyms.put("terapia", List.of("tratamiento", "sesion", "consulta", "psicoterapia", "acompañamiento"));
        synonyms.put("psiquiatra", List.of("medico psiquiatra", "psiquiatria", "farmacos", "medicacion", "pastillas"));
        synonyms.put("duelo", List.of("perdida", "muerte", "fallecimiento", "separacion", "ruptura"));
        synonyms.put("estres", List.of("burnout", "agotamiento", "cansancio", "presion", "sobrecarga"));
        synonyms.put("ansiedad social", List.of("fobia social", "timidez", "vergüenza", "juicio social"));
        synonyms.put("autoestima", List.of("autoconcepto", "autovaloracion", "confianza en uno mismo", "inseguridad"));

        // Aplicar sinónimos
        String expandedQuery = query;
        for (Map.Entry<String, List<String>> entry : synonyms.entrySet()) {
            if (lowerQuery.contains(entry.getKey())) {
                for (String synonym : entry.getValue()) {
                    String replacement = query.replaceAll("(?i)" + entry.getKey(), synonym);
                    if (!expanded.contains(replacement)) {
                        expanded.add(replacement);
                    }
                }
            }
        }

        // Variante con corrección de tildes (quitar tildes)
        String noAccents = removeAccents(lowerQuery);
        if (!noAccents.equals(lowerQuery)) {
            expanded.add(noAccents);
        }

        // Variante con primera letra mayúscula
        String capitalized = query.substring(0, 1).toUpperCase() + (query.length() > 1 ? query.substring(1).toLowerCase() : "");
        if (!expanded.contains(capitalized)) {
            expanded.add(capitalized);
        }

        // Variante en minúsculas
        String allLower = query.toLowerCase();
        if (!expanded.contains(allLower)) {
            expanded.add(allLower);
        }

        // Si la pregunta no tiene signos, añadir versión con signos
        if (!query.contains("¿") && !query.contains("?")) {
            expanded.add("¿" + query + "?");
        }

        // Si la pregunta empieza con "que", añadir versión con "qué"
        if (lowerQuery.startsWith("que ")) {
            String withAccent = "qué " + query.substring(4);
            if (!expanded.contains(withAccent)) {
                expanded.add(withAccent);
            }
        }

        // Si la pregunta NO tiene "qué", añadir versión con "qué"
        if (!lowerQuery.startsWith("qué") && !lowerQuery.startsWith("que ")) {
            String withQue = "qué es " + query;
            if (!expanded.contains(withQue)) {
                expanded.add(withQue);
            }
        }

        // Añadir versión como pregunta completa en español formal
        String formalQuestion = "¿Qué estudian los psicólogos?";
        if (lowerQuery.contains("psicolog") && (lowerQuery.contains("estudi") || lowerQuery.contains("qué"))) {
            if (!expanded.contains(formalQuestion)) {
                expanded.add(formalQuestion);
            }
        }

        return expanded;
    }

    /**
     * Elimina tildes y acentos
     */
    private String removeAccents(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    /**
     * Búsqueda por palabras clave (fallback cuando la búsqueda semántica falla)
     */
    private List<ChunkRag> keywordSearch(String query) {
        List<ChunkRag> allChunks = chunkRepo.findAll();
        String lowerQuery = query.toLowerCase();

        // Extraer palabras clave importantes (ignorar palabras comunes)
        Set<String> stopWords = Set.of("que", "qué", "como", "cómo", "para", "por", "con", "sin", "una", "una", "lo", "la", "el", "los", "las", "y", "o", "pero", "si", "no");
        String[] words = lowerQuery.split("\\s+");
        List<String> keywords = Arrays.stream(words)
                .filter(w -> w.length() > 3)
                .filter(w -> !stopWords.contains(w))
                .collect(Collectors.toList());

        if (keywords.isEmpty()) {
            keywords = List.of(lowerQuery);
        }

        System.out.println("Keywords para búsqueda: " + keywords);

        // Buscar chunks que contengan las keywords
        Map<ChunkRag, Integer> keywordMatches = new HashMap<>();

        for (ChunkRag chunk : allChunks) {
            String content = chunk.getContenido().toLowerCase();
            int matches = 0;
            for (String keyword : keywords) {
                if (content.contains(keyword)) {
                    matches++;
                }
            }
            if (matches > 0) {
                keywordMatches.put(chunk, matches);
            }
        }

        return keywordMatches.entrySet().stream()
                .sorted(Map.Entry.<ChunkRag, Integer>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Parsea embedding desde String a List<Double>
     */
    private List<Double> parseEmbedding(String embedding) {
        if (embedding == null || embedding.isBlank()) {
            return List.of();
        }
        try {
            return Arrays.stream(embedding.replace("[", "").replace("]", "").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.equals("null"))
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Calcula cosine similarity entre dos vectores
     */
    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty() || a.size() != b.size()) {
            return -1;
        }
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        if (normA == 0 || normB == 0) return -1;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}