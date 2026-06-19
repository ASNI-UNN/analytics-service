package org.asni.analytics.domain.model;

import java.util.List;
import java.util.UUID;

public class SurveySummary {
    private final UUID surveyId;
    private final String surveyTitle;
    private final int totalResponses;
    private final List<QuestionSummary> questions;

    public SurveySummary(UUID surveyId, String surveyTitle, int totalResponses, List<QuestionSummary> questions) {
        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
        this.totalResponses = totalResponses;
        this.questions = questions;
    }

    public UUID getSurveyId() { return surveyId; }
    public String getSurveyTitle() { return surveyTitle; }
    public int getTotalResponses() { return totalResponses; }
    public List<QuestionSummary> getQuestions() { return questions; }
}
