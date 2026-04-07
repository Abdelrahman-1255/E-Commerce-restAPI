package com.abdelrahman.e_com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.abdelrahman.e_com.model.dto.ImageGenerationResponse;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeminiImageService {

    private final RestClient restClient;

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash-preview-image-generation}")
    private String model;

    public GeminiImageService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public ImageGenerationResponse generateProductImage(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be empty");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing Gemini API key. Set gemini.api-key in application.properties");
        }

        String finalPrompt = "Create a clean, realistic e-commerce product image for this description: " + prompt
                + ". Use a neutral background, centered subject, and good lighting.";

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", finalPrompt)))),
                "generationConfig", Map.of("responseModalities", List.of("TEXT", "IMAGE")));

        JsonNode root = restClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}",
                        model,
                        apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        if (root == null) {
            throw new IllegalStateException("Empty response from Gemini API");
        }

        JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
        String imageBase64 = null;
        String mimeType = "image/png";
        String textResponse = null;

        if (parts.isArray()) {
            for (JsonNode part : parts) {
                if (part.has("inlineData")) {
                    JsonNode inlineData = part.path("inlineData");
                    imageBase64 = inlineData.path("data").asText(null);
                    mimeType = inlineData.path("mimeType").asText("image/png");
                }
                if (part.has("text")) {
                    textResponse = part.path("text").asText();
                }
            }
        }

        if (imageBase64 == null || imageBase64.isBlank()) {
            throw new IllegalStateException("No image returned by model. Try a more detailed product description.");
        }

        String imageDataUrl = "data:" + mimeType + ";base64," + imageBase64;
        return new ImageGenerationResponse(mimeType, imageDataUrl, textResponse);
    }
}