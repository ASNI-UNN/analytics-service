package org.asni.analytics.infrastructure.client.dto;

import java.util.UUID;

public record AnswerOptionDto(UUID id, String text, int orderIndex) {}
