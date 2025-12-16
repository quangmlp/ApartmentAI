package com.bluemoonproject.service;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.Vehicle;
import com.bluemoonproject.enums.FeeStatus;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingFeeService {

    private final FeeRepository feeRepository;
    private final RoomRepository roomRepository;
    private final VehicleRepository vehicleRepository;

    @Scheduled(cron = "0 0 22 30 * *")
    public void autoGenerateMonthlyParkingFees() {
        generateMonthlyParkingFees(LocalDate.now());
    }


    public void generateMonthlyParkingFees(LocalDate month) {
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            if (room.getVehicleIds().isEmpty()) continue;

            List<Vehicle> vehicles = vehicleRepository.findAllByIdIn(room.getVehicleIds());
            long bikes = vehicles.stream().filter(v -> v.getType() == Vehicle.Type.MOTORBIKE).count();
            long cars = vehicles.stream().filter(v -> v.getType() == Vehicle.Type.CAR).count();

            double amount = bikes * 70000 + cars*1200000;


            if (amount > 0) {
                Fee fee = Fee.builder()
                        .roomNumber(room.getRoomNumber())
                        .description("Monthly parking fee")
                        .amount(amount)
                        .dueDate(month.withDayOfMonth(10)) // 10th of the month
                        .createdAt(LocalDateTime.now())
                        .status(FeeStatus.UNPAID)
                        .build();

                feeRepository.save(fee);
            }
        }
    }
}
