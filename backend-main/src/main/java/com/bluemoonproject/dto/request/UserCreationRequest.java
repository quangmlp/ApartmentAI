package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.ResidencyStatus;
import com.bluemoonproject.validator.DobConstraint;
import com.bluemoonproject.validator.PasswordConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NonNull
    @NotNull
    @Size(min=3,message ="USERNAME_INVALID")
    String username;
    //
    @NonNull
    @NotNull
    @PasswordConstraint
    String password;

    @NonNull
    @NotNull
    String email;

    @NonNull
    @NotNull
    String firstName;

    @NonNull
    @NotNull
    String lastName;

    @NonNull
    @NotNull
    @DobConstraint(min=18,message = "INVALID_DOB")
    LocalDate dob;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private ResidencyStatus residencyStatus;

}