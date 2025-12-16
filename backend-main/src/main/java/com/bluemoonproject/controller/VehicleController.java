package com.bluemoonproject.controller;

import com.bluemoonproject.dto.response.VehicleWithLotDTO;
import com.bluemoonproject.entity.ParkingLot;
import com.bluemoonproject.entity.Vehicle;
import com.bluemoonproject.service.ParkingLotService;
import com.bluemoonproject.service.RoomService;
import com.bluemoonproject.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final ParkingLotService parkingLotService;

    @PostMapping("/room/{roomId}")
    public ResponseEntity<?> createVehicleForRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid Vehicle vehicle) {
        try {
            Vehicle created = vehicleService.createVehicleForRoom(roomId, vehicle);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{roomId}/vehicles-with-lots")
    public ResponseEntity<List<VehicleWithLotDTO>> getVehiclesWithLots(@PathVariable Long roomId) {
        return ResponseEntity.ok(parkingLotService.getVehiclesWithLotsByRoomId(roomId));
    }

    @PostMapping("/{lotId}/assign-vehicle/{vehicleId}")
    public ParkingLot assignVehicleToLot(@PathVariable Long lotId, @PathVariable Long vehicleId) {
        return parkingLotService.assignVehicleToSpecificLot(vehicleId, lotId);
    }

    @GetMapping
    public List<ParkingLot> getAllLots() {
        return parkingLotService.getAllParkingLots();
    }

    @DeleteMapping("/{vehicleId}/unassign")
    public ResponseEntity<String> unassignVehicle(@PathVariable Long vehicleId) {
        try {
            ParkingLot updatedLot = parkingLotService.unassignVehicleFromLotForCurrentUser(vehicleId);
            return ResponseEntity.ok("Vehicle unassigned from parking lot successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        parkingLotService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

