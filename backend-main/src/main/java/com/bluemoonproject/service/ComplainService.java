package com.bluemoonproject.service;

import com.bluemoonproject.dto.ComplainUserViewDTO;
import com.bluemoonproject.entity.Complain;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.enums.Status;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.repository.ComplainRepository;
import com.bluemoonproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ComplainService {

    private final ComplainRepository complainRepository;
    private final UserRepository userRepository; // Assume User entity and repository exists



    public Complain createComplain(String description, ComplainTopic type, MultipartFile attachment) throws IOException {



        Complain complain = new Complain();
        complain.setDescription(description);
        complain.setType(type);
        complain.setCreatedAt(LocalDate.now());
        complain.setStatus(Status.WAITING);


        var context= SecurityContextHolder.getContext();
        String name=context.getAuthentication().getName();

        User user=userRepository.findByUsername(name).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTS));

        complain.setUserIds(new HashSet<>(Collections.singletonList(user.getId())));

        if (attachment != null && !attachment.isEmpty()) {
            // Option 1: Save the file to the file system and save the path (Or Option 2: Store it as byte[] in DB)
            byte[] fileContent = attachment.getBytes();
            complain.setAttachment(Arrays.toString(fileContent)); // Example: Convert to Base64 for storage
        }
        return complainRepository.save(complain);
    }

    public Complain addUserToComplain(Long complainId, Long userId) {
        Complain complain = complainRepository.findById(complainId)
                .orElseThrow(() -> new RuntimeException("Complain not found"));

        // Check if the user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        complain.getUserIds().add(userId);
        return complainRepository.save(complain);
    }


    public List<ComplainUserViewDTO> getComplainsForUser() {
        var context= SecurityContextHolder.getContext();
        String name=context.getAuthentication().getName();

        User user=userRepository.findByUsername(name).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTS));
        Long userId=user.getId();
        return complainRepository.findAll().stream()
                .filter(c -> c.getUserIds().contains(userId))
                .map(c -> new ComplainUserViewDTO(
                        c.getId(),
                        c.getDescription(),
                        c.getResponse(),
                        c.getCreatedAt(),
                        c.getAttachment(),
                        c.getType(),
                        c.getStatus(),
                        c.getUserIds()
                ))
                .collect(Collectors.toList());
    }
}
