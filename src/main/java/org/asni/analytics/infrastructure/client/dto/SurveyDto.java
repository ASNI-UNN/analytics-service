package org.asni.analytics.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record SurveyDto(
    UUID id,
    String title,
    String description,
    String status,
    List<QuestionDto> questions
) {}
