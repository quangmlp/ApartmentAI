package com.bluemoonproject.service;

import com.bluemoonproject.dto.response.VehicleWithLotDTO;
import com.bluemoonproject.entity.*;
import com.bluemoonproject.enums.FeeStatus;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final VehicleRepository vehicleRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final FeeRepository feeRepository;
    public ParkingLot assignVehicleToSpecificLot(Long vehicleId, Long parkingLotId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        ParkingLot lot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found"));

        // Ensure the lot is not already occupied
        if (lot.isOccupied()) {
            throw new IllegalStateException("Parking lot is already occupied");
        }

        // Ensure the vehicle is not already assigned
        boolean alreadyAssigned = parkingLotRepository.findAll().stream()
                .anyMatch(l -> l.getVehicleIds().contains(vehicleId));
        if (alreadyAssigned) {
            throw new IllegalStateException("Vehicle is already assigned to a parking lot");
        }

        // Check type compatibility
        if (!lot.getType().equals(vehicle.getType())) {
            throw new IllegalStateException("Vehicle type does not match parking lot type");
        }

        // Assign the vehicle and mark the lot as occupied
        lot.getVehicleIds().add(vehicleId);
        lot.setOccupied(true);
        ParkingLot updatedLot = parkingLotRepository.save(lot);

        // Find the room that owns this vehicle
        Room room = roomRepository.findAll().stream()
                .filter(r -> r.getVehicleIds().contains(vehicleId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // Calculate and create fee
        List<Vehicle> vehicles = vehicleRepository.findAllByIdIn(room.getVehicleIds());
        long bikes = vehicles.stream().filter(v -> v.getType() == Vehicle.Type.MOTORBIKE).count();
        long cars = vehicles.stream().filter(v -> v.getType() == Vehicle.Type.CAR).count();

        double amount = bikes * 70000 + cars * 1200000;

        if (amount > 0) {
            Fee fee = Fee.builder()
                    .roomNumber(room.getRoomNumber())
                    .description("Monthly parking fee")
                    .amount(amount)
                    .dueDate(LocalDate.now().withDayOfMonth(10)) // due on the 10th of this month
                    .createdAt(LocalDateTime.now())
                    .status(FeeStatus.UNPAID)
                    .build();

            feeRepository.save(fee);
        }

        return updatedLot;
    }

    public ParkingLot unassignVehicleFromLot(Long vehicleId) {
        // Find the parking lot where this vehicle is assigned
        Optional<ParkingLot> optionalLot = parkingLotRepository.findAll().stream()
                .filter(lot -> lot.getVehicleIds().contains(vehicleId))
                .findFirst();

        if (optionalLot.isEmpty()) {
            throw new IllegalStateException("Vehicle is not assigned to any parking lot");
        }

        ParkingLot lot = optionalLot.get();

        // Remove the vehicle and mark the lot as unoccupied
        lot.getVehicleIds().remove(vehicleId);
        lot.setOccupied(false);

        boolean inRoom = roomRepository.findAll().stream()
                .anyMatch(room -> room.getVehicleIds().contains(vehicleId));

        if (!inRoom) {
            vehicleRepository.deleteById(vehicleId);
        }

        return parkingLotRepository.save(lot);
    }
    public ParkingLot unassignVehicleFromLotForCurrentUser(Long vehicleId) {
        // Step 1: Get current username (adjust if using JWT or custom auth)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Step 2: Find user and their room
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Room> rooms = roomRepository.findRoomsByUserId(user.getId());
        if (rooms.isEmpty()) {
            throw new IllegalStateException("User is not assigned to any room");
        }
        // Step 3: Check if vehicle belongs to the user's room
        Room owningRoom = rooms.stream()
                .filter(r -> r.getVehicleIds().contains(vehicleId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("This vehicle does not belong to your room"));



        // Step 5: Unassign vehicle from parking lot
        Optional<ParkingLot> optionalLot = parkingLotRepository.findAll().stream()
                .filter(lot -> lot.getVehicleIds().contains(vehicleId))
                .findFirst();

        if (optionalLot.isEmpty()) {
            throw new IllegalStateException("Vehicle is not assigned to any parking lot");
        }

        ParkingLot lot = optionalLot.get();
        lot.getVehicleIds().remove(vehicleId);
        lot.setOccupied(false);
        parkingLotRepository.save(lot);



        return lot;
    }
    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public List<Vehicle> getVehiclesOfLot(Long parkingLotId) {
        ParkingLot lot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        return vehicleRepository.findByIdIn(lot.getVehicleIds());
    }

    public List<VehicleWithLotDTO> getVehiclesWithLotsByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        Set<Long> vehicleIds = room.getVehicleIds();
        if (vehicleIds.isEmpty()) return Collections.emptyList();

        List<Vehicle> vehicles = vehicleRepository.findByIdIn(vehicleIds);
        List<ParkingLot> lots = parkingLotRepository.findByVehicleIds(vehicleIds);

        Map<Long, String> vehicleToLotMap = new HashMap<>();
        for (ParkingLot lot : lots) {
            for (Long vId : lot.getVehicleIds()) {
                vehicleToLotMap.put(vId, lot.getLotNumber());
            }
        }

        return vehicles.stream()
                .map(v -> new VehicleWithLotDTO(
                        v.getId(),
                        v.getLicensePlate(),
                        v.getType(),
                        vehicleToLotMap.getOrDefault(v.getId(), null)))
                .collect(Collectors.toList());
    }
}
