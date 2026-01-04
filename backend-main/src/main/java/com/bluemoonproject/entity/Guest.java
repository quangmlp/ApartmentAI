package com.bluemoonproject.entity;

import com.bluemoonproject.validator.DobConstraint;
import com.bluemoonproject.validator.PasswordConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //
    @Size(min=3,message ="USERNAME_INVALID")
    @NotNull
    private String username;

    @PasswordConstraint
    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @DobConstraint(min=18,message = "INVALID_DOB")
    private LocalDate dob;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    // Constructors, Getters, and Setters
    public Guest() {}

    public Guest(String username, String password, String firstName, String lastName, LocalDate dob, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
    }

    // Getters and Setters...
}
