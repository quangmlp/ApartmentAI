package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.FeeStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SearchFeeCriteriaDTO {

    private String roomNumber;
    private String description;
    private Double minAmount;
    private Double maxAmount;
    private LocalDate dueDate;
    private FeeStatus status;
}
