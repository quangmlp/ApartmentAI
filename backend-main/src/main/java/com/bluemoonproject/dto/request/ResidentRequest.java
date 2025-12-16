package com.bluemoonproject.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentRequest {

    @NotBlank(message = "Name must not be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be greater than or equal to 0")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private Integer age;

    // getters, setters
}