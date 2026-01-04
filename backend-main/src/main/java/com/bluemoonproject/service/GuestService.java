package com.bluemoonproject.service;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Guest;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.repository.GuestRepository;
import com.bluemoonproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GuestService {
    //
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    public GuestService(GuestRepository guestRepository, UserRepository userRepository) {
        this.guestRepository = guestRepository;
        this.userRepository = userRepository;
    }

    public Guest createAccount(String username, String password, String firstName, String lastName, LocalDate dob, String email) {
        // Check if email already exists in Guest table
        Guest existingGuest = guestRepository.findByEmail(email);
        if (existingGuest != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Check if email already exists in User table
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        Guest guest = new Guest(username, password, firstName, lastName, dob, email);
        return guestRepository.save(guest);
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }
}
