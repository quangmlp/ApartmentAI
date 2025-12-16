package com.bluemoonproject.repository;

import com.bluemoonproject.entity.Complain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplainRepository extends JpaRepository<Complain, Long> {
}
