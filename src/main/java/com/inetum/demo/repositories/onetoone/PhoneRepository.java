package com.inetum.demo.repositories.onetoone;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.domain.onetoone.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    // Métodos mediante Query (hql)
    @Query("SELECT p from Phone p where p.details.provider = :name")
    List<Alumno> findPhonesByDetails_Provider(String name);
}
