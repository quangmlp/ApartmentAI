package com.bluemoonproject.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeeUpdateRequest {

    @NotBlank(message = "ROOM_NOT_FOUND") // hoặc tạo ErrorCode riêng nếu cần
    String roomNumber;

    @NotBlank(message = "DESCRIPTION_REQUIRED")
    @Size(max = 255, message = "DESCRIPTION_TOO_LONG") // gợi ý giới hạn độ dài
    String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.01", message = "Amount must be greater than 1000")
    Double amount;

    @NotNull(message = "DUE_DATE_REQUIRED")
    @FutureOrPresent(message = "DUE_DATE_MUST_BE_TODAY_OR_FUTURE")
    LocalDate dueDate;
}
