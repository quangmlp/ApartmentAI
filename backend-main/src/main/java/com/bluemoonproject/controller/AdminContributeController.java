package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Contribution;
import com.bluemoonproject.entity.ContributionRecord;
import com.bluemoonproject.repository.ContributionRecordRepository;
import com.bluemoonproject.service.ContributionService;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/contribute")
public class AdminContributeController {
    private final ContributionService contributionService;
    private final ContributionRecordRepository contributionRecordRepository;

    public AdminContributeController(ContributionService contributionService, ContributionRecordRepository contributionRecordRepository) {
        this.contributionService = contributionService;
        this.contributionRecordRepository = contributionRecordRepository;
    }


    @PostMapping
    public ResponseEntity<Contribution> create(@Valid @RequestBody Contribution contribution) {
        return ResponseEntity.ok(contributionService.createContribution(contribution));
    }

    // Xem tất cả - Admin
    @GetMapping("/all")
    public ResponseEntity<List<Contribution>> getAll() {
        return ResponseEntity.ok(contributionService.getAllContributions());
    }

    // Xóa - Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contributionService.deleteContribution(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contribution> update(@PathVariable Long id,@Valid @RequestBody Contribution updated) {
        return ResponseEntity.ok(contributionService.updateContribution(id, updated));
    }

    @PutMapping("/records/{recordId}/approve")
    public ResponseEntity<ContributionRecord> approveRecord(@PathVariable Long recordId) {

        ContributionRecord record = contributionRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Contribution record not found"));

        record.setApproved(true);
        return ResponseEntity.ok(contributionRecordRepository.save(record));
    }

    @GetMapping("/records/pending")
    public ResponseEntity<List<ContributionRecord>> getPendingRecords() {
        return ResponseEntity.ok(
                contributionRecordRepository.findAll()
                        .stream()
                        .filter(r -> !r.isApproved())
                        .toList()
        );
    }
    @GetMapping("/{id}/records")
    public ResponseEntity<List<ContributionRecord>> getRecordsByContribution(@PathVariable Long id) {
        Contribution contribution = contributionService.getContributionById(id); // throws if not found

        List<ContributionRecord> records = contributionService.getRecordsByContribution(id);
        return ResponseEntity.ok(records);
    }

}
