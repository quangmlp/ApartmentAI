package com.bluemoonproject.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SearchContributionRecordCriteriaDTO {

    private Long id;                     // ID của bản ghi đóng góp

    private Long userId;                // ID người đóng góp

    private Long contributionId;        // ID của khoản đóng góp (khóa ngoại)

    private Double minAmount;
    private Double maxAmount;              // Số tiền đóng góp
    // Ngày đóng góp

    private LocalDate fromDate;   // Ngày bắt đầu lọc
    private LocalDate toDate;     // Ngày kết thúc lọc

    private Boolean approved;           // Trạng thái duyệt
}