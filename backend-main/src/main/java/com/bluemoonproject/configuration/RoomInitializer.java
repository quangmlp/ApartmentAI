package com.bluemoonproject.configuration;

import com.bluemoonproject.entity.Room;
import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import com.bluemoonproject.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RoomInitializer {

    private final RoomRepository roomRepository;

    @PostConstruct
    public void initRooms() {
        // chỉ khởi tạo khi bảng còn trống
        if (roomRepository.count() > 0) return;

        List<Room> rooms = new ArrayList<>();

        // Kiot tầng 1
        for (int i = 1; i <= 6; i++) {
            String roomNumber = String.format("K1%02d", i); // K101
            rooms.add(defaultRoom(roomNumber, 1, 30.0, RoomType.KIOT));
        }

        // Căn hộ tầng 5–28
        for (int floor = 5; floor <= 28; floor++) {
            for (int i = 1; i <= 6; i++) {
                String roomNumber = String.format("%02d%02d", floor, i); // 0501
                rooms.add(defaultRoom(roomNumber, floor, 75.0, RoomType.STANDARD));
            }
        }

        // Penthouse tầng 29
        for (int i = 1; i <= 2; i++) {
            String roomNumber = String.format("P29%02d", i);
            rooms.add(defaultRoom(roomNumber, 29, 120.0, RoomType.PENHOUSE));
        }

        roomRepository.saveAll(rooms);
    }

    private Room defaultRoom(String roomNumber, int floor, double area, RoomType type) {
        return Room.builder()
                .roomNumber(roomNumber)
                .floor(floor)
                .area(area)
                .roomType(type)
                .status(RoomStatus.VACANT)
                .peopleCount(0L)
                .residentCount(0L)
                .userIds(new HashSet<>())
                .feeIds(new HashSet<>())
                .residents(new HashSet<>())
                .build();
    }
}