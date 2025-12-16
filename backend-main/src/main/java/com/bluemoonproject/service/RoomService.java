package com.bluemoonproject.service;


import com.bluemoonproject.dto.request.ResidentRequest;
import com.bluemoonproject.dto.request.UpdateResidentRequest;
import com.bluemoonproject.dto.response.ResidentInfoResponse;
import com.bluemoonproject.dto.response.RoomResponse;
import com.bluemoonproject.entity.ResidentInfo;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.mapper.RoomMapper;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMapper roomMapper;


    //
    @Transactional
    public Room addUserToRoom(String roomNumber, String userName) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        room.getUserIds().add(user.getId());
        room.setPeopleCount((long) room.getUserIds().size());

        return roomRepository.save(room);
    }
//

    @Transactional
    public List<String> getAllUsernamesInRoom(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return room.getUserIds().stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(User::getUsername)
                .collect(Collectors.toList());
    }



    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<RoomResponse> filterRooms(RoomStatus status, RoomType type) {
        List<Room> rooms;

        if (status != null && type != null) {
            rooms = roomRepository.findByStatusAndRoomType(status, type);
        } else if (status != null) {
            rooms = roomRepository.findByStatus(status);
        } else if (type != null) {
            rooms = roomRepository.findByRoomType(type);
        } else {
            rooms = roomRepository.findAll();
        }

        return rooms.stream().map(roomMapper::toRoomResponse).toList();
    }

    @Transactional
    public void removeUserFromRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getUserIds().contains(userId)) {
            throw new AppException(ErrorCode.USER_NOT_IN_ROOM);
        }

        room.getUserIds().remove(userId);
        room.setPeopleCount((long) room.getUserIds().size());
        roomRepository.save(room);
    }


    public RoomResponse getRoomResponseById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return roomMapper.toRoomResponse(room);
    }


//    public RoomResponse updateRoom(Long id, RoomRequest roomRequest) {
//        Room room = roomRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));
//
//        roomMapper.updateRoomFromRequest(roomRequest, room); // Update fields
//        room = roomRepository.save(room); // Save updated entity
//
//        return roomMapper.toRoomResponse(room); // Return updated response
//    }

    // Thêm residents vào trong room
    public void addResidentToRoom(Long roomId, ResidentRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        boolean exists = room.getResidents().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(request.getName())
                        && r.getAge().equals(request.getAge()));

        if (exists) {
            throw new AppException(ErrorCode.DUPLICATE_RESIDENT);
        }

        ResidentInfo newResident = new ResidentInfo(request.getName(), request.getAge());
        room.getResidents().add(newResident);
        room.setResidentCount((long)room.getResidents().size());
        RoomStatus status = room.getResidents().size() > 0 ? RoomStatus.OCCUPIED : RoomStatus.VACANT;
        room.setStatus(status);
        roomRepository.save(room);
    }


    // Lấy tất cả Resident trong room
    public List<ResidentInfoResponse> getResidentsByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return room.getResidents().stream()
                .map(resident -> new ResidentInfoResponse(resident.getName(), resident.getAge()))
                .toList();
    }

    // update resident
    public void updateResidentInRoom(Long roomId, UpdateResidentRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Set<ResidentInfo> residents = room.getResidents();

        // Tìm cư dân cần cập nhật
        Optional<ResidentInfo> target = residents.stream()
                .filter(r -> r.getName().equalsIgnoreCase(request.getOldName()) && r.getAge().equals(request.getOldAge()))
                .findFirst();

        if (target.isEmpty()) {
            throw new AppException(ErrorCode.RESIDENT_NOT_FOUND);
        }

        // Check cư dân mới đã tồn tại chưa
        boolean exists = residents.stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(request.getNewName()));
        if (exists) {
            throw new AppException(ErrorCode.DUPLICATE_RESIDENT);
        }

        // Cập nhật bằng cách: xóa
        residents.remove(target.get());

        ResidentInfo updatedResident = new ResidentInfo(request.getNewName(), request.getNewAge());
        residents.add(updatedResident);

        room.setResidents(residents);
        roomRepository.save(room);
    }

    // delete resident
    public void deleteResidentFromRoom(Long roomId, ResidentRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Set<ResidentInfo> residents = room.getResidents();

        boolean removed = residents.removeIf(r ->
                r.getName().equalsIgnoreCase(request.getName()) && r.getAge().equals(request.getAge())
        );

        if (!removed) {
            throw new AppException(ErrorCode.RESIDENT_NOT_FOUND);
        }

        room.setResidents(residents);
        room.setResidentCount((long)room.getResidents().size());
        RoomStatus status = room.getResidents().size() > 0 ? RoomStatus.OCCUPIED : RoomStatus.VACANT;
        room.setStatus(status);
        roomRepository.save(room);
    }
    public void deleteRoomById(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
        roomRepository.deleteById(roomId);
    }

}