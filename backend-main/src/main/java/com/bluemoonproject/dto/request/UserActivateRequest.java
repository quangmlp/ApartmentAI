package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.ResidencyStatus;
import com.bluemoonproject.validator.DobConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActivateRequest {
    String password;
    String firstName;
    String lastName;
    //
    @DobConstraint(min=18,message = "INVALID_DOB")
    LocalDate dob;

    Set<String> roles = new HashSet<>(List.of("USER"));

    @Enumerated(EnumType.STRING)
    private ResidencyStatus residencyStatus;
}