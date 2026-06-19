package org.asni.analytics.infrastructure.web.dto;

import org.asni.analytics.domain.model.QuestionSummary;
import org.asni.analytics.domain.model.QuestionType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record QuestionSummaryResponse(
    UUID questionId,
    String questionText,
    QuestionType type,
    int totalAnswers,
    Map<String, Long> optionCounts,
    Double averageScale,
    List<String> textAnswers
) {
    public static QuestionSummaryResponse from(QuestionSummary s) {
        return new QuestionSummaryResponse(
            s.getQuestionId(), s.getQuestionText(), s.getType(),
            s.getTotalAnswers(), s.getOptionCounts(), s.getAverageScale(), s.getTextAnswers()
        );
    }
}
