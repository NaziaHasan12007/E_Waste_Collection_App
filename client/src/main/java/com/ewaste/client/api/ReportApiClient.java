package com.ewaste.client.api;

import com.ewaste.client.dto.response.AnalyticsClientResponse;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.time.LocalDate;

/**
 * Client for {@code com.ewaste.server.api.controller.ReportController},
 * backing Workflow 3. AnalyticsController calls getSummary()
 * to bind aggregate metrics to JavaFX chart models.
 */
public class ReportApiClient extends ApiClient {

    private static final String BASE_PATH = "/reports";

    /** Full-history summary — no date range applied. */
    public AnalyticsClientResponse getSummary() {
        return get(BASE_PATH + "/summary", AnalyticsClientResponse.class);
    }

    /** Summary scoped to [startDate, endDate], both inclusive. */
    public AnalyticsClientResponse getSummary(LocalDate startDate, LocalDate endDate) {
        String path = BASE_PATH + "/summary?"
                + "startDate=" + URLEncoder.encode(startDate.toString(), StandardCharsets.UTF_8)
                + "&endDate=" + URLEncoder.encode(endDate.toString(), StandardCharsets.UTF_8);
        return get(path, AnalyticsClientResponse.class);
    }
}