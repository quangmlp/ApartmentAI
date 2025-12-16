package com.bluemoonproject.validator;

import com.bluemoonproject.entity.Vehicle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.bluemoonproject.validator.ValidVehicle;
public class VehicleValidator implements ConstraintValidator<ValidVehicle, Vehicle> {

    private static final String CAR_PATTERN = "^(?:[1-9][1-9])[A-Z]-\\d{3}\\.\\d{2}$";
    private static final String MOTORBIKE_PATTERN = "^\\d{2}[A-Z]\\d-\\d{3}\\.\\d{2}$";


    @Override
    public boolean isValid(Vehicle vehicle, ConstraintValidatorContext context) {
        if (vehicle.getLicensePlate() == null || vehicle.getType() == null) {
            return true; // Let @NotNull or other validations handle this
        }

        String plate = vehicle.getLicensePlate();
        boolean valid;

        if (vehicle.getType() == Vehicle.Type.CAR) {
            valid = plate.matches(CAR_PATTERN);
        } else if (vehicle.getType() == Vehicle.Type.MOTORBIKE) {
            valid = plate.matches(MOTORBIKE_PATTERN);
        } else {
            valid = false;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid license plate format for " + vehicle.getType())
                    .addPropertyNode("licensePlate")
                    .addConstraintViolation();
        }

        return valid;
    }
}
