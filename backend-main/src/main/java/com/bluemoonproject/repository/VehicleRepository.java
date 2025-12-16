package com.bluemoonproject.repository;

import com.bluemoonproject.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByIdIn(Collection<Long> ids);
    List<Vehicle> findByIdIn(Set<Long> ids);
}

