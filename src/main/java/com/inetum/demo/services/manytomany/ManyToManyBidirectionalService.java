package com.inetum.demo.services.manytomany;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.dtos.manytomany.RoleWithUsersDto;
import com.inetum.demo.repositories.manytomany.RoleRepository;
import com.inetum.demo.repositories.manytomany.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ManyToManyBidirectionalService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Autowired
    ManyToManyBidirectionalService(
        UserRepository userRepository,
        RoleRepository roleRepository
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void deleteAll() {
        // ejecuta directamente el DELETE en la tabla de relación
        userRepository.deleteAllUserRoles();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        // Hibernate ejecuta los INSERT antes que los DELETE al hacer flush; sin esto, volver a crear
        // un rol con el mismo name chocaría con la restricción unique antes de borrar el antiguo.
        roleRepository.flush();
    }


    public List<User> listado() {
        return this.userRepository.findAll();
    }

    @Transactional
    public List<User> doSomething() {
        this.deleteAll();
        User user = new User();
        user.setFirstName("David");
        this.userRepository.save(user);
        Role role = new Role ();
        role.setName("Admin");
        this.roleRepository.save(role);
        user.getRoles().add(role);
        // User David - Admin
        this.userRepository.save(user);
        user = new User();
        user.setFirstName("Javier");
        this.userRepository.save(user);
        user.getRoles().add(role);
        // User Javier - Admin
        // this.userRepository.save(user);
        role = new Role ();
        role.setName("User");
        this.roleRepository.save(role);
        user.getRoles().add(role);
        // User Javier - User
        this.userRepository.save(user);

        return this.userRepository.findAll();
    }

    @Transactional
    public List<Role> doSomethingRoles() {
        this.deleteAll();
        User user = new User();
        user.setFirstName("David");
        this.userRepository.save(user);
        Role role = new Role ();
        role.setName("Admin");
        this.roleRepository.save(role);
        user.getRoles().add(role);
        // Relación David - Admin
        this.userRepository.save(user);
        user = new User();
        user.setFirstName("Javier");
        this.userRepository.save(user);
        user.getRoles().add(role);
        // Relación Javier - Admin
        this.userRepository.save(user);
        role = new Role ();
        role.setName("User");
        this.roleRepository.save(role);
        user.getRoles().add(role);
        // Relación Javier - User
        this.userRepository.save(user);

        return this.roleRepository.findAll();
    }

    public List<Role> listadoRoles() {
        return this.roleRepository.findAll();
    }
    /** Lista los roles con sus usuarios asignados, ya convertidos a DTO. */
    public List<RoleWithUsersDto> listadoRolesConUsuarios() {
        return this.roleRepository.findAll().stream()
                .map(RoleWithUsersDto::from)
                .toList();
    }
    public List<User> listadoUsers() {
        return this.userRepository.findAll();
    }
}
