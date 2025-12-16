package com.bluemoonproject.service;

import com.bluemoonproject.entity.Contribution;
import com.bluemoonproject.entity.ContributionRecord;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.repository.ContributionRecordRepository;
import com.bluemoonproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.bluemoonproject.repository.ContributionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ContributionService {
    private final ContributionRepository contributionRepository;
    private final ContributionRecordRepository contributionRecordRepository;
    private final  UserRepository userRepository;
    public Contribution createContribution(Contribution contribution) {
        contribution.setActive(true);
        return contributionRepository.save(contribution);
    }

    public Contribution updateContribution(Long id, Contribution updatedContribution) {
        Contribution existing = contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        existing.setTitle(updatedContribution.getTitle());
        existing.setDescription(updatedContribution.getDescription());
        existing.setStartDate(updatedContribution.getStartDate());
        existing.setEndDate(updatedContribution.getEndDate());
        existing.setActive(updatedContribution.isActive());

        return contributionRepository.save(existing);
    }

    public void deleteContribution(Long id) {
        contributionRepository.deleteById(id);
    }

    public List<Contribution> getAllContributions() {
        return contributionRepository.findAll();
    }

    public List<Contribution> getActiveContributions() {
        return contributionRepository.findAll()
                .stream()
                .filter(Contribution::isActive)
                .toList();
    }

    public ContributionRecord contribute(Long contributionId, Double amount) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new RuntimeException("Contribution not found"));

        if (!contribution.isActive()) {
            throw new RuntimeException("This contribution is no longer active.");
        }
        var context= SecurityContextHolder.getContext();
        String name=context.getAuthentication().getName();


        User user=userRepository.findByUsername(name).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTS));
        Long userId=user.getId();
        ContributionRecord record = ContributionRecord.builder()
                .contribution(contribution)
                .userId(userId)
                .amount(amount)
                .contributedAt(LocalDateTime.now())
                .approved(false)
                .build();

        return contributionRecordRepository.save(record);
    }

    public List<ContributionRecord> getRecordsByContribution(Long contributionId) {
        return contributionRecordRepository.findByContributionId(contributionId);
    }
    public Contribution getContributionById(Long id) {
        return contributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contribution not found with ID: " + id));
    }

    public List<ContributionRecord> getRecordsByResident(Long userId) {
        return contributionRecordRepository.findByUserId(userId);
    }

    public List<ContributionRecord> getMyContributionRecords(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return contributionRecordRepository.findByUserId(user.getId());
    }
}
