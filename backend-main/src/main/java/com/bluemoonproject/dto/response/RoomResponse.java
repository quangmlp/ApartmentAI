package com.bluemoonproject.dto.response;


import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    private long id;
    private Integer floor;
    private String roomNumber;
    private Long peopleCount;
    private Long ResidentCount;
    // Diện tích căn hộ (m²)
    private Double area;
    // Loại căn hộ (KIOT, STANDARD, PENHOUSE)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}
