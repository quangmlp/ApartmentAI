package com.bluemoonproject.service;

import com.bluemoonproject.entity.Complain;
import com.bluemoonproject.enums.ComplainTopic;
import com.bluemoonproject.enums.Priority;
import com.bluemoonproject.enums.Status;
import com.bluemoonproject.repository.ComplainRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AdminComplainService {

    private final ComplainRepository complainRepository;

    public List<Complain> getAllComplains(Status status, Priority priority, ComplainTopic type) {
        return complainRepository.findAll().stream()
                .filter(c -> status == null || c.getStatus() == status)
                .filter(c -> priority == null || c.getPrior() == priority)
                .filter(c -> type == null || c.getType() == type)
                .collect(Collectors.toList());
    }
    // Admin service to resolve a complaint and set priority and status
    public Complain resolveComplain(Long complainId, String response, Priority priority, Status status) {
        Complain complain = complainRepository.findById(complainId).orElseThrow(() -> new RuntimeException("Complain not found"));
        complain.setResponse(response);
        complain.setPrior(priority);  // Change the priority
        complain.setStatus(status);   // Change the status
        return complainRepository.save(complain);
    }

    // Admin service to delete a complaint
    public void deleteComplain(Long complainId) {
        complainRepository.deleteById(complainId);
    }

    // Admin service to get all complaints
    public List<Complain> getAllComplains() {
        return complainRepository.findAll();
    }
}

