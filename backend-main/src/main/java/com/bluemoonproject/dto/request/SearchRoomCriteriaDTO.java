package com.bluemoonproject.dto.request;

import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import lombok.Data;

@Data
public class SearchRoomCriteriaDTO {
    private String roomNumber;
    private Integer floor;
    private Long peopleCount;
    private Long residentCount;
    private Double area;
    private RoomType roomType;
    private RoomStatus status;
}
