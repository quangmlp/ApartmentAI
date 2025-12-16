package com.bluemoonproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import com.bluemoonproject.validator.ValidVehicle;
@Entity
@ValidVehicle
@Data
@Table(name = "vehicles")
public class Vehicle {

    public enum Type {
        MOTORBIKE,
        CAR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private Type type;
}
