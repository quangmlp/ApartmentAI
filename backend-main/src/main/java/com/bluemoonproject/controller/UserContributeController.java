package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Contribution;
import com.bluemoonproject.entity.ContributionRecord;
import com.bluemoonproject.service.ContributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contribute")
public class UserContributeController {
    private final ContributionService contributionService;

    public UserContributeController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<Contribution>> getActive() {
        return ResponseEntity.ok(contributionService.getActiveContributions());
    }

    @PostMapping("/{id}/contribute")
    public ResponseEntity<ContributionRecord> contribute(
            @PathVariable Long id,
            @RequestParam Double amount
    ) {
        if (amount == null || amount < 1000) {
            throw new IllegalArgumentException("Amount must be greater than 1000");
        }

        return ResponseEntity.ok(contributionService.contribute(id, amount));
    }

    @GetMapping("/my-records")
    public ResponseEntity<List<ContributionRecord>> getMyContributionRecords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ContributionRecord> records = contributionService.getMyContributionRecords(username);
        return ResponseEntity.ok(records);
    }

}
