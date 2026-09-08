package com.ewaste.server.domain.pattern.template;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

import java.time.LocalDateTime;

/**
 * Template Method Pattern (Sections 3.4 &amp; 5.2). The step names and order
 * here follow the documentation exactly: {@code validateItem} -&gt;
 * {@code inspectCondition} -&gt; {@code determineProcessingOutcome} -&gt;
 * {@code calculateReward} -&gt; {@code persistRecord}.
 * <p>
 * {@code persistRecord} deliberately builds an in-memory
 * {@link ProcessingRecord} only, rather than writing to SQLite itself: per
 * Section 2.1.3 the domain layer has no dependency on database drivers, so
 * {@code ProcessingFacade} is responsible for handing the returned record
 * to the {@code processing_records} repository (and filling in the
 * pickup/center ids, which this template has no way to know).
 */
public abstract class AbstractProcessingTemplate {

    /** The template method - final so subclasses can't reorder the pipeline. */
    public final ProcessingRecord executeProcessing(EWasteItem item) {
        validateItem(item);
        if (!isEligible(item)) {
            throw new IllegalStateException("Item is not eligible for the " + getWorkflowName() + " workflow");
        }

        WasteCondition inspectedCondition = inspectCondition(item);
        ProcessingOutcome outcome = determineProcessingOutcome(item, inspectedCondition);
        double pointsAwarded = calculateReward(item, outcome);

        return persistRecord(item, outcome, pointsAwarded);
    }

    /** Checks item specifications before any processing begins. */
    protected void validateItem(EWasteItem item) {
        if (item == null) {
            throw new IllegalArgumentException("item is required");
        }
        if (item.getCategory() == null) {
            throw new IllegalStateException("Item " + item.getId() + " has no category assigned");
        }
        if (item.getWeightKg() <= 0) {
            throw new IllegalStateException("Item " + item.getId() + " has an invalid weight");
        }
    }

    /** Hook: whether this item belongs in this particular workflow. Default accepts everything. */
    protected boolean isEligible(EWasteItem item) {
        return true;
    }

    /** Verifies functional/physical degradation. Default trusts the reported condition as-is. */
    protected WasteCondition inspectCondition(EWasteItem item) {
        return item.getCondition();
    }

    /** Abstract step: allocates the recovery route (recycle/refurbish/repair/dispose) for this item. */
    protected abstract ProcessingOutcome determineProcessingOutcome(EWasteItem item, WasteCondition inspectedCondition);

    /** Computes recycling credits. Default delegates to the item; workflows may apply a bonus/penalty. */
    protected double calculateReward(EWasteItem item, ProcessingOutcome outcome) {
        return item.calculateRewardPoints();
    }

    /**
     * Builds the yield-log record for this run. Returns an in-memory value
     * object; actual persistence to {@code processing_records} happens in
     * ProcessingFacade, which also knows the pickup_id and center_id.
     */
    protected ProcessingRecord persistRecord(EWasteItem item, ProcessingOutcome outcome, double pointsAwarded) {
        return new ProcessingRecord(item.getId(), getWorkflowName(), outcome.getNotes(), pointsAwarded);
    }

    /** Display name used in logs/reports and stored as {@code processing_records.workflow_type}. */
    public abstract String getWorkflowName();

    /** Result of {@link #determineProcessingOutcome}: what happens to the item and why. */
    public static final class ProcessingOutcome {
        private final EWasteItem.ProcessingType type;
        private final String notes;

        public ProcessingOutcome(EWasteItem.ProcessingType type, String notes) {
            this.type = type;
            this.notes = notes;
        }

        public EWasteItem.ProcessingType getType() {
            return type;
        }

        public String getNotes() {
            return notes;
        }
    }

    /**
     * In-memory mirror of a {@code processing_records} row. {@code pickupId}
     * and {@code centerId} are left for the caller to fill in, since a
     * single item's processing template has no knowledge of the pickup or
     * recycling center it's associated with.
     */
    public static final class ProcessingRecord {
        private Long pickupId;
        private Long centerId;
        private final Long itemId;
        private final String workflowType;
        private final String notes;
        private final double pointsAwarded;
        private final LocalDateTime processedAt;

        public ProcessingRecord(Long itemId, String workflowType, String notes, double pointsAwarded) {
            this.itemId = itemId;
            this.workflowType = workflowType;
            this.notes = notes;
            this.pointsAwarded = pointsAwarded;
            this.processedAt = LocalDateTime.now();
        }

        public Long getPickupId() {
            return pickupId;
        }

        public void setPickupId(Long pickupId) {
            this.pickupId = pickupId;
        }

        public Long getCenterId() {
            return centerId;
        }

        public void setCenterId(Long centerId) {
            this.centerId = centerId;
        }

        public Long getItemId() {
            return itemId;
        }

        public String getWorkflowType() {
            return workflowType;
        }

        public String getNotes() {
            return notes;
        }

        public double getPointsAwarded() {
            return pointsAwarded;
        }

        public LocalDateTime getProcessedAt() {
            return processedAt;
        }

        @Override
        public String toString() {
            return "ProcessingRecord{itemId=" + itemId + ", workflowType='" + workflowType
                    + "', pointsAwarded=" + pointsAwarded + '}';
        }
    }
}
