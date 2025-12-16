package com.bluemoonproject.dto;

import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.enums.Status;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplainUserViewDTO {
    private Long id;
    private String description;
    private String response;
    private LocalDate createdAt;
    private String attachment;
    private ComplainTopic type;
    private Status status;
    private Set<Long> userIds;
}

