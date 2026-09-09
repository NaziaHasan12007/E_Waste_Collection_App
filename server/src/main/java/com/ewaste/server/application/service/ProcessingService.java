package com.ewaste.server.application.service;

import com.ewaste.server.common.exception.ResourceNotFoundException;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.repository.ProcessingRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessingService {

    private final ProcessingRecordRepository processingRecordRepository;

    public ProcessingService(ProcessingRecordRepository processingRecordRepository) {
        this.processingRecordRepository = processingRecordRepository;
    }

    public ProcessingRecord saveRecord(ProcessingRecord record) {
        return processingRecordRepository.save(record);
    }

    public ProcessingRecord findById(Long recordId) {
        return processingRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("ProcessingRecord", recordId));
    }

    public List<ProcessingRecord> findByCenterId(Long centerId) {
        return processingRecordRepository.findByCenterId(centerId);
    }

    public List<ProcessingRecord> findAll() {
        return processingRecordRepository.findAll();
    }
}