package com.bluemoonproject.validator;
import com.bluemoonproject.validator.VehicleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VehicleValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVehicle {
    String message() default "Invalid license plate format for the given vehicle type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
