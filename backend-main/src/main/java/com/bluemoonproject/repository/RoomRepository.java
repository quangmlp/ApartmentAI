package com.bluemoonproject.repository;

import com.bluemoonproject.entity.Room;
import com.bluemoonproject.enums.RoomStatus;
import com.bluemoonproject.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);
    //
    List<Room> findByUserIdsContaining(Long userId);
    //
    boolean existsByRoomNumber(String roomNumber);



    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByStatusAndRoomType(RoomStatus status, RoomType roomType);

    @Query("SELECT r FROM Room r WHERE " +
            "(LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :roomNumber, '%')) OR :roomNumber IS NULL) AND " +
            "(r.floor = :floor OR :floor IS NULL) AND " +
            "(r.peopleCount = :peopleCount OR :peopleCount IS NULL) AND " +
            "(r.residentCount = :residentCount OR :residentCount IS NULL) AND " +
            "(r.area = :area OR :area IS NULL) AND " +
            "(r.roomType = :roomType OR :roomType IS NULL) AND " +
            "(r.status = :status OR :status IS NULL)")
    Page<Room> findRoomsBySearchParams(String roomNumber, Integer floor, Long peopleCount, Long residentCount,
                                       Double area, RoomType roomType, RoomStatus status, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE :userId MEMBER OF r.userIds")
    List<Room> findRoomsByUserId(@Param("userId") Long userId);


}