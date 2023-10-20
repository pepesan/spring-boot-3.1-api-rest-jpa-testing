package com.inetum.demo.repositories.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
