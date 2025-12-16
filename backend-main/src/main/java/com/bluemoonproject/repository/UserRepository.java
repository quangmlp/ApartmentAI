package com.bluemoonproject.repository;

import com.bluemoonproject.entity.User;
import com.bluemoonproject.enums.ResidencyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    //
    Optional<User> findByEmail(String email);

    // Có cần thêm tìm findByResidencyStatus không ?
    List<User> findByResidencyStatus(ResidencyStatus residencyStatus);

    List<User> findByRoles_Name(String adminRoles);

    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:residencyStatus IS NULL OR u.residencyStatus = :residencyStatus)")
    Page<User> findUsersBySearchParams(
            @Param("username") String username,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("residencyStatus") ResidencyStatus residencyStatus,
            Pageable pageable);
}