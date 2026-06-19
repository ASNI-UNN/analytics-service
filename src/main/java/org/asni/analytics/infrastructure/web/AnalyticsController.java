package org.asni.analytics.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asni.analytics.domain.port.in.AnalyticsUseCase;
import org.asni.analytics.infrastructure.web.dto.SurveySummaryResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics")
class AnalyticsController {

    private final AnalyticsUseCase analyticsUseCase;

    AnalyticsController(AnalyticsUseCase analyticsUseCase) {
        this.analyticsUseCase = analyticsUseCase;
    }

    @GetMapping("/surveys/{id}/summary")
    @Operation(summary = "Aggregated statistics for a survey")
    ResponseEntity<SurveySummaryResponse> summary(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(SurveySummaryResponse.from(analyticsUseCase.getSurveySummary(id, token)));
    }

    @GetMapping("/surveys/{id}/export")
    @Operation(summary = "Export survey responses as CSV")
    ResponseEntity<byte[]> export(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        byte[] csv = analyticsUseCase.exportSurveyCsv(id, token);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"survey-" + id + ".csv\"")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(csv);
    }
}
