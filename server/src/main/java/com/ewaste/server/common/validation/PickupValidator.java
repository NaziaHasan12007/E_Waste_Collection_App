package com.ewaste.server.common.validation;

import com.ewaste.server.api.dto.request.CreatePickupRequestDto;
import com.ewaste.server.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class PickupValidator {

    public void validateCreateRequest(CreatePickupRequestDto dto) {
        if (dto == null) {
            throw new BusinessRuleException("Pickup request payload cannot be null.");
        }

        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new BusinessRuleException("A valid customer user ID is required.");
        }

        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new BusinessRuleException("Pickup address cannot be empty.");
        }

        if (dto.getAddress().trim().length() < 5) {
            throw new BusinessRuleException("Pickup address must be at least 5 characters long.");
        }

        if (dto.getItemIds() == null || dto.getItemIds().isEmpty()) {
            throw new BusinessRuleException("At least one registered e-waste item must be attached to the pickup request.");
        }

        if (dto.getPreferredDate() != null && !dto.getPreferredDate().trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dto.getPreferredDate().trim());
                if (date.isBefore(LocalDate.now())) {
                    throw new BusinessRuleException("Preferred pickup date cannot be in the past.");
                }
            } catch (DateTimeParseException ex) {
                throw new BusinessRuleException("Preferred pickup date must follow ISO format (YYYY-MM-DD).");
            }
        }
    }
}