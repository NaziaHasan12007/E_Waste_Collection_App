package com.ewaste.client.config;

import com.ewaste.client.api.*;

public class ClientContext {

    private static ClientContext instance;
    private final AuthApiClient authApiClient;
    private final EWasteApiClient eWasteApiClient;
    private final PickupApiClient pickupApiClient;
    private final RewardApiClient rewardApiClient;
    private final CollectorApiClient collectorApiClient;
    private final ReportApiClient reportApiClient;
    private final ProcessingApiClient processingApiClient;

    private ClientContext() {
        this.authApiClient = AuthApiClient.getInstance();
        this.eWasteApiClient = EWasteApiClient.getInstance();
        this.pickupApiClient = PickupApiClient.getInstance();
        this.rewardApiClient = RewardApiClient.getInstance();
        this.collectorApiClient = CollectorApiClient.getInstance();
        this.reportApiClient = ReportApiClient.getInstance();
        this.processingApiClient = ProcessingApiClient.getInstance();
    }

    public static ClientContext getInstance() {
        if (instance == null) {
            instance = new ClientContext();
        }
        return instance;
    }

    public AuthApiClient getAuthApiClient() { return authApiClient; }
    public EWasteApiClient getEWasteApiClient() { return eWasteApiClient; }
    public PickupApiClient getPickupApiClient() { return pickupApiClient; }
    public RewardApiClient getRewardApiClient() { return rewardApiClient; }
    public CollectorApiClient getCollectorApiClient() { return collectorApiClient; }
    public ReportApiClient getReportApiClient() { return reportApiClient; }
    public ProcessingApiClient getProcessingApiClient() { return processingApiClient; }
}