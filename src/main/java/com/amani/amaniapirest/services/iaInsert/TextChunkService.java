package com.amani.amaniapirest.services.iaInsert;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextChunkService {

    // Nueva configuración para documentos técnicos
    private static final int CHUNK_SIZE = 1500;      // 1500 caracteres (~250-300 palabras)
    private static final int OVERLAP = 200;          // 200 caracteres de solapamiento
    private static final int MIN_CHUNK_SIZE = 300;   // Chunks más pequeños no se guardan

    /**
     * Versión mejorada: chunking inteligente que respeta párrafos y oraciones
     */
    public List<String> splitSemantic(String text) {
        return splitSemantic(text, CHUNK_SIZE, OVERLAP);
    }

    public List<String> splitSemantic(String text, int size, int overlap) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        if (overlap >= size) {
            throw new IllegalArgumentException("overlap debe ser menor que size");
        }

        List<String> chunks = new ArrayList<>();

        // Primero, dividimos por párrafos (doble salto de línea)
        String[] paragraphs = text.split("\\n\\s*\\n");

        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            // Si el párrafo actual ya es muy grande, lo dividimos por oraciones
            if (paragraph.length() > size) {
                // Guardar chunk actual si tiene contenido
                if (currentChunk.length() > MIN_CHUNK_SIZE) {
                    chunks.add(currentChunk.toString());
                    currentChunk = new StringBuilder(getLastSentences(currentChunk.toString(), 3));
                }

                // Dividir párrafo grande por oraciones
                List<String> sentences = splitBySentences(paragraph);
                for (String sentence : sentences) {
                    if (currentChunk.length() + sentence.length() + 1 > size) {
                        if (currentChunk.length() > MIN_CHUNK_SIZE) {
                            chunks.add(currentChunk.toString());
                            currentChunk = new StringBuilder(getLastSentences(currentChunk.toString(), 2));
                        }
                    }
                    if (currentChunk.length() > 0) {
                        currentChunk.append(" ");
                    }
                    currentChunk.append(sentence);
                }
            }
            else if (currentChunk.length() + paragraph.length() + 2 > size) {
                // El párrafo actual no cabe en el chunk → guardamos chunk actual
                if (currentChunk.length() > MIN_CHUNK_SIZE) {
                    chunks.add(currentChunk.toString());
                }
                // Iniciamos nuevo chunk con solapamiento (últimas 2-3 oraciones)
                String overlapText = getLastSentences(paragraph, 2);
                currentChunk = new StringBuilder(overlapText);
                if (!overlapText.isEmpty() && !paragraph.equals(overlapText)) {
                    currentChunk.append(" ");
                    currentChunk.append(paragraph);
                } else {
                    currentChunk = new StringBuilder(paragraph);
                }
            }
            else {
                // Cabe en el chunk actual
                if (currentChunk.length() > 0) {
                    currentChunk.append("\n\n");
                }
                currentChunk.append(paragraph);
            }
        }

        // Último chunk
        if (currentChunk.length() > MIN_CHUNK_SIZE) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }

    /**
     * Divide un texto por oraciones (., !, ? seguido de espacio o salto)
     */
    private List<String> splitBySentences(String text) {
        List<String> sentences = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^.!?]+[.!?]\\s*)");
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;
        while (matcher.find()) {
            String sentence = matcher.group(1).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            lastEnd = matcher.end();
        }

        // Lo que quede (última parte sin puntuación)
        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd).trim();
            if (!remaining.isEmpty()) {
                sentences.add(remaining);
            }
        }

        return sentences;
    }

    /**
     * Obtiene las últimas N oraciones de un texto (para solapamiento)
     */
    private String getLastSentences(String text, int n) {
        List<String> sentences = splitBySentences(text);
        if (sentences.size() <= n) {
            return text;
        }

        List<String> lastSentences = sentences.subList(sentences.size() - n, sentences.size());
        return String.join(" ", lastSentences);
    }

    // Método legacy para compatibilidad
    public List<String> split(String text, int size, int overlap) {
        return splitSemantic(text, size, overlap);
    }
}