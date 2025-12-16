package com.bluemoonproject.controller;

import com.bluemoonproject.dto.ComplainUserViewDTO;
import com.bluemoonproject.entity.Complain;
import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.service.ComplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/complains")
public class UserComplainController {

    private final ComplainService complainService;

    @Autowired
    public UserComplainController(ComplainService complainService) {
        this.complainService = complainService;
    }

    @PostMapping
    public ResponseEntity<Complain> createComplain(
            @RequestParam String description,
            @RequestParam ComplainTopic type,
            @RequestParam(required = false) MultipartFile attachment) throws IOException { // Optional attachment
        Complain complain = complainService.createComplain(description, type, attachment);
        return ResponseEntity.status(HttpStatus.CREATED).body(complain);
    }


    @PutMapping("/{complainId}/add-user")
    public ResponseEntity<Complain> addUserToComplain(@PathVariable Long complainId, @RequestParam Long userId) {
        Complain complain = complainService.addUserToComplain(complainId, userId);
        return ResponseEntity.ok(complain);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ComplainUserViewDTO>> getUserComplains() {
        List<ComplainUserViewDTO> complains = complainService.getComplainsForUser();
        return ResponseEntity.ok(complains);
    }
}
