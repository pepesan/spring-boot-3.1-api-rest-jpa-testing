package com.inetum.demo.repositories.onetoone;

import com.inetum.demo.domain.onetoone.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
