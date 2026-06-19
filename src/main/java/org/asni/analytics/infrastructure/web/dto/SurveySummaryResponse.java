package org.asni.analytics.infrastructure.web.dto;

import org.asni.analytics.domain.model.SurveySummary;

import java.util.List;
import java.util.UUID;

public record SurveySummaryResponse(
    UUID surveyId,
    String surveyTitle,
    int totalResponses,
    List<QuestionSummaryResponse> questions
) {
    public static SurveySummaryResponse from(SurveySummary s) {
        return new SurveySummaryResponse(
            s.getSurveyId(), s.getSurveyTitle(), s.getTotalResponses(),
            s.getQuestions().stream().map(QuestionSummaryResponse::from).toList()
        );
    }
}
