package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.ResidencyStatus;
import com.bluemoonproject.validator.DobConstraint;
import com.bluemoonproject.validator.PasswordConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @PasswordConstraint
    String password;

    String firstName;
    String lastName;
    String email;

    @DobConstraint(min=18,message = "INVALID_DOB")
    LocalDate dob;

    @Enumerated(EnumType.STRING)
    private ResidencyStatus residencyStatus;
}