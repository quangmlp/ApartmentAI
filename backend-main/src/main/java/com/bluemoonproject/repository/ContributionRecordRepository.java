package com.bluemoonproject.repository;

import com.bluemoonproject.entity.ContributionRecord;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContributionRecordRepository extends JpaRepository<ContributionRecord, Long> {
    List<ContributionRecord> findByContributionId(Long contributionId);

    List<ContributionRecord> findByUserId(Long userId);

    @Query("SELECT c FROM ContributionRecord c WHERE " +
            "(:id IS NULL OR c.id = :id) AND " +
            "(:contributionId IS NULL OR c.contribution.id = :contributionId) AND " +
            "(:userId IS NULL OR c.userId = :userId) AND " +
            "(:minAmount IS NULL OR c.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR c.amount <= :maxAmount) AND " +
            "(:fromDate IS NULL OR c.contributedAt >= :fromDate) AND " +
            "(:toDate IS NULL OR c.contributedAt <= :toDate) AND " +
            "(:approved IS NULL OR c.approved = :approved)")
    Page<ContributionRecord> findContributionRecordsBySearchParams(
            @Param("id") Long id,
            @Param("contributionId") Long contributionId,
            @Param("userId") Long userId,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("approved") Boolean approved,
            Pageable pageable
    );

}