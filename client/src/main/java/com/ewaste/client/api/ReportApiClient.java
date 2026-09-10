package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.dto.response.AnalyticsClientResponse;

public class ReportApiClient extends ApiClient {

    private static ReportApiClient instance;

    private ReportApiClient() {
        super();
    }

    public static ReportApiClient getInstance() {
        if (instance == null) {
            instance = new ReportApiClient();
        }
        return instance;
    }

    public AnalyticsClientResponse getReportsSummary() {
        String endpoint = ApiConfig.getInstance().getReportsSummaryEndpoint();
        return get(endpoint, AnalyticsClientResponse.class);
    }

    public AnalyticsClientResponse getPickupsReport() {
        String endpoint = ApiConfig.getInstance().getReportsPickupsEndpoint();
        return get(endpoint, AnalyticsClientResponse.class);
    }

    public AnalyticsClientResponse getProcessingReport() {
        String endpoint = ApiConfig.getInstance().getReportsProcessingEndpoint();
        return get(endpoint, AnalyticsClientResponse.class);
    }
}