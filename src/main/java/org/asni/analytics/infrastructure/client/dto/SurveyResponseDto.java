package org.asni.analytics.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record SurveyResponseDto(
    UUID id,
    UUID surveyId,
    UUID respondentId,
    String respondentUsername,
    List<QuestionAnswerDto> answers
) {}
