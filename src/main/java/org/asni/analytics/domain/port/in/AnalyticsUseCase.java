package org.asni.analytics.domain.port.in;

import org.asni.analytics.domain.model.SurveySummary;

import java.util.UUID;

public interface AnalyticsUseCase {
    SurveySummary getSurveySummary(UUID surveyId, String bearerToken);
    byte[] exportSurveyCsv(UUID surveyId, String bearerToken);
}
