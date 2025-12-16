package com.bluemoonproject.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateResidentRequest {

    private String oldName;
    private Integer oldAge;

    @NotBlank(message = "New name must not be blank")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "New name must contain only letters and spaces")
    private String newName;

    @NotNull(message = "New age is required")
    @Min(value = 0, message = "New age must be greater than or equal to 0")
    @Max(value = 120, message = "New age must be less than or equal to 120")
    private Integer newAge;

    // getters, setters
}