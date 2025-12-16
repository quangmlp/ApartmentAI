package com.bluemoonproject.controller;

import com.bluemoonproject.entity.ParkingLot;
import com.bluemoonproject.entity.Vehicle;
import com.bluemoonproject.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    /**
     * Assign an available parking lot to a vehicle
     */


    @DeleteMapping("/unassign-vehicle/{vehicleId}")
    public ParkingLot unassignVehicle(@PathVariable Long vehicleId) {
        return parkingLotService.unassignVehicleFromLot(vehicleId);
    }
    @GetMapping("/{id}/vehicles")
    public List<Vehicle> getVehiclesOfLot(@PathVariable Long id) {
        return parkingLotService.getVehiclesOfLot(id);
    }




}
