package com.inetum.demo.repositories.onetoone;

import com.inetum.demo.domain.onetoone.PhoneDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDetailsRepository extends JpaRepository<PhoneDetails, Long> {
}
