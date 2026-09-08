package com.ewaste.server.api.controller;

import com.ewaste.server.application.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Get system summary metrics
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportService.ReportMetrics> getSystemSummary() {
        return ResponseEntity.ok(reportService.getSystemSummary());
    }

    /**
     * Get pickup statistics by date range
     */
    @GetMapping("/pickups")
    public ResponseEntity<ReportService.PickupStatistics> getPickupStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(reportService.getPickupStatistics(startDate, endDate));
    }

    /**
     * Get collector performance report
     */
    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<ReportService.CollectorPerformanceReport> getCollectorPerformance(
            @PathVariable Long collectorId) {
        return ResponseEntity.ok(reportService.getCollectorPerformance(collectorId));
    }

    /**
     * Get processing records summary
     */
    @GetMapping("/processing")
    public ResponseEntity<ReportService.ProcessingSummary> getProcessingSummary() {
        return ResponseEntity.ok(reportService.getProcessingSummary());
    }

    /**
     * Get customer reward summary
     */
    @GetMapping("/rewards/customer/{customerId}")
    public ResponseEntity<ReportService.RewardSummary> getCustomerRewardSummary(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(reportService.getCustomerRewardSummary(customerId));
    }
}