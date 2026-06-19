package org.asni.analytics.application;

import org.asni.analytics.application.service.AnalyticsService;
import org.asni.analytics.domain.model.QuestionType;
import org.asni.analytics.domain.model.SurveySummary;
import org.asni.analytics.infrastructure.client.SurveyClient;
import org.asni.analytics.infrastructure.client.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SurveyClient surveyClient;

    @InjectMocks
    private AnalyticsService service;

    @Test
    void getSurveySummary_aggregatesResponses() {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        UUID optionAId = UUID.randomUUID();
        UUID optionBId = UUID.randomUUID();

        SurveyDto survey = new SurveyDto(
            surveyId, "Test Survey", "Desc", "PUBLISHED",
            List.of(new QuestionDto(
                questionId, "How do you rate?", "SINGLE_CHOICE", 0, true,
                List.of(
                    new AnswerOptionDto(optionAId, "Good", 0),
                    new AnswerOptionDto(optionBId, "Bad", 1)
                )
            ))
        );

        List<SurveyResponseDto> responses = List.of(
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "user1",
                List.of(new QuestionAnswerDto(questionId, null, List.of(optionAId), null))),
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "user2",
                List.of(new QuestionAnswerDto(questionId, null, List.of(optionAId), null))),
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "user3",
                List.of(new QuestionAnswerDto(questionId, null, List.of(optionBId), null)))
        );

        when(surveyClient.getSurvey(surveyId, "token")).thenReturn(survey);
        when(surveyClient.getResponses(surveyId, "token")).thenReturn(responses);

        SurveySummary summary = service.getSurveySummary(surveyId, "token");

        assertThat(summary.getTotalResponses()).isEqualTo(3);
        assertThat(summary.getQuestions()).hasSize(1);

        var q = summary.getQuestions().get(0);
        assertThat(q.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(q.getOptionCounts()).containsEntry("Good", 2L).containsEntry("Bad", 1L);
    }

    @Test
    void getSurveySummary_scaleQuestion_calculatesAverage() {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        SurveyDto survey = new SurveyDto(surveyId, "S", "D", "PUBLISHED",
            List.of(new QuestionDto(questionId, "Rate 1-5", "SCALE", 0, true, List.of()))
        );

        List<SurveyResponseDto> responses = List.of(
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "u1",
                List.of(new QuestionAnswerDto(questionId, null, null, 4))),
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "u2",
                List.of(new QuestionAnswerDto(questionId, null, null, 2)))
        );

        when(surveyClient.getSurvey(surveyId, "t")).thenReturn(survey);
        when(surveyClient.getResponses(surveyId, "t")).thenReturn(responses);

        SurveySummary summary = service.getSurveySummary(surveyId, "t");

        assertThat(summary.getQuestions().get(0).getAverageScale()).isEqualTo(3.0);
    }

    @Test
    void exportSurveyCsv_returnsCsvBytes() {
        UUID surveyId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        SurveyDto survey = new SurveyDto(surveyId, "Survey", "D", "PUBLISHED",
            List.of(new QuestionDto(questionId, "Comments", "TEXT", 0, false, List.of()))
        );

        List<SurveyResponseDto> responses = List.of(
            new SurveyResponseDto(UUID.randomUUID(), surveyId, UUID.randomUUID(), "alice",
                List.of(new QuestionAnswerDto(questionId, "Great!", null, null)))
        );

        when(surveyClient.getSurvey(surveyId, "t")).thenReturn(survey);
        when(surveyClient.getResponses(surveyId, "t")).thenReturn(responses);

        byte[] csv = service.exportSurveyCsv(surveyId, "t");
        String content = new String(csv);

        assertThat(content).contains("respondent").contains("Comments").contains("alice").contains("Great!");
    }
}
