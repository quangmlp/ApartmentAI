package com.bluemoonproject.controller;

import java.util.List;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Guest;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.ResidencyStatus;
import com.bluemoonproject.service.GuestService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bluemoonproject.dto.response.ApiResponse;
import com.bluemoonproject.dto.request.UserCreationRequest;
import com.bluemoonproject.dto.request.UserUpdateRequest;
import com.bluemoonproject.dto.response.UserResponse;
import com.bluemoonproject.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class UserController {
    UserService userService;
    GuestService guestService;

    //
    @PostMapping     //taoj người
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping ("/admin")   //lấy hết mọi người
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    //endpoint: .../users/ashwkhiu213isd : lấy toàn bộ thông tin về 1 người
    @GetMapping("/admin/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }
    // .../users/my-info :lấy thông tin của bản thân
    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    //....users/ằer43324w :xóa người
    @DeleteMapping("/admin/{userId}")
    ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    //...users/ạiuqi190s09a/activate: make guest to user
    @PostMapping("/admin/activate")
    public ResponseEntity<User> activateUser(
            @RequestParam String email) {
        return ResponseEntity.ok(userService.activateAccount(email));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Fee>> getUnpaidFeesForUser() {
        List<Fee> unpaidFees = userService.getUnpaidFeesForUser();
        return ResponseEntity.ok(unpaidFees);
    }


    @PostMapping("/admin/{id}/make-admin")
    public ResponseEntity<String> makeUserAdmin(@PathVariable Long id) {
        userService.makeUserAdmin(id);
        return ResponseEntity.ok("User role updated to ADMIN");
    }

    @PostMapping("/create")
    public ResponseEntity<Guest> createAccount(@RequestBody @Valid Guest guest) {
        Guest newGuest = guestService.createAccount(guest.getUsername(), guest.getPassword(), guest.getFirstName(),
                guest.getLastName(), guest.getDob(), guest.getEmail());
        return ResponseEntity.ok(newGuest);
    }

    @GetMapping("/room")
    public ResponseEntity<List<Room>> getRoomsForUser( ) {

        List<Room> rooms = userService.getRoomsForUser();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/admin/guest")
    public ResponseEntity<List<Guest>> getGuestsForUser( ) {
        List<Guest> guests=guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/admin/user/{residencyStatus}")
    public ApiResponse<List<UserResponse>> getUsersByResidencyStatus(@PathVariable ResidencyStatus residencyStatus) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.findUserByResidencyStatus(residencyStatus))
                .build();
    }
}