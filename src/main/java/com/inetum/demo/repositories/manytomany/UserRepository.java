package com.inetum.demo.repositories.manytomany;

import com.inetum.demo.domain.manytomany.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Borra todas las entradas de la tabla user_roles (join table).
     */
    @Modifying
    @Transactional  // si no hereda transacción de servicio
    @Query(value = "DELETE FROM users_roles", nativeQuery = true)
    void deleteAllUserRoles();
}
