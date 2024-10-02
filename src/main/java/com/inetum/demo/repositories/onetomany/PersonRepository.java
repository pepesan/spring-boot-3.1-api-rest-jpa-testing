package com.inetum.demo.repositories.onetomany;

import com.inetum.demo.domain.onetomany.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
