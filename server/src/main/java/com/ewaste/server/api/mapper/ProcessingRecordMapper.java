package com.ewaste.server.api.mapper;

import com.ewaste.server.api.dto.response.ProcessingOutcomeResponse;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import org.springframework.stereotype.Component;

@Component
public class ProcessingRecordMapper {

    public ProcessingOutcomeResponse toResponseDto(ProcessingRecord record) {
        if (record == null) {
            return null;
        }

        ProcessingOutcomeResponse dto = new ProcessingOutcomeResponse();
        dto.setRecordId(record.getRecordId());
        dto.setPickupId(record.getPickupId());
        dto.setItemId(record.getItemId());
        dto.setCenterId(record.getCenterId());
        dto.setWorkflowType(record.getProcessingResult() != null ? record.getProcessingResult().name() : null);
        dto.setProcessingResult(record.getProcessingResult() != null ? record.getProcessingResult().name() : null);
        dto.setPointsAwarded(record.getPointsAwarded() != null ? record.getPointsAwarded() : 0);
        dto.setProcessedAt(record.getProcessedAt() != null ? record.getProcessedAt().toString() : null);

        return dto;
    }
}