package com.inetum.demo.repositories.onetomany;

import com.inetum.demo.domain.onetomany.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Long> {
}
