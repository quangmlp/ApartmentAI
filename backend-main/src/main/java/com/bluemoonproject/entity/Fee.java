package com.bluemoonproject.entity;

import com.bluemoonproject.enums.FeeStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="fees")
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//

    private String roomNumber;
    private String description;

    private Double amount;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private FeeStatus status;

}
