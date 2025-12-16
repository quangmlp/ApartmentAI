package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Complain;
import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.enums.Priority;
import com.bluemoonproject.enums.Status;
import com.bluemoonproject.service.AdminComplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/complains")
public class AdminComplainController {

    private final AdminComplainService adminComplainService;

    @Autowired
    public AdminComplainController(AdminComplainService adminComplainService) {
        this.adminComplainService = adminComplainService;
    }

    @PutMapping("/{complainId}/resolve")
    public ResponseEntity<Complain> resolveComplain(
            @PathVariable Long complainId,
            @RequestParam String response,
            @RequestParam Priority priority,
            @RequestParam Status status) {
        Complain complain = adminComplainService.resolveComplain(complainId, response, priority, status);
        return ResponseEntity.ok(complain);
    }

    @DeleteMapping("/{complainId}")
    public ResponseEntity<Void> deleteComplain(@PathVariable Long complainId) {
        adminComplainService.deleteComplain(complainId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Complain>> getAllComplains(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) ComplainTopic type
    ) {
        List<Complain> complains = adminComplainService.getAllComplains(status, priority, type);
        return ResponseEntity.ok(complains);
    }
}
