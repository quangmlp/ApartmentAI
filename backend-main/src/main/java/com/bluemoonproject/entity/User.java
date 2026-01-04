package com.bluemoonproject.entity;

import com.bluemoonproject.enums.ResidencyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //
    String username;
    String password;

    String firstName;
    String lastName;
    LocalDate dob;

    @Column(unique = true, nullable = false)
    String email;

    @ManyToMany
    Set<Role> roles;

    @Enumerated(EnumType.STRING)
    private ResidencyStatus  residencyStatus = ResidencyStatus.THUONG_TRU;


}