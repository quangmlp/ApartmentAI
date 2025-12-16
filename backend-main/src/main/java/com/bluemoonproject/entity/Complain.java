package com.bluemoonproject.entity;

import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.enums.Priority;
import com.bluemoonproject.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Complain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String response;

    private LocalDate createdAt;

    @Lob
    private String attachment;


    @Enumerated(EnumType.STRING)
    private ComplainTopic type;

    @Enumerated(EnumType.STRING)
    private Priority prior;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="complain_users",joinColumns = @JoinColumn(name="complain_id"))
    @Column(name="user_id")
    Set<Long> userIds;
}
