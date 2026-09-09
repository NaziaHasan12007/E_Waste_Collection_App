package com.ewaste.server.application.service;

import com.ewaste.server.common.exception.ResourceNotFoundException;
import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.repository.CollectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectorService {

    private final CollectorRepository collectorRepository;

    public CollectorService(CollectorRepository collectorRepository) {
        this.collectorRepository = collectorRepository;
    }

    public Collector findById(Long collectorId) {
        return collectorRepository.findById(collectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector", collectorId));
    }

    public Collector findByUserId(Long userId) {
        return collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector with User ID", userId));
    }

    public List<Collector> findAll() {
        return collectorRepository.findAll();
    }

    public List<Collector> findAvailable() {
        return collectorRepository.findByAvailability(true);
    }

    public void setAvailability(Long collectorId, boolean available) {
        Collector collector = findById(collectorId);
        collector.setAvailable(available);
        collectorRepository.update(collector);
    }

    public Collector save(Collector collector) {
        return collectorRepository.save(collector);
    }
}