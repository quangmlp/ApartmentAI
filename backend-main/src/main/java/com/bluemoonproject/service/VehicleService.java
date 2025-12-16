package com.bluemoonproject.service;

import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.Vehicle;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RoomRepository roomRepository;

    public Vehicle createVehicleForRoom(Long roomId, Vehicle vehicle) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Get all current vehicles by ID
        List<Vehicle> currentVehicles = vehicleRepository.findAllById(room.getVehicleIds());

        long motorbikeCount = currentVehicles.stream()
                .filter(v -> v.getType() == Vehicle.Type.MOTORBIKE)
                .count();

        long carCount = currentVehicles.stream()
                .filter(v -> v.getType() == Vehicle.Type.CAR)
                .count();

        // Enforce vehicle limits
        if (vehicle.getType() == Vehicle.Type.MOTORBIKE && motorbikeCount >= 4) {
            throw new IllegalStateException("Room already has 4 motorbikes registered");
        }

        if (vehicle.getType() == Vehicle.Type.CAR && carCount >= 2) {
            throw new IllegalStateException("Room already has 2 cars registered");
        }

        // Save new vehicle
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Add vehicle ID to room
        room.getVehicleIds().add(savedVehicle.getId());
        roomRepository.save(room);

        return savedVehicle;
    }
}
