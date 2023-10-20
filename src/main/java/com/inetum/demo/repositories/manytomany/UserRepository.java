package com.inetum.demo.repositories.manytomany;

import com.inetum.demo.domain.manytomany.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
