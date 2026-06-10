package com.amani.amaniapirest.services.iaInsert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final WebClient webClient;

    public EmbeddingService(
            @Value("${openai.api.key}")
            String apiKey
    ) {

        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();
        System.out.println("API KEY cargada: " + (apiKey != null));
    }

    public List<Double> getEmbedding(String text) {

        Map<String, Object> body = Map.of(
                "model", "text-embedding-3-small",
                "input", text
        );

        return webClient.post()
                .uri("/embeddings")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List data = (List) response.get("data");
                    Map item = (Map) data.get(0);
                    return (List<Double>) item.get("embedding");
                })
                .block();
    }
}