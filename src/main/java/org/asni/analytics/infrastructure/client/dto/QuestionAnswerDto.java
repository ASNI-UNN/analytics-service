package org.asni.analytics.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record QuestionAnswerDto(
    UUID questionId,
    String textValue,
    List<UUID> selectedOptionIds,
    Integer scaleValue
) {}
