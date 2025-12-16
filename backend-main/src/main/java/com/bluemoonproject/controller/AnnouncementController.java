package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Announcement;
import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.AnnounceType;
import com.bluemoonproject.enums.FeeStatus;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.UserRepository;
import com.bluemoonproject.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final FeeRepository feeRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    // Endpoint to send announcement to all users
    @PostMapping("/sendToAll")
    public ResponseEntity<String> sendAnnouncementToAll(@RequestBody Announcement announcement) {
        try {
            announcementService.sendAnnouncementToAllUsers(announcement);
            return ResponseEntity.ok("Announcement sent to all users successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to send announcement: " + e.getMessage());
        }
    }

    // Endpoint to send announcement to specific users
    @PostMapping("/sendToSpecific")
    public ResponseEntity<String> sendAnnouncementToSpecificUsers(@RequestBody SendAnnouncementRequest request) {
        try {
            announcementService.sendAnnouncementToSpecificUsers(request.getDescription(),request.getType(), request.getUsers());
            return ResponseEntity.ok("Announcement sent to specified users successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to send announcement: " + e.getMessage());
        }
    }


    public static class SendAnnouncementRequest {
        private String description;
        private AnnounceType type;
        private List<Long> users;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public AnnounceType getType() {
            return type;
        }

        public void setType(AnnounceType type) {
            this.type = type;
        }

        public List<Long> getUsers() {
            return users;
        }

        public void setUsers(List<Long> users) {
            this.users = users;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        try {
            Announcement createdAnnouncement = announcementService.createAnnouncement(announcement);
            return ResponseEntity.ok(createdAnnouncement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint to delete an announcement by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok("Announcement deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to delete announcement: " + e.getMessage());
        }
    }
    @Scheduled(cron = "0 47 19 * * ?")
    public void sendFeeExpirationWarnings() {
        // Fetch all rooms
        List<Room> rooms = roomRepository.findAll();

        for (Room room : rooms) {
            // For each room, iterate through its fees
            for (Long feeId : room.getFeeIds()) {
                Optional<Fee> feeOptional = feeRepository.findById(feeId);
                if (feeOptional.isPresent()) {
                    Fee fee = feeOptional.get();
                    if (fee.getDueDate().isBefore(LocalDate.now()) && fee.getStatus() == FeeStatus.UNPAID) {
                        // If the fee is overdue and unpaid, send a warning email to all users in the room
                        sendFeeExpirationEmail(room, fee);
                    }
                }
            }
        }
    }

    // Helper method to send the email to the user using AnnouncementService
    private void sendFeeExpirationEmail(Room room, Fee fee) {
        Set<Long> userIds = room.getUserIds();
        List<User> users = userRepository.findAllById(userIds);

        // Loop through all users and send an email
        for (User user : users) {
            String subject = "Fee Payment Due Warning";
            String body = "Dear " + user.getUsername() + ",\n\n" +
                    "This is a reminder that the fee for room " + room.getRoomNumber() +
                    " is past due. The fee amount is " + fee.getAmount() + ". Please make the payment as soon as possible.\n\n" +
                    "Thank you.";
            announcementService.sendEmail(user.getEmail(), subject, body);
        }
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }

}

