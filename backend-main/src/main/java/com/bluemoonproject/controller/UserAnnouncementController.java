package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Announcement;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.UserRepository;
import com.bluemoonproject.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class UserAnnouncementController {

    private final AnnouncementService announcementService;
    private final FeeRepository feeRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(announcements);
    }
}
