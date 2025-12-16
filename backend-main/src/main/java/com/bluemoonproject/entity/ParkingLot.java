package com.bluemoonproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "parking_lots")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lotNumber;

    @Enumerated(EnumType.STRING)
    private Vehicle.Type type;

    private boolean occupied = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="parkinglot_vehicles",joinColumns = @JoinColumn(name="parking_lots_id"))
    @Column(name="vehicles_id")
    Set<Long> vehicleIds;

    public static List<ParkingLot> autoGenerateLots() {
        List<ParkingLot> lots = new ArrayList<>();
        int totalArea = 450;
        int carLotArea = 15;       // approx
        int bikeLotArea = 3;       // approx

        int numCarLots = totalArea / (carLotArea + 4 * bikeLotArea); // ~15 cars
        int numBikeLots = (totalArea - numCarLots * carLotArea) / bikeLotArea;

        for (int i = 1; i <= numCarLots; i++) {
            lots.add(new ParkingLot("C" + i, Vehicle.Type.CAR, false));
        }
        for (int i = 1; i <= numBikeLots; i++) {
            lots.add(new ParkingLot("M" + i, Vehicle.Type.MOTORBIKE, false));
        }

        return lots;
    }

    public ParkingLot() {}

    public ParkingLot(String lotNumber, Vehicle.Type type, boolean occupied) {
        this.lotNumber = lotNumber;
        this.type = type;
        this.occupied = occupied;
    }
}
