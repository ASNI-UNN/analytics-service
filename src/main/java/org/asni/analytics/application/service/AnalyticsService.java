package org.asni.analytics.application.service;

import org.asni.analytics.domain.model.QuestionSummary;
import org.asni.analytics.domain.model.QuestionType;
import org.asni.analytics.domain.model.SurveySummary;
import org.asni.analytics.domain.port.in.AnalyticsUseCase;
import org.asni.analytics.infrastructure.client.SurveyClient;
import org.asni.analytics.infrastructure.client.dto.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService implements AnalyticsUseCase {

    private final SurveyClient surveyClient;

    public AnalyticsService(SurveyClient surveyClient) {
        this.surveyClient = surveyClient;
    }

    @Override
    public SurveySummary getSurveySummary(UUID surveyId, String bearerToken) {
        SurveyDto survey = surveyClient.getSurvey(surveyId, bearerToken);
        List<SurveyResponseDto> responses = surveyClient.getResponses(surveyId, bearerToken);

        List<QuestionSummary> questionSummaries = survey.questions().stream()
            .map(q -> aggregateQuestion(q, responses))
            .toList();

        return new SurveySummary(survey.id(), survey.title(), responses.size(), questionSummaries);
    }

    @Override
    public byte[] exportSurveyCsv(UUID surveyId, String bearerToken) {
        SurveyDto survey = surveyClient.getSurvey(surveyId, bearerToken);
        List<SurveyResponseDto> responses = surveyClient.getResponses(surveyId, bearerToken);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            List<String> headers = new ArrayList<>();
            headers.add("respondent");
            survey.questions().forEach(q -> headers.add(q.text()));
            writer.println(String.join(",", headers.stream().map(this::escapeCsv).toList()));

            Map<UUID, QuestionDto> questionMap = survey.questions().stream()
                .collect(Collectors.toMap(QuestionDto::id, q -> q));

            for (SurveyResponseDto response : responses) {
                List<String> row = new ArrayList<>();
                row.add(escapeCsv(response.respondentUsername()));

                Map<UUID, QuestionAnswerDto> answerByQuestion = response.answers().stream()
                    .collect(Collectors.toMap(QuestionAnswerDto::questionId, a -> a));

                for (QuestionDto question : survey.questions()) {
                    QuestionAnswerDto answer = answerByQuestion.get(question.id());
                    row.add(escapeCsv(formatAnswer(answer, question)));
                }
                writer.println(String.join(",", row));
            }
        }
        return out.toByteArray();
    }

    private QuestionSummary aggregateQuestion(QuestionDto question, List<SurveyResponseDto> responses) {
        QuestionType type = QuestionType.valueOf(question.type());

        List<QuestionAnswerDto> answers = responses.stream()
            .map(r -> r.answers().stream()
                .filter(a -> question.id().equals(a.questionId()))
                .findFirst()
                .orElse(null))
            .filter(Objects::nonNull)
            .toList();

        Map<UUID, String> optionTextMap = question.options().stream()
            .collect(Collectors.toMap(AnswerOptionDto::id, AnswerOptionDto::text));

        Map<String, Long> optionCounts = new LinkedHashMap<>();
        Double averageScale = null;
        List<String> textAnswers = new ArrayList<>();

        switch (type) {
            case SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                question.options().forEach(o -> optionCounts.put(o.text(), 0L));
                for (QuestionAnswerDto a : answers) {
                    if (a.selectedOptionIds() != null) {
                        for (UUID optId : a.selectedOptionIds()) {
                            String optText = optionTextMap.getOrDefault(optId, optId.toString());
                            optionCounts.merge(optText, 1L, Long::sum);
                        }
                    }
                }
            }
            case SCALE -> {
                List<Integer> scales = answers.stream()
                    .map(QuestionAnswerDto::scaleValue)
                    .filter(Objects::nonNull)
                    .toList();
                if (!scales.isEmpty()) {
                    averageScale = scales.stream().mapToInt(Integer::intValue).average().orElse(0);
                }
            }
            case TEXT -> {
                textAnswers = answers.stream()
                    .map(QuestionAnswerDto::textValue)
                    .filter(v -> v != null && !v.isBlank())
                    .toList();
            }
        }

        return new QuestionSummary(
            question.id(), question.text(), type,
            answers.size(), optionCounts, averageScale, textAnswers
        );
    }

    private String formatAnswer(QuestionAnswerDto answer, QuestionDto question) {
        if (answer == null) return "";
        if (answer.textValue() != null && !answer.textValue().isBlank()) return answer.textValue();
        if (answer.scaleValue() != null) return String.valueOf(answer.scaleValue());
        if (answer.selectedOptionIds() != null && !answer.selectedOptionIds().isEmpty()) {
            Map<UUID, String> optionMap = question.options().stream()
                .collect(Collectors.toMap(AnswerOptionDto::id, AnswerOptionDto::text));
            return answer.selectedOptionIds().stream()
                .map(id -> optionMap.getOrDefault(id, id.toString()))
                .collect(Collectors.joining("; "));
        }
        return "";
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
