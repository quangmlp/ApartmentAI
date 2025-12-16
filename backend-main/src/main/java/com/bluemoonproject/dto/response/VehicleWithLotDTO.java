package com.bluemoonproject.dto.response;

import com.bluemoonproject.entity.Vehicle;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleWithLotDTO {
    private Long id;
    private String licensePlate;
    private Vehicle.Type type;
    private String lotNumber; // null if not assigned

    public VehicleWithLotDTO(Long id, String licensePlate, Vehicle.Type type, String lotNumber) {
        this.id=id;
        this.licensePlate = licensePlate;
        this.type = type;
        this.lotNumber = lotNumber;
    }

    // Getters & setters
}
