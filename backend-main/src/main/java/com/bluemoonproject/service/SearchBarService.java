package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.SearchContributionRecordCriteriaDTO;
import com.bluemoonproject.dto.request.SearchFeeCriteriaDTO;
import com.bluemoonproject.entity.ContributionRecord;
import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.ResidencyStatus;
import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import com.bluemoonproject.repository.ContributionRecordRepository;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.repository.RoomRepository;
import com.bluemoonproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SearchBarService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> searchUsers(String username, String firstName, String lastName, String email, ResidencyStatus residencyStatus, Pageable pageable) {
        return userRepository.findUsersBySearchParams(username, firstName, lastName, email, residencyStatus, pageable);
    }

    @Autowired
    private RoomRepository roomRepository;

    public Page<Room> searchRooms(String roomNumber, Integer floor, Long peopleCount, Long residentCount,
                                  Double area, RoomType roomType, RoomStatus status, Pageable pageable) {
        return roomRepository.findRoomsBySearchParams(roomNumber, floor, peopleCount, residentCount, area, roomType, status, pageable);
    }

    // SearchFee
    @Autowired
    private FeeRepository feeRepository;

    public Page<Fee> searchFees(SearchFeeCriteriaDTO feeSearchCriteria, Pageable pageable) {
        return feeRepository.findFeesBySearchCriteria(
                feeSearchCriteria.getRoomNumber(),
                feeSearchCriteria.getDescription(),
                feeSearchCriteria.getMinAmount(),
                feeSearchCriteria.getMaxAmount(),
                feeSearchCriteria.getDueDate(),
                feeSearchCriteria.getStatus(),
                pageable
        );
    }

    @Autowired
    private ContributionRecordRepository contributionRecordRepository;

    public Page<ContributionRecord> searchContributionRecords(SearchContributionRecordCriteriaDTO criteria, Pageable pageable) {
        LocalDateTime fromDateTime = criteria.getFromDate() != null
                ? criteria.getFromDate().atStartOfDay()
                : null;

        LocalDateTime toDateTime = criteria.getToDate() != null
                ? criteria.getToDate().atTime(LocalTime.MAX) // 23:59:59.999999999
                : null;

        return contributionRecordRepository.findContributionRecordsBySearchParams(
                criteria.getId(),
                criteria.getContributionId(),
                criteria.getUserId(),
                criteria.getMinAmount(),
                criteria.getMaxAmount(),
                fromDateTime,
                toDateTime,
                criteria.getApproved(),
                pageable
        );
    }

}