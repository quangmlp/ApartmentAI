package com.bluemoonproject.repository;

import com.bluemoonproject.entity.ParkingLot;
import com.bluemoonproject.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    Optional<ParkingLot> findByLotNumber(String lotNumber);

    long countByOccupied(boolean occupied);
    long countByOccupiedAndType(boolean occupied, Vehicle.Type type);

    boolean existsByLotNumber(String lotNumber);


    List<ParkingLot> findByTypeAndOccupiedFalse(Vehicle.Type type);

    @Query("SELECT p FROM ParkingLot p JOIN p.vehicleIds v WHERE v IN :vehicleIds")
    List<ParkingLot> findByVehicleIds(@Param("vehicleIds") Set<Long> vehicleIds);
}

