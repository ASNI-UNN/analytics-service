package org.asni.analytics.domain.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestionSummary {
    private final UUID questionId;
    private final String questionText;
    private final QuestionType type;
    private final int totalAnswers;
    private final Map<String, Long> optionCounts;
    private final Double averageScale;
    private final List<String> textAnswers;

    public QuestionSummary(UUID questionId, String questionText, QuestionType type,
                           int totalAnswers, Map<String, Long> optionCounts,
                           Double averageScale, List<String> textAnswers) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.type = type;
        this.totalAnswers = totalAnswers;
        this.optionCounts = optionCounts;
        this.averageScale = averageScale;
        this.textAnswers = textAnswers;
    }

    public UUID getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public QuestionType getType() { return type; }
    public int getTotalAnswers() { return totalAnswers; }
    public Map<String, Long> getOptionCounts() { return optionCounts; }
    public Double getAverageScale() { return averageScale; }
    public List<String> getTextAnswers() { return textAnswers; }
}
