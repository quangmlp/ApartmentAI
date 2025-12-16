package com.bluemoonproject.configuration;

import com.bluemoonproject.entity.ParkingLot;
import com.bluemoonproject.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParkingLotInitializer implements ApplicationRunner {

    private final ParkingLotRepository parkingLotRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (parkingLotRepository.count() == 0) {
            List<ParkingLot> lots = ParkingLot.autoGenerateLots();
            parkingLotRepository.saveAll(lots);
        }
    }
}
