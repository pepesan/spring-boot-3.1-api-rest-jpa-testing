package com.inetum.demo.repositories.onetoone;

import com.inetum.demo.domain.onetoone.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressesRepository extends JpaRepository<Address, Long> {
}
