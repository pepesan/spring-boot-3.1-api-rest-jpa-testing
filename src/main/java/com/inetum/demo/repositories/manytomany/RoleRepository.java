package com.inetum.demo.repositories.manytomany;

import com.inetum.demo.domain.manytomany.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
