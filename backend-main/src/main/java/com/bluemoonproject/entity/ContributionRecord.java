package com.bluemoonproject.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contribution_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khoản đóng góp nào
    @ManyToOne
    @JoinColumn(name = "contribution_id", nullable = false)
    private Contribution contribution;


    @Column(nullable = false)
    private Long userId;

    // Số tiền đóng góp
    @Column(nullable = false)
    private Double amount;

    // Ngày đóng
    private LocalDateTime contributedAt;

    private boolean approved = false;
}
