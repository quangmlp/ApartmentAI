package com.bluemoonproject.controller;

import com.bluemoonproject.dto.request.SearchContributionRecordCriteriaDTO;
import com.bluemoonproject.dto.request.SearchFeeCriteriaDTO;
import com.bluemoonproject.dto.request.SearchRoomCriteriaDTO;
import com.bluemoonproject.dto.request.SearchUserCriteriaDTO;
import com.bluemoonproject.entity.ContributionRecord;
import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.entity.User;
import com.bluemoonproject.service.SearchBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchBarController {

    @Autowired
    private SearchBarService searchBarService;

    // Search User - GET mapping, sử dụng query parameters
    @PostMapping("/users")
    public Page<User> searchUsers(@RequestBody SearchUserCriteriaDTO criteria, Pageable pageable) {
        return searchBarService.searchUsers(
                criteria.getUsername(),
                criteria.getFirstName(),
                criteria.getLastName(),
                criteria.getEmail(),
                criteria.getResidencyStatus(),
                pageable
        );
    }

    // Search Room
    @PostMapping("/rooms")
    public Page<Room> searchRooms(@RequestBody SearchRoomCriteriaDTO searchRoomCriteria, Pageable pageable) {
        return searchBarService.searchRooms(
                searchRoomCriteria.getRoomNumber(),
                searchRoomCriteria.getFloor(),
                searchRoomCriteria.getPeopleCount(),
                searchRoomCriteria.getResidentCount(),
                searchRoomCriteria.getArea(),
                searchRoomCriteria.getRoomType(),
                searchRoomCriteria.getStatus(),
                pageable
        );
    }

    // Search Fee
    @PostMapping("/fees")
    public Page<Fee> searchFees(@RequestBody SearchFeeCriteriaDTO feeSearchCriteria, Pageable pageable) {
        return searchBarService.searchFees(feeSearchCriteria, pageable);
    }


    @PostMapping("/contributionRecords")
    public Page<ContributionRecord> searchContributionRecords(
            @RequestBody SearchContributionRecordCriteriaDTO criteria,
            Pageable pageable) {
        return searchBarService.searchContributionRecords(criteria, pageable);
    }



}