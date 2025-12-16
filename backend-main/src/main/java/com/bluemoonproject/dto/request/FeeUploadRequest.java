package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.FeeStatus;
import com.bluemoonproject.enums.FeeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeeUploadRequest {

    @NotBlank(message = "DESCRIPTION_REQUIRED")
    @Size(max = 255, message = "DESCRIPTION_TOO_LONG")  // Nếu muốn giới hạn độ dài
    private String description;

    @NotNull(message = "DUE_DATE_REQUIRED")
    @FutureOrPresent(message = "DUE_DATE_MUST_BE_TODAY_OR_FUTURE")
    private LocalDate dueDate;

    @NotNull(message = "STATUS_REQUIRED")
    @Enumerated(EnumType.STRING)
    private FeeStatus status;

    @NotNull(message = "FEE_TYPE_REQUIRED")
    @Enumerated(EnumType.STRING)
    private FeeType feeType;
}
