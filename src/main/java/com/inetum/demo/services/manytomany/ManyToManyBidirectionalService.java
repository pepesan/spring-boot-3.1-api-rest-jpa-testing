package com.inetum.demo.services.manytomany;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
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
        this.userRepository.save(user);
        return this.roleRepository.findAll();
    }

    public List<Role> listadoRoles() {
        return this.roleRepository.findAll();
    }
}
