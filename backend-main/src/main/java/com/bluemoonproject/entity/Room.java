package com.bluemoonproject.entity;

import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //
    private String roomNumber;
    private Integer floor;
    private Long peopleCount; //peopleCount ở đây tức là số tài khoản user, resident có thể không có tài khoản
    private Long residentCount;

    // Diện tích căn hộ (m²)
    private Double area;

    // Loại căn hộ (KIOT, STANDARD, PENHOUSE)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    // Danh sách người trong phòng (tên và tuổi)
    @ElementCollection
    @CollectionTable(name = "room_residents", joinColumns = @JoinColumn(name = "room_id"))
    private Set<ResidentInfo> residents = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "room_users", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "user_id")
    Set<Long> userIds;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_fees", joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"))
    @Column(name = "fee_id")
    Set<Long> feeIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "room_vehicles", joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"))
    @Column(name = "vehicle_id")
    Set<Long> vehicleIds = new HashSet<>();
}