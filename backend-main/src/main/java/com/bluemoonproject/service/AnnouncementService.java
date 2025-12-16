package com.bluemoonproject.service;

import com.bluemoonproject.entity.Announcement;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.AnnounceType;
import com.bluemoonproject.enums.PredefinedRole;
import com.bluemoonproject.repository.AnnouncementRepository;
import com.bluemoonproject.repository.GuestRepository;
import com.bluemoonproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    public void sendAnnouncementToAllUsers(Announcement announcement) {
        if (announcement.getDescription() == null) {
            throw new IllegalArgumentException("Announcement description cannot be null");
        }

        List<User> allUsers = userRepository.findAll();
        sendEmails(allUsers, announcement.getDescription(), announcement.getType());
    }

    public void sendAnnouncementToSpecificUsers(String description, AnnounceType type, List<Long> userIds) {
        if (description == null) {
            throw new IllegalArgumentException("Announcement description cannot be null");
        }

        // Fetch users by their IDs
        List<User> targetUsers = userRepository.findAllById(userIds);

        // Ensure that the users list is not empty
        if (targetUsers.isEmpty()) {
            throw new IllegalArgumentException("No users found with the provided IDs.");
        }
        log.info("Here");
        // Send emails to the users
        sendEmails(targetUsers, description, type);
    }


    private void sendEmails(List<User> users, String messageBody, AnnounceType type) {
        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                if(type==AnnounceType.INFORMATION)
                    message.setSubject("📢 New Announcement: News");
                else message.setSubject("📢 WARNING!");
                message.setText(messageBody);
                mailSender.send(message);
            }
        }
    }
    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        if (announcement.getDescription() == null) {
            throw new IllegalArgumentException("Announcement description cannot be null");
        }
        return announcementRepository.save(announcement);
    }

    // Delete an announcement by ID
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Optional<Announcement> announcement = announcementRepository.findById(announcementId);
        if (announcement.isPresent()) {
            announcementRepository.delete(announcement.get());
        } else {
            throw new IllegalArgumentException("Announcement with the given ID does not exist");
        }
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    @Scheduled(cron = "0 0 0 * * ?")  // This schedules the task to run daily at midnight
    public void generateDailyAnnouncements() {
        // Check if there are any guests in the repository
        long guestCount = guestRepository.count(); // This will return the number of guests in the system
        if (guestCount == 0) {
            System.out.println("No guests found. Skipping announcement generation.");
            return;  // If no guests, skip announcement creation
        }

        // If there are guests, proceed to generate announcements for admin users
        List<User> admins = userRepository.findByRoles_Name(PredefinedRole.ADMIN_ROLE);
        for (User admin : admins) {
            Announcement announcement = new Announcement();
            announcement.setDescription("Có người đang đợi xác thực, hãy kiểm tra!");
            announcement.setType(AnnounceType.INFORMATION);  // Or set as per your logic
            announcementRepository.save(announcement);
        }
    }
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }
}
