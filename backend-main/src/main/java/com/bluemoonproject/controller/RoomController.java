package com.bluemoonproject.controller;

import com.bluemoonproject.dto.request.*;
import com.bluemoonproject.dto.response.ApiResponse;
import com.bluemoonproject.dto.response.ResidentInfoResponse;
import com.bluemoonproject.dto.response.RoomResponse;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/room")
public class RoomController {
    private final RoomService roomService;
    private final RoomRepository roomRepository;

    @PostMapping("/{roomNumber}/users/{userName}")
    public ResponseEntity<Room> addUserToRoom(
            @PathVariable String roomNumber,
            @PathVariable String userName) {
        try {
            Room updatedRoom = roomService.addUserToRoom(roomNumber, userName);
            return ResponseEntity.ok(updatedRoom);
        } catch (AppException e) {
            return ResponseEntity.status(e.getErrorCode().getStatusCode())
                    .body(null);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsernamesInRoom(@RequestParam String roomNumber) {
        List<String> usernames = roomService.getAllUsernamesInRoom(roomNumber);
        return ResponseEntity.ok(usernames);
    }

    @GetMapping
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomService.getAllRooms(pageable);
    }

    // lọc các room theo roomStatus, RoomType
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByFilter(
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) RoomType type
    ) {
        return ResponseEntity.ok(roomService.filterRooms(status, type));
    }


    @DeleteMapping("/{roomId}/users/{userId}")
    public ResponseEntity<String> removeUserFromRoom(@PathVariable Long roomId, @PathVariable Long userId) {
        roomService.removeUserFromRoom(roomId, userId);
        return ResponseEntity.ok("User removed from room successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomResponseById(id));
    }

    // Không cần update room vì khi thêm người hoặc xóa người thì room tự động cập nhật
//    @PutMapping("/{id}")
//    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @RequestBody RoomRequest roomRequest) {
//        return ResponseEntity.ok(roomService.updateRoom(id, roomRequest));
//    }

    // Thêm residents vào trong phòng
    @PostMapping("/{roomId}/residents")
    public ApiResponse<String> addResidentToRoom(@PathVariable Long roomId, @RequestBody @Valid ResidentRequest request) {
        roomService.addResidentToRoom(roomId, request);
        return ApiResponse.<String>builder()
                .result("Resident added successfully")
                .build();
    }

    // lẤY tất cả residents trong phòng
    @GetMapping("/{roomId}/residents")
    public ApiResponse<List<ResidentInfoResponse>> getResidents(@PathVariable Long roomId) {
        return ApiResponse.<List<ResidentInfoResponse>>builder()
                .result(roomService.getResidentsByRoomId(roomId))
                .build();
    }

    // update resident
    @PutMapping("/{roomId}/residents")
    public ApiResponse<String> updateResidentInRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateResidentRequest request
    ) {
        roomService.updateResidentInRoom(roomId, request);
        return ApiResponse.<String>builder()
                .result("Resident updated successfully")
                .build();
    }

    // delete resident
    @DeleteMapping("/{roomId}/residents")
    public ApiResponse<String> deleteResidentFromRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid ResidentRequest request
    ) {
        roomService.deleteResidentFromRoom(roomId, request);
        return ApiResponse.<String>builder()
                .result("Resident deleted successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

}