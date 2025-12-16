package com.bluemoonproject.dto.response;

import com.bluemoonproject.enums.ResidencyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    LocalDate dob;
    //
    Set<RoleResponse> roles;

    ResidencyStatus residencyStatus;

}
