package com.bluemoonproject.repository;
import com.bluemoonproject.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Guest findByEmail(String email);
}//
