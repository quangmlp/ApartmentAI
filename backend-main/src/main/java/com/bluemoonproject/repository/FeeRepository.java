package com.bluemoonproject.repository;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Permission;
import com.bluemoonproject.enums.FeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByDueDateBeforeAndStatusNot(LocalDate today, FeeStatus feeStatus);

    List<Fee> findByRoomNumber(String roomNumber);

    @Query("SELECT f FROM Fee f WHERE "
            + "(f.roomNumber LIKE CONCAT('%', :roomNumber, '%') OR :roomNumber IS NULL) AND "
            + "(f.description LIKE CONCAT('%', :description, '%') OR :description IS NULL) AND "
            + "(f.amount >= :minAmount OR :minAmount IS NULL) AND "
            + "(f.amount <= :maxAmount OR :maxAmount IS NULL) AND "
            + "(f.dueDate = :dueDate OR :dueDate IS NULL) AND "
            + "(f.status = :status OR :status IS NULL)")
    Page<Fee> findFeesBySearchCriteria(
            String roomNumber,
            String description,
            Double minAmount,
            Double maxAmount,
            LocalDate dueDate,
            FeeStatus status,
            Pageable pageable
    );
}//
