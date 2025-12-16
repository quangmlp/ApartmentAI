package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.ResidencyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class SearchUserCriteriaDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private ResidencyStatus residencyStatus;
}