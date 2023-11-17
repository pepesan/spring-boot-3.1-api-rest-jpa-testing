package com.inetum.demo.services.manytomany;

import com.inetum.demo.domain.manytomany.Role;
import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.repositories.manytomany.RoleRepository;
import com.inetum.demo.repositories.manytomany.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        // eliminate all the references to the roles
        this.userRepository.findAll().stream()
                .forEach(user -> {
                    user.getRoles().clear();
                    this.userRepository.save(user);
                });
        this.roleRepository.findAll().stream()
                .forEach(role -> {
                    role.getUsers().clear();
                    this.roleRepository.save(role);
                });
    }

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
}
