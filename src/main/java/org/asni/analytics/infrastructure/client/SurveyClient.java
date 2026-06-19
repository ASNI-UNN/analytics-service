package org.asni.analytics.infrastructure.client;

import org.asni.analytics.infrastructure.client.dto.SurveyDto;
import org.asni.analytics.infrastructure.client.dto.SurveyResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
public class SurveyClient {

    private final WebClient webClient;

    public SurveyClient(@Value("${services.survey.url}") String surveyUrl) {
        this.webClient = WebClient.builder().baseUrl(surveyUrl).build();
    }

    public SurveyDto getSurvey(UUID surveyId, String token) {
        return webClient.get()
            .uri("/api/v1/surveys/{id}", surveyId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(SurveyDto.class)
            .block();
    }

    public List<SurveyResponseDto> getResponses(UUID surveyId, String token) {
        return webClient.get()
            .uri("/api/v1/surveys/{id}/responses", surveyId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<SurveyResponseDto>>() {})
            .block();
    }
}
