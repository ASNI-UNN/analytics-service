package org.asni.analytics.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record QuestionDto(
    UUID id,
    String text,
    String type,
    int orderIndex,
    boolean required,
    List<AnswerOptionDto> options
) {}
