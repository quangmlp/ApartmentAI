package com.bluemoonproject.service;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Guest;
import com.bluemoonproject.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GuestService {
    //
    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public Guest createAccount(String username, String password, String firstName, String lastName, LocalDate dob, String email) {
        Guest guest = new Guest(username, password, firstName, lastName, dob, email);
        return guestRepository.save(guest);
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }
}
